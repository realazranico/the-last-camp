// Automated tests for the account system of the browser game (LastCamp.html).
// Run with:  node tests/account-tests.js
// It loads the real account code out of LastCamp.html, so the tests always match the game.
const fs = require('fs');
const path = require('path');
const crypto = require('crypto');

const html = fs.readFileSync(path.join(__dirname, '..', 'LastCamp.html'), 'utf8').replace(/\r\n/g, '\n');
const a = html.indexOf('const ACH = [');
const b = html.indexOf('function rLogin()');
if (a < 0 || b < 0) {
    console.log('Could not find the account code in LastCamp.html');
    process.exit(1);
}

const harness = `
const store = {};
const localStorage = {
    getItem: k => (k in store ? store[k] : null),
    setItem: (k, v) => { store[k] = String(v); },
    removeItem: k => { delete store[k]; }
};
let playerName = '', st = 'title', stT = 0, panel = 0, mode = 0, me = null, tl = 0, radio = 0, nameReturn = '';
let fa = null, fb = null, fc = null, fd = null;
const savePrefs = () => {}, closeNet = () => {};
${html.slice(a, b)}
return {
    sha256, store,
    setF: o => { F = o; formMsg = ''; },
    run: fn => { fn(); },
    doRegister, doLogin, doLogout, doEdit, doDelete, doGuest, initAccts, recordRun,
    setRun: (m, t, r, right, gather, hp) => { mode = m; tl = t; radio = r; runRight = right; runGather = gather; me = { hp: hp === undefined ? 5 : hp }; },
    reopen: () => { accts = {}; acct = null; initAccts(); },
    get: () => ({ st, formMsg, formOk, acct, accts, guest, playerName, runInfo }),
    lvl, ACH
};`;

const g = new Function('crypto', harness)(crypto);
let pass = 0, fail = 0;
function check(name, cond) {
    if (cond) { pass++; console.log('  PASS  ' + name); } else { fail++; console.log('  FAIL  ' + name); }
}
const reg = (u, n, p, q) => { g.setF({ ru: u, rn: n, rp: p, rq: q }); g.doRegister(); return g.get(); };
const log = (u, p) => { g.setF({ lu: u, lp: p }); g.doLogin(); return g.get(); };
const edit = (n, np, cur) => { g.setF({ en: n, ep: np, ec: cur }); g.doEdit(); return g.get(); };

console.log('Password hashing');
check('SHA-256 of "abc" is correct', g.sha256('abc') === 'ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad');
check('SHA-256 matches Node crypto for 9 different texts', ['', 'a:b', 'x'.repeat(55), 'x'.repeat(56), 'x'.repeat(64), 'x'.repeat(300), 'héllo wörld ✓', 'salt:pass', '1'].every(t => g.sha256(t) === crypto.createHash('sha256').update(t).digest('hex')));

console.log('Create (register)');
check('empty form is rejected', reg('', '', '', '').formMsg.includes('fill'));
check('username with symbols is rejected', reg('a!b', 'Ana', 'pass1', 'pass1').formMsg.includes('Username'));
check('username shorter than 3 is rejected', reg('ab', 'Ana', 'pass1', 'pass1').formMsg.includes('Username'));
check('display name with symbols is rejected', reg('ana01', 'A$a', 'pass1', 'pass1').formMsg.includes('Name'));
check('password shorter than 4 is rejected', reg('ana01', 'Ana', 'abc', 'abc').formMsg.includes('Password'));
check('different passwords are rejected', reg('ana01', 'Ana', 'pass1', 'pass2').formMsg.includes('match'));
let r = reg('Ana01', 'Ana', 'pass1', 'pass1');
check('valid account is created', r.st === 'login' && r.formOk && !!r.accts['ana01']);
check('duplicate username (any letter case) is rejected', reg('ANA01', 'Other', 'pass1', 'pass1').formMsg.includes('taken'));
check('the password is never stored in plain text', !JSON.stringify(r.accts).includes('pass1'));

