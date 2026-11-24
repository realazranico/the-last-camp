# The Last Camp

A Philippine folklore survival game for phone, PC browser and Java desktop, with online multiplayer for up to 10 players.

**Play online:** https://the-last-camp.onrender.com (the free server may need up to a minute to wake up)
**Repository:** https://github.com/realazranico/the-last-camp

## The story
A group of teenagers goes camping during their school break. They take an unfamiliar road deep into a forest and get lost: no phone signal, the vehicle cannot go on, and night is falling. At an old campsite they learn that something mysterious lives in the forest, that it fears fire, and that an abandoned radio could call for rescue, but it needs batteries. Find wood, find all the batteries, and keep the fire alive. Will they find their way home, or will the forest become their last camp?

## Game features
- **User accounts:** register, view your profile, edit it, delete it, log in and log out. Accounts are saved and stay after the game is closed and reopened.
- **Main menu:** Solo, Solo + Bots, Multiplayer, Continue, Profile, How to Play, Settings and Logout.
- **Your own wood and batteries (multiplayer):** what you pick up belongs to you and is not shared. The campfire, the radio and the shelters are shared, so friends fill them with their own items.
- **Levels make the radio harder:** the radio needs 8 batteries at level 1, and 2 more for every level you gain, up to 22 batteries at level 8.
- **Playable game:** survive one night in a forest. Gather wood and batteries, keep the campfire alive, build shelters, fight or avoid 4 monsters, and repair the radio.
- **10 characters** and **up to 10 players** online. A player who dies becomes a spectator, and the game continues until it is finished.
- **The Duwende and the punso:** step on a brown mound and a Duwende asks a hard question. A right answer heals you, a wrong answer curses you.
- **Rules and instructions:** a HOW TO PLAY guide (it opens by itself the first time), plus a monster introduction.
- **Score, level and achievements:** every game gives a score and XP. Your level goes up and you unlock 8 achievements.
- **Game over, restart and completion:** an end screen with score, TRY AGAIN and MAIN MENU. You complete the game by surviving until 6:00 AM or repairing the radio.
- **Input validation and error handling:** every form checks its input and shows a clear message. Storage, file and network errors are handled.

## How to run

### Option 1: online (nothing to install)
Open https://the-last-camp.onrender.com on any phone or computer.

### Option 2: browser version on your own computer
1. Install a JDK (version 8 or newer).
2. In this folder run:
   ```
   javac --release 8 TheLastCampServer.java
   java TheLastCampServer
   ```
3. Open `http://localhost:5050` in a browser. Other devices on the same Wi-Fi open `http://YOUR-PC-IP:5050` (the server prints this address).

You can also open `LastCamp.html` directly for solo play. Multiplayer needs the server.

### Option 3: Java desktop version
1. Install a JDK (version 8 or newer).
2. Copy the pictures from `assets/` into the `assets` folder next to the game.
3. Compile and run:
   ```
   javac --release 8 TheLastCamp.java Sprites.java
   java TheLastCamp
   ```
4. For multiplayer, start `TheLastCampServer` and put its address in `assets/server.txt`.

### Playing multiplayer
The host clicks MULTIPLAYER, then HOST (2 to 10 players), and shares the 4-letter code. Everyone else clicks MULTIPLAYER, types the code under JOIN and presses JOIN. To play over the internet, use the online link, or run `StartServer.bat`, which starts the server and a Cloudflare tunnel.

## Dependencies
| What | Needed for | Notes |
|---|---|---|
| A modern web browser (Chrome, Edge, Firefox, Safari) | Browser version | No libraries, no internet needed after the page loads |
| JDK 8 or newer | Server and Java desktop version | Only the Java standard library is used |
| `cloudflared` | Optional, only for `StartServer.bat` | `winget install Cloudflare.cloudflared --source winget` |
| Node.js | Optional, only to run the tests | `node tests/account-tests.js` |

## Controls
| Action | PC | Phone |
|---|---|---|
| Move | W A S D or arrow keys | Drag on the left side |
| Slash | Space or J | SLASH button |
| Dash | Shift or K | DASH button (the bar above it shows the recharge) |
| Use (chop, grab, feed fire, fix radio) | E | USE button |
| Build a shelter | B | BUILD button |
| Torch | F | In the BAG |
| Bag / Map / Settings | I / M / Esc | BAG / MAP / SET buttons |

## Accounts, score and level
- **Register:** username 3-12 letters, numbers or `_`; display name 1-12 letters or numbers; password 4-20 characters.
- **Passwords** are never stored as plain text. Each one is stored as a salted SHA-256 hash.
- **Browser accounts** are saved in the browser's local storage. **Java accounts** are saved in files in your home folder. They are separate from each other.
- **Score of a game** = seconds survived (up to 300) + 300 for a win + 2 per radio percent + 50 per right Duwende answer + 5 per item gathered.
- **Level** = 1 + XP / 600, and XP is the sum of your scores. **Batteries needed for the radio** = 8 + 2 for every level above 1 (maximum 22). In multiplayer the host's level decides.
- **Achievements:** FIRST NIGHT, SUNRISE, RESCUED, DUWENDE FRIEND, DUWENDE SCHOLAR, GATHERER, TEAM PLAYER, SURVIVOR.

## Project structure
```
LastCamp.html            browser game (one file: pictures, sounds and code are inside)
TheLastCamp.java         Java desktop game
TheLastCampServer.java   relay server for multiplayer and web page server
Embedart.java            tool: packs assets/*.png into Sprites.java and LastCamp.html
Sprites.java             the 15 pictures stored as text
assets/                  the 15 PNG pictures
Dockerfile               recipe to host the server online (Render)
StartServer.bat          starts the server and a tunnel on Windows
docs/DOCUMENTATION.md    full project documentation
tests/account-tests.js   automated tests for the account system
CONTRIBUTING.md          team workflow and commit guide
```

## Documentation
- [Full documentation](docs/DOCUMENTATION.md): requirements checklist, user manual, account design, architecture, testing.
- [Contributing and commit guide](CONTRIBUTING.md).

## Team
| Member | Role | Responsibility |
|---|---|---|
| Sawali, Mia Cassey F. | Project Leader / Game Designer | Project planning, game concept, task coordination |
| Orozco, Paul Angelo | Java / OOP Developer | Java classes, OOP structure, core game logic |
| Sawali, Mia Cassey F. | Gameplay Developer | Game mechanics, player controls, scoring and interactions |
| Adarlo, Marinela | Account Management Developer | Registration, login, CRUD, local file handling |
| Paz, Claud Alfred | UI/UX Designer | Game interface, menus, visual design, player experience |
| Dela Roca, Elizabeth Rose Aivy | QA Tester / Documentation Lead | Functional testing, test cases, documentation |

See [CONTRIBUTING.md](CONTRIBUTING.md) for the team workflow and the commit guide.

Repository link for the final documentation: https://github.com/realazranico/the-last-camp
