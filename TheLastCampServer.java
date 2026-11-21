import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.*;

public class TheLastCampServer {

    interface Conn {
        void send(String s);
        void close();
    }

    static class Room {
        String code;
        Conn host;
        Map<Integer, Conn> cl = new ConcurrentHashMap<>();
        int next = 1, max = 4;
    }

    static class Peer {
        Conn c;
        Room room;
        int id = -1;
    }

    static final Map<String, Room> rooms = new ConcurrentHashMap<>();
    static final Random R = new Random();

    static String newCode() {
        String a = "ABCDEFGHJKLMNPQRSTUVWXYZ";
        while (true) {
            StringBuilder b = new StringBuilder();
            for (int i = 0; i < 4; i++) b.append(a.charAt(R.nextInt(a.length())));
            if (!rooms.containsKey(b.toString())) return b.toString();
        }
    }

    static void onLine(Peer p, String l) {
        try {
            if (p.room == null) {
                if (l.startsWith("HOST")) {
                    Room r = new Room();
                    try {
                        r.max = Math.max(2, Math.min(10, Integer.parseInt(l.substring(4).trim())));
                    } catch (Exception e) { }
                    r.code = newCode();
                    r.host = p.c;
                    p.room = r;
                    p.id = 0;
                    rooms.put(r.code, r);
                    p.c.send("CODE " + r.code);
                    System.out.println("room " + r.code + " opened (max " + r.max + ")");
                } else if (l.startsWith("JOIN ")) {
                    Room r = rooms.get(l.substring(5).trim().toUpperCase());
                    if (r == null) {
                        p.c.send("ERR no such room");
                        return;
                    }
                    synchronized (r) {
                        if (r.cl.size() + 1 >= r.max) {
                            p.c.send("ERR room is full");
                            return;
                        }
                        int nid = 1;
                        while (r.cl.containsKey(nid)) nid++;
                        p.id = nid;
                        p.room = r;
                        r.cl.put(p.id, p.c);
                    }
                    p.c.send("I " + p.id);
                    r.host.send(p.id + " JOINED");
                    System.out.println("room " + r.code + ": player " + p.id + " joined");
                }
            } else if (p.id == 0) {
                Room r = p.room;
                int sp = l.indexOf(' ');
                if (sp < 0) return;
                String t = l.substring(0, sp), m = l.substring(sp + 1);
                if (t.equals("*")) for (Conn c : r.cl.values()) c.send(m);
                else if (t.startsWith("*-")) {
                    int ex = Integer.parseInt(t.substring(2));
                    for (Map.Entry<Integer, Conn> e : r.cl.entrySet()) if (e.getKey() != ex) e.getValue().send(m);
                } else {
                    Conn c = r.cl.get(Integer.parseInt(t));
                    if (c != null) c.send(m);
                }
            } else p.room.host.send(p.id + " " + l);
        } catch (Exception e) { }
    }

    static void onClose(Peer p) {
        Room r = p.room;
        if (r == null) return;
        if (p.id == 0) {
            rooms.remove(r.code);
            System.out.println("room " + r.code + " closed");
            for (Conn c : r.cl.values()) {
                c.send("ERR host left");
                c.close();
            }
        } else {
            r.cl.remove(p.id);
            r.host.send(p.id + " LEFT");
        }
    }

