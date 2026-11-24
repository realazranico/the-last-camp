# Contributing and commit guide

This guide helps the group keep a meaningful commit history where every member's work can be seen.

## Team roles
| Role | Responsibility | Member | Suggested commit areas |
|---|---|---|---|
| Project Leader / Game Designer | Project planning, game concept, task coordination | Sawali, Mia Cassey F | Game concept and story, task list, merging pull requests |
| Java / OOP Developer | Java classes, OOP structure, core game logic | Orozco, Paul Angelo | Java classes and structure, relay server, `Sprites.java` and `Embedart.java` |
| Gameplay Developer | Game mechanics, player controls, scoring and interactions | Sawali, Mia Cassey | Monsters, fire, radio and shelters, controls, score, level, Duwende quiz |
| Account Management Developer | Registration, login, CRUD, local file handling | Adarlo, Marinela | Register, login, logout, profile, edit, delete, account storage |
| UI/UX Designer | Game interface, menus, visual design, player experience | Paz, Claud Alfred | Main menu, screens and buttons, characters and pictures, HOW TO PLAY |
| QA Tester / Documentation Lead | Functional testing, test cases, documentation | Dela Roca, Elizabeth Rose Aivy | Test cases, `tests/`, README, `docs/DOCUMENTATION.md` |

Everyone also helps with testing and with reviewing each other's pull requests.

## Rules for the group
1. **Each member commits their own work with their own GitHub account.** Do not commit for someone else.
2. Make small commits that each do one thing, with a clear message.
3. Work on a branch for each feature, then merge it with a pull request that another member reviews.
4. Never delete or rewrite the history. Fix mistakes with a new commit.
5. Keep the team tables in README.md and docs/DOCUMENTATION.md up to date with what each member really did and how many commits they made.

## One-time setup for every member
```
git config --global user.name "Your Name"
git config --global user.email "the email of your GitHub account"
git clone https://github.com/realazranico/the-last-camp.git
cd the-last-camp
```

## Everyday workflow
```
git checkout -b feature/short-name      # start a branch
# ... change files ...
git add FILE_YOU_CHANGED
git commit -m "Add login screen"        # a clear message
git push -u origin feature/short-name
```
Then open a Pull Request on GitHub, ask a teammate to review it, and merge it.

## Writing good commit messages
- Start with a verb: Add, Fix, Update, Remove, Document, Test.
- Say what and where, in under 70 characters.
- Good: `Add salted password hashing to account system`
- Bad: `update`, `final`, `asdf`

## Suggested commit plan (split it between members)
| Area | Example commit messages |
|---|---|
| Game core | `Add forest map generation`, `Add campfire, wood and battery rules`, `Add radio repair and win condition` |
| Monsters | `Add four monsters with chase behaviour`, `Add shelters that keep monsters away` |
| Menus | `Add main menu`, `Add HOW TO PLAY guide`, `Add monster introduction pages` |
| Accounts | `Add register screen with validation`, `Add login and logout`, `Add profile screen`, `Add edit profile`, `Add delete account`, `Store accounts so they survive restart` |
| Score | `Add score and XP after each game`, `Add levels and achievements`, `Show score on the end screen` |
| Multiplayer | `Add relay server`, `Add lobby and room codes`, `Add spectator mode`, `Support 10 players` |
| Extras | `Add Duwende quiz and curse`, `Add 6 more characters`, `Add dash energy bar` |
| Documentation | `Write README`, `Write project documentation`, `Add account tests` |

## Checking everyone's contribution
On GitHub open the repository, then **Insights > Contributors**, or run `git shortlog -sn`. Copy the numbers and a short description of each member's work into the team table of docs/DOCUMENTATION.md.

## Uploading this project to the repository
1. Extract the zip.
2. On GitHub open the repository, click **Add file > Upload files**, drag in every file and folder from the zip (including `assets`, `docs` and `tests`), and click **Commit changes**.
3. If you host on Render, upload the new `LastCamp.html` and `TheLastCampServer.java`, delete any old `index.html`, and use **Manual Deploy > Deploy latest commit**.
