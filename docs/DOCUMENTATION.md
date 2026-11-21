# The Last Camp: Project Documentation

Repository: https://github.com/realazranico/the-last-camp
Online game: https://the-last-camp.onrender.com

| | |
|---|---|
| Course / subject | [fill in] |
| Section / group | [fill in] |
| Members | Sawali, Mia Cassey F; Orozco, Paul Angelo; Sawali, Mia Cassey; Adarlo, Marinela; Paz, Claud Alfred; Dela Roca, Elizabeth Rose Aivy |
| Date | [fill in] |

## 1. Overview
The Last Camp is a survival game based on Philippine folklore. One night in a haunted forest, players must gather wood and batteries, keep a campfire burning, and repair a radio to call for rescue, while four creatures (Manananggal, Kapre, Tikbalang, Multo) hunt them. A Duwende hiding in a punso (earth mound) asks hard questions. The game runs in a web browser (phone and PC) and as a Java desktop program. Both versions can share one online multiplayer room of up to 10 players.

## 2. Requirements checklist
### 2.1 User account management (basic CRUD)
| Requirement | How the game does it | Where to see it |
|---|---|---|
| **Create**: register a new player account | CREATE ACCOUNT screen with username, display name, password and repeat password | LOGIN screen > REGISTER |
| **Read**: view player information | PROFILE screen: username, name, level, XP bar, games, wins, best score, longest night, items found, 8 achievements | MAIN MENU > PROFILE |
| **Update**: change player information | EDIT PROFILE screen: change display name and/or password (needs the current password) | PROFILE > EDIT |
| **Delete**: remove an account | DELETE ACCOUNT screen: needs the password, cannot be undone | PROFILE > DELETE |
| **Login and logout** | LOGIN screen; LOGOUT button on the main menu and on the profile; the session is remembered until you log out | LOGIN screen, MAIN MENU > LOGOUT |
| **Accounts stay after closing and reopening** | Browser: saved in local storage. Java: saved in files in the home folder (section 5.2) | Close and reopen, then log in |
| Database optional | Not used. Local storage and Java file handling are used instead | Section 5.2 |

### 2.2 Game features
| Requirement | Where it is |
|---|---|
| Main menu | MAIN MENU: Solo, Solo + Bots, Multiplayer, Continue, Profile or Login, How to Play, Settings, Logout |
| Playable game environment | A 64 x 48 tile forest with trees, items, a campfire, a radio, shelters, monsters and punso |
| Game rules or instructions | HOW TO PLAY (opens automatically the first time), the story intro, the monster introduction, and section 4 of this document |
| Player controls and interactions | Section 4.2. Keyboard, mouse, touch and on-screen buttons |
| Score, progress, level or achievement tracking | Score per game, XP, level, 8 achievements, saved in the account (section 4.4) |
| Game over, restart or completion | End screen: RESCUED / YOU SURVIVED / GAME OVER, score, TRY AGAIN, MAIN MENU |
| Basic input validation and error handling | Section 5.3 |
| Creative extras | Spectator mode, up to 10 players, 10 characters, Duwende quiz and curse, dash energy bar, map, auto-rotate on phones, cross-play between browser and Java |

### 2.3 GitHub repository
| Requirement | Status |
|---|---|
| Complete source code | In the repository (see the README for the file list) |
| README.md | Yes |
| Project description and game features | In README.md and section 1 |
| Instructions for installing and running | In README.md, section "How to run" |
| Required dependencies | In README.md, section "Dependencies" |
| Project documentation | This file |
| Meaningful commit history and member contributions | See CONTRIBUTING.md. Each member commits their own work with their own GitHub account |
| Repository link in the final documentation | At the top of this file |

## 3. System requirements and dependencies
- Browser version: any modern browser. No libraries and no internet needed after the page loads (pictures, sounds and letters are inside `LastCamp.html`).
- Server and Java desktop version: JDK 8 or newer. Only the Java standard library is used.
- Optional: `cloudflared` (tunnel), Node.js (tests), a Render account (free hosting).

## 4. User manual
### 4.1 Starting
1. Open the game and press START on the title screen.
2. On the LOGIN screen, log in, press REGISTER to create an account, or press PLAY AS GUEST. Guests can play but their progress is not saved.
3. After the story intro you reach the MAIN MENU. The first time, HOW TO PLAY and the monster introduction open by themselves.