    static class TcpConn implements Conn {
        final Socket s;
        final PrintWriter w;
        TcpConn(Socket s) throws IOException {
            this.s = s;
            w = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8), true);
        }
        public synchronized void send(String m) {
            w.println(m);
        }
        public void close() {
            try {
                s.close();
            } catch (Exception e) { }
        }
    }

    static class WsConn implements Conn {
        final Socket s;
        final OutputStream o;
        WsConn(Socket s) throws IOException {
            this.s = s;
            o = s.getOutputStream();
        }
        public synchronized void send(String m) {
            frame(0x81, m.getBytes(StandardCharsets.UTF_8));
        }
        synchronized void frame(int op, byte[] b) {
            try {
                o.write(op);
                int n = b.length;
                if (n < 126) o.write(n);
                else if (n < 65536) {
                    o.write(126);
                    o.write(n >> 8);
                    o.write(n);
                } else {
                    o.write(127);
                    for (int i = 7; i >= 0; i--) o.write((int) (((long) n >> (8 * i)) & 255));
                }
                o.write(b);
                o.flush();
            } catch (Exception e) { }
        }
        public void close() {
            try {
                s.close();
            } catch (Exception e) { }
        }
    }

    static void serveFile(Socket s, String req) throws IOException {
        String[] pr = req.split(" ");
        String path = pr.length > 1 ? pr[1] : "/";
        int q = path.indexOf('?');
        if (q >= 0) path = path.substring(0, q);
        if (path.equals("/")) path = !new File("index.html").isFile() && new File("LastCamp.html").isFile() ? "/LastCamp.html" : "/index.html";
        File base = new File(".").getCanonicalFile(), f = new File(base, path.substring(1)).getCanonicalFile();
        OutputStream o = s.getOutputStream();
        String n = f.getName().toLowerCase();
        boolean okExt = n.endsWith(".html") || n.endsWith(".png") || n.endsWith(".ttf") || n.endsWith(".txt");
        if (!f.getPath().startsWith(base.getPath()) || !f.isFile() || !okExt) o.write("HTTP/1.1 404 Not Found\r\nConnection: close\r\n\r\nnot found".getBytes());
        else {
            byte[] d = java.nio.file.Files.readAllBytes(f.toPath());
            String ct = n.endsWith(".html") ? "text/html" : n.endsWith(".png") ? "image/png" : n.endsWith(".ttf") ? "font/ttf" : "text/plain";
            o.write(("HTTP/1.1 200 OK\r\nContent-Type: " + ct + "\r\nContent-Length: " + d.length + "\r\nConnection: close\r\n\r\n").getBytes());
            o.write(d);
        }
        o.flush();
        s.close();
    }

    static void tcpClient(Socket s) {
        Peer p = new Peer();
        try {
            p.c = new TcpConn(s);
            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8));
            String l;
            while ((l = br.readLine()) != null) onLine(p, l);
        } catch (Exception e) { }
        onClose(p);
    }

    static void wsClient(Socket s) {
        Peer p = new Peer();
        try {
            DataInputStream in = new DataInputStream(new BufferedInputStream(s.getInputStream()));
            StringBuilder h = new StringBuilder();
            int b;
            while ((b = in.read()) >= 0) {
                h.append((char) b);
                if (h.length() >= 4 && h.substring(h.length() - 4).equals("\r\n\r\n")) break;
                if (h.length() > 8192) return;
            }
            String key = null;
            for (String line : h.toString().split("\r\n")) if (line.toLowerCase().startsWith("sec-websocket-key:")) key = line.substring(18).trim();
            if (key == null) {
                serveFile(s, h.toString().split("\r\n")[0]);
                return;
            }
            String acc = Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-1").digest((key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes(StandardCharsets.UTF_8)));
            s.getOutputStream().write(("HTTP/1.1 101 Switching Protocols\r\nUpgrade: websocket\r\nConnection: Upgrade\r\nSec-WebSocket-Accept: " + acc + "\r\n\r\n").getBytes());
            s.getOutputStream().flush();
            WsConn c = new WsConn(s);
            p.c = c;
            while (true) {
                int b0 = in.read();
                if (b0 < 0) break;
                int op = b0 & 15, b1 = in.read();
                boolean mask = (b1 & 128) != 0;
                long len = b1 & 127;
                if (len == 126) len = in.readUnsignedShort();
                else if (len == 127) len = in.readLong();
                byte[] mk = new byte[4];
                if (mask) in.readFully(mk);
                byte[] pl = new byte[(int) len];
                in.readFully(pl);
                if (mask) for (int i = 0; i < pl.length; i++) pl[i] ^= mk[i & 3];
                if (op == 8) break;
                if (op == 9) c.frame(0x8A, pl);
                else if (op == 1) onLine(p, new String(pl, StandardCharsets.UTF_8));
            }
        } catch (Exception e) { }
        onClose(p);
        try {
            s.close();
        } catch (Exception e) { }
    }

    static void listen(int port, boolean ws) throws IOException {
        ServerSocket ss = new ServerSocket(port);
        Thread t = new Thread(() -> {
            while (true) {
                try {
                    Socket s = ss.accept();
                    s.setTcpNoDelay(true);
                    Thread c = new Thread(() -> {
                        if (ws) wsClient(s);
                        else tcpClient(s);
                    });
                    c.setDaemon(true);
                    c.start();
                } catch (Exception e) { }
            }
        }, "accept-" + port);
        t.start();
    }

    public static void main(String[] a) throws Exception {
        String envPort = System.getenv("PORT");
        int wsPort = a.length > 0 ? Integer.parseInt(a[0]) : envPort != null ? Integer.parseInt(envPort) : 5050, tcpPort = a.length > 1 ? Integer.parseInt(a[1]) : 5051;
        listen(wsPort, true);
        listen(tcpPort, false);
        System.out.println("The Last Camp relay running. Browsers: ws port " + wsPort + " | Java game: tcp port " + tcpPort);
        try {
            for (NetworkInterface n : Collections.list(NetworkInterface.getNetworkInterfaces())) for (InetAddress i : Collections.list(n.getInetAddresses())) if (i instanceof Inet4Address && !i.isLoopbackAddress()) System.out.println("  Phones/browsers open:  http://" + i.getHostAddress() + ":" + wsPort + "   |  Java players: put " + i.getHostAddress() + " in assets/server.txt");
        } catch (Exception e) { }
    }

}