console.log('Accounts stay after the game is closed and reopened');
g.reopen();
check('account is still there after reopening', !!g.get().accts['ana01']);

console.log('Login');
check('empty login is rejected', log('', '').formMsg.includes('Type'));
check('wrong password is rejected', log('ana01', 'nope').formMsg.includes('Wrong') && !g.get().acct);
check('unknown username is rejected', log('ghost', 'pass1').formMsg.includes('Wrong'));
r = log('ANA01', 'pass1');
check('correct login works (username is not case sensitive)', r.acct && r.acct.name === 'Ana' && r.st === 'lore' && r.playerName === 'Ana');
g.reopen();
check('player is still logged in after reopening', g.get().acct && g.get().acct.name === 'Ana');

console.log('Read (profile, score, level, achievements)');
g.setRun(0, 0, 40, 2, 12);
g.recordRun(true);
r = g.get();
check('score = 300 survived + 300 win + 80 radio + 100 answers + 60 items = 840', r.runInfo.score === 840);
check('profile shows 1 game, 1 win, 840 XP, level 2', r.acct.games === 1 && r.acct.wins === 1 && r.acct.xp === 840 && g.lvl(r.acct.xp) === 2);
check('achievements FIRST NIGHT, SUNRISE, DUWENDE FRIEND unlocked', r.runInfo.ach.join() === 'FIRST NIGHT,SUNRISE,DUWENDE FRIEND');
g.setRun(2, 200, 100, 4, 45);
g.recordRun(true);
r = g.get();
check('winning by radio in multiplayer unlocks RESCUED, DUWENDE SCHOLAR, GATHERER, TEAM PLAYER', r.runInfo.ach.join() === 'RESCUED,DUWENDE SCHOLAR,GATHERER,TEAM PLAYER');
g.setRun(0, 0, 0, 0, 0, 0);
g.recordRun(false);
check('a lost game still counts as a game played but not a win', g.get().acct.games === 3 && g.get().acct.wins === 2);

console.log('Update');
check('invalid new name is rejected', edit('A$', '', 'pass1').formMsg.includes('Name'));
check('missing current password is rejected', edit('Ana2', '', '').formMsg.includes('current'));
check('wrong current password is rejected', edit('Ana2', '', 'zzzz').formMsg.includes('wrong'));
check('too short new password is rejected', edit('Ana2', 'ab', 'pass1').formMsg.includes('New password'));
check('display name can be changed', edit('Ana2', '', 'pass1').acct.name === 'Ana2' && g.get().st === 'profile');
edit('Ana2', 'newpass', 'pass1');
check('old password stops working after a password change', log('ana01', 'pass1').formMsg.includes('Wrong'));
check('new password works', log('ana01', 'newpass').acct.name === 'Ana2');

console.log('Logout');
g.doLogout();
r = g.get();
check('logout clears the account and the saved session', r.acct === null && r.st === 'login' && g.store.lcsess === undefined);

console.log('Delete');
log('ana01', 'newpass');
g.setF({ dp: '' }); g.doDelete();
check('delete without a password is rejected', g.get().formMsg.includes('password') && !!g.get().accts['ana01']);
g.setF({ dp: 'wrong' }); g.doDelete();
check('delete with a wrong password keeps the account', g.get().formMsg.includes('Wrong') && !!g.get().accts['ana01']);
g.setF({ dp: 'newpass' }); g.doDelete();
check('delete with the right password removes the account', !g.get().accts['ana01'] && g.get().acct === null && g.get().st === 'login');
g.reopen();
check('deleted account stays deleted after reopening', !g.get().accts['ana01']);

console.log('Guest');
g.doGuest();
check('playing as a guest works without an account', g.get().guest === true && g.get().acct === null && g.get().st === 'name');

console.log('\n' + pass + ' passed, ' + fail + ' failed');
process.exit(fail ? 1 : 0);