### 4.2 Controls
| Action | PC | Phone |
|---|---|---|
| Move | W A S D or arrows | Drag on the left side of the screen |
| Slash | Space or J | SLASH |
| Dash | Shift or K | DASH (a bar above it shows the recharge: yellow while charging, green when ready, 2.5 seconds) |
| Use | E | USE |
| Build a shelter (6 wood) | B | BUILD |
| Torch | F | BAG |
| Bag / Map / Settings | I / M / Esc | BAG / MAP / SET |

### 4.3 Rules
- Win by surviving until 6:00 AM (the night lasts 5 minutes) or by putting 8 batteries in the radio.
- You lose when you run out of hearts. In multiplayer a dead player watches the others; the game only ends when the radio is fixed, sunrise comes, or every player is dead.
- Trees give wood and battery piles give batteries. Each wood adds 25 to the campfire, which loses 1.2 per second. Monsters fear fire and shelters.
- Each of the 6 punso asks one question per player. A right answer gives full health and 10 seconds of protection. A wrong answer is a curse: 2 hearts lost over 5 seconds.

### 4.4 Score, level and achievements
- Score of a game = seconds survived (max 300) + 300 if you win + 2 x radio percent + 50 x right Duwende answers + 5 x items gathered.
- XP is the total of your scores. Level = 1 + XP / 600.
- Achievements:

| Achievement | How to unlock |
|---|---|
| FIRST NIGHT | Finish a game |
| SUNRISE | Survive until 6:00 AM |
| RESCUED | Win by repairing the radio |
| DUWENDE FRIEND | 1 right Duwende answer |
| DUWENDE SCHOLAR | 5 right Duwende answers in total |
| GATHERER | Collect 50 items in total |
| TEAM PLAYER | Finish a multiplayer game |
| SURVIVOR | Reach level 5 |

### 4.5 Account screens
| Screen | What you do |
|---|---|
| Register | Fill in username, display name, password, repeat password, then CREATE |
| Login | Username and password, then LOGIN |
| Profile | Read your information. Buttons: EDIT, LOGOUT, DELETE, BACK |
| Edit | Change the display name; type a new password only if you want to change it; always type the current password; SAVE |
| Delete | Type your password and press DELETE FOREVER |
| Logout | From the main menu or the profile |

## 5. Design of the account system
### 5.1 What is stored for each account
Username, display name, password salt, password hash, XP, games played, wins, best score, longest night, items gathered, right Duwende answers, multiplayer games, achievements (as a number where each bit is one achievement).

### 5.2 Where it is stored
| Version | Accounts | Logged-in user | Settings | Solo save |
|---|---|---|---|---|
| Browser | local storage key `lcaccts` | `lcsess` | `lcprefs` | `lastcamp` |
| Java | `~/.lastcamp.accounts` | `~/.lastcamp.session` | `~/.lastcamp.prefs` | `~/.lastcamp.sav` |

(`~` is the user's home folder.) Browser and Java accounts are separate. A Java account line looks like: `user|name|salt|hash|xp|games|wins|best|bestTime|gather|right|mp|ach`.

### 5.3 Validation and error handling
| Input | Rule | Message |
|---|---|---|
| Any form | Nothing left empty | "Please fill in every box." |
| Username | 3-12 letters, numbers or `_`. Not case sensitive. Must be new | "Username: 3-12 letters, numbers or _" / "That username is already taken." |
| Display name | 1-12 letters, numbers or spaces | "Name: 1-12 letters or numbers" |
| Password | 4-20 characters | "Password must be 4-20 characters." |
| Repeat password | Must match | "The two passwords do not match." |
| Login | Username and password must match an account | "Wrong username or password." (the same message for both, so nobody can guess usernames) |
| Edit | The current password is required | "Type your current password to save." / "Your current password is wrong." |
| Delete | The password is required | "Wrong password. Nothing was deleted." |
| Storage | If saving fails (blocked or full storage, file permissions) the change is undone | "Could not save (storage is blocked or full)." |

Typing is also limited while you type (the boxes only accept allowed characters and lengths). Other handled errors: the multiplayer server cannot be reached, wrong or full room codes, the host leaving, a player leaving during a game, and missing pictures (a built-in copy is used).

### 5.4 Password security
Passwords are never stored. Each account gets a random 16-character salt, and the stored value is SHA-256 of `salt:password`. This is suitable for a class project; a real service would use a slow hash such as bcrypt and a server-side database.

### 5.5 Known limitations
- Accounts live on one device (browser) or one computer (Java). They do not follow you to another device.
- There is no password recovery. If a password is forgotten, the account cannot be opened.
- Browser accounts are removed if the browser's site data is cleared.

## 6. Architecture
```
Phone / PC browser  --WebSocket, port 5050--\
                                              >-- TheLastCampServer (relay + web page) --< host's game
Java desktop        --TCP, port 5051---------/
```
- The server only passes messages and serves `LastCamp.html`. The host's game decides everything (monsters, fire, radio, who wins), and other players send their actions.
- Browser and Java versions use the same messages, so they can play in the same room.
- The map is made from a fixed seed (the same algorithm as Java's `Random`), so every device builds the same forest and the same 6 punso.
- Pictures are stored inside the code as text (`Sprites.java` and the `ART` block in `LastCamp.html`), made by `Embedart.java`.

## 7. Testing
### 7.1 Automated tests (browser accounts)
Run `node tests/account-tests.js`. It loads the real account code from `LastCamp.html` and checks the hashing, register, login, logout, profile, score and achievements, update, delete, guest, and that accounts survive closing and reopening. Result when this document was written: **35 passed, 0 failed**.

### 7.2 Manual test cases (run each in the browser version and in the Java version)
| No. | Test | Expected result | Browser | Java |
|---|---|---|---|---|
| TC-01 | Register with valid data | Account created, back to LOGIN | [ ] | [ ] |
| TC-02 | Register with a taken username | "That username is already taken." | [ ] | [ ] |
| TC-03 | Register with different passwords | "The two passwords do not match." | [ ] | [ ] |
| TC-04 | Login with the wrong password | "Wrong username or password." | [ ] | [ ] |
| TC-05 | Login with the right data | Story intro, then the main menu showing the name and level | [ ] | [ ] |
| TC-06 | Close and reopen the game | You are still logged in | [ ] | [ ] |
| TC-07 | Open PROFILE | Shows your information and achievements | [ ] | [ ] |
| TC-08 | Edit the name with the right current password | Profile shows the new name | [ ] | [ ] |
| TC-09 | Edit with the wrong current password | Error, nothing changes | [ ] | [ ] |
| TC-10 | Play and finish a game | End screen with score; profile XP, games and level go up | [ ] | [ ] |
| TC-11 | Unlock an achievement | "NEW: ..." on the end screen and [X] in the profile | [ ] | [ ] |
| TC-12 | Logout | Back to LOGIN | [ ] | [ ] |
| TC-13 | Delete with a wrong password | Error, account stays | [ ] | [ ] |
| TC-14 | Delete with the right password | Account removed, cannot log in again | [ ] | [ ] |
| TC-15 | Play as guest and finish a game | Score shows, "Log in to save your score" | [ ] | [ ] |
| TC-16 | TRY AGAIN after game over | A new game starts | [ ] | [ ] |
| TC-17 | Step on a punso | Duwende question appears; right answer heals, wrong answer curses | [ ] | [ ] |
| TC-18 | Multiplayer: a player dies | They watch the others; the game goes on | [ ] | [ ] |

The Java version was written to match the browser version but was not compiled or run by the author of this document, so every Java row above must be checked by the team.

## 8. Team and contributions
| Member | Role | Responsibility | Contributions and commits |
|---|---|---|---|
| Sawali, Mia Cassey F | Project Leader / Game Designer | Project planning, game concept, task coordination | [fill in] |
| Orozco, Paul Angelo | Java / OOP Developer | Java classes, OOP structure, core game logic | [fill in] |
| Sawali, Mia Cassey | Gameplay Developer | Game mechanics, player controls, scoring and interactions | [fill in] |
| Adarlo, Marinela | Account Management Developer | Registration, login, CRUD, local file handling | [fill in] |
| Paz, Claud Alfred | UI/UX Designer | Game interface, menus, visual design, player experience | [fill in] |
| Dela Roca, Elizabeth Rose Aivy | QA Tester / Documentation Lead | Functional testing, test cases, documentation | [fill in] |

See CONTRIBUTING.md for how every member should commit their own work so the history shows who did what.
