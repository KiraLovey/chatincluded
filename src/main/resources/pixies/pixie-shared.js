'use strict';
// pixie-shared.js — ChatIncluded Pixies sprite engine
// Loaded by pixie-overlay.html, pixie-config.html, and pixie-library.html

// ─── Canvas drawing helpers ─────────────────────────────────────────────────
const PX_W = 80, PX_H = 96;

function h(H, s, l) { return `hsl(${H},${s}%,${l}%)`; }
function ha(H, s, l, a) { return `hsla(${H},${s}%,${l}%,${a})`; }
function rg(ctx, cx, cy, r, c0, c1) {
    const g = ctx.createRadialGradient(cx, cy - r * .2, r * .1, cx, cy, r);
    g.addColorStop(0, c0); g.addColorStop(1, c1); return g;
}
function lg(ctx, x0, y0, x1, y1, c0, c1) {
    const g = ctx.createLinearGradient(x0, y0, x1, y1);
    g.addColorStop(0, c0); g.addColorStop(1, c1); return g;
}
function rr(ctx, x, y, w, ht, r) { ctx.beginPath(); ctx.roundRect(x, y, w, ht, r); }
function ci(ctx, cx, cy, r) { ctx.beginPath(); ctx.arc(cx, cy, r, 0, Math.PI * 2); }
function sh(ctx, c, b, ox, oy) {
    b = b === undefined ? 8 : b;
    ox = ox === undefined ? 0 : ox;
    oy = oy === undefined ? 3 : oy;
    ctx.shadowColor = c; ctx.shadowBlur = b; ctx.shadowOffsetX = ox; ctx.shadowOffsetY = oy;
}
function ns(ctx) { ctx.shadowColor = 'transparent'; ctx.shadowBlur = 0; ctx.shadowOffsetX = 0; ctx.shadowOffsetY = 0; }

// ─── Sprite image loader ─────────────────────────────────────────────────────
var SPRITE_IMAGES = {};
(function() {
    ['fairy','cat','dog','ghost','axolotl','kitsune'].forEach(function(name) {
        var img = new Image();
        img.src = './sprites/' + name + 'Pixie.png';
        SPRITE_IMAGES[name] = img;
    });
})();

function _imgSprite(name, defaultHue) {
    return { draw: function(ctx, hue) {
        var img = SPRITE_IMAGES[name];
        if (!img || !img.complete || !img.naturalWidth) return;
        var offset = hue - defaultHue;
        if (offset !== 0) ctx.filter = 'hue-rotate(' + offset + 'deg)';
        ctx.drawImage(img, 0, 0, 80, 96);
        ctx.filter = 'none';
    }};
}

// ─── Sprite definitions ─────────────────────────────────────────────────────
const SPRITES = {

    fairy:   _imgSprite('fairy',   175),
    axolotl: _imgSprite('axolotl', 335),
    kitsune: _imgSprite('kitsune',  20),
    cat:     _imgSprite('cat',      25),
    dog:     _imgSprite('dog',      30),
    ghost:   _imgSprite('ghost',   245),

    robot: { draw(ctx, hue) {
        const mL = h(hue,28,72), mM = h(hue,28,58), mD = h(hue,28,42), sc = '#00ffee', bo = '#ffd700', eG = h((hue+160)%360,90,62);
        // antenna
        ctx.save(); ctx.strokeStyle=mD; ctx.lineWidth=3; ctx.lineCap='round'; ctx.beginPath(); ctx.moveTo(40,7); ctx.lineTo(40,17); ctx.stroke(); ci(ctx,40,6,5.5); ctx.fillStyle=lg(ctx,35,2,45,10,bo,h(40,90,55)); sh(ctx,ha(40,90,55,.7),8); ctx.fill(); ns(ctx); ctx.restore();
        // head
        ctx.save(); rr(ctx,15,16,50,38,[12,12,8,8]); ctx.fillStyle=lg(ctx,15,16,15,54,mL,mM); sh(ctx,ha(hue,25,20,.4),8,0,4); ctx.fill(); ns(ctx); rr(ctx,15,16,50,38,[12,12,8,8]); ctx.strokeStyle=mD; ctx.lineWidth=1.5; ctx.stroke(); ctx.restore();
        // eye bezels + screens
        ctx.save(); rr(ctx,19,22,19,17,[6]); ctx.fillStyle=mD; ctx.fill(); rr(ctx,42,22,19,17,[6]); ctx.fill();
        rr(ctx,21,24,15,13,[5]); var e1=ctx.createRadialGradient(28.5,30.5,1,28.5,30.5,9); e1.addColorStop(0,'#fff'); e1.addColorStop(.3,sc); e1.addColorStop(1,ha((hue+160)%360,90,28,.8)); ctx.fillStyle=e1; sh(ctx,sc,12); ctx.fill(); ns(ctx);
        rr(ctx,44,24,15,13,[5]); var e2=ctx.createRadialGradient(51.5,30.5,1,51.5,30.5,9); e2.addColorStop(0,'#fff'); e2.addColorStop(.3,sc); e2.addColorStop(1,ha((hue+160)%360,90,28,.8)); ctx.fillStyle=e2; sh(ctx,sc,12); ctx.fill(); ns(ctx); ctx.restore();
        // grille mouth
        ctx.save(); rr(ctx,22,43,36,8,[5]); ctx.fillStyle=mD; ctx.fill(); ctx.strokeStyle=ha(hue,20,55,.55); ctx.lineWidth=1.2; for(var x=26;x<=54;x+=5){ ctx.beginPath(); ctx.moveTo(x,44); ctx.lineTo(x,50); ctx.stroke(); } ctx.restore();
        // body
        ctx.save(); rr(ctx,17,56,46,32,[8,8,6,6]); ctx.fillStyle=lg(ctx,17,56,17,88,mM,mD); sh(ctx,ha(hue,25,15,.4),8,0,4); ctx.fill(); ns(ctx); rr(ctx,17,56,46,32,[8,8,6,6]); ctx.strokeStyle=mD; ctx.lineWidth=1.5; ctx.stroke(); ctx.restore();
        // chest panel
        ctx.save(); rr(ctx,25,60,30,19,[7]); ctx.fillStyle=mD; ctx.fill(); [bo,sc,'#ff6b9d',bo].forEach(function(c,i){ ci(ctx,31+i*8,70,3.2); ctx.fillStyle=c; sh(ctx,c,8); ctx.fill(); ns(ctx); }); ctx.restore();
        // arms
        ctx.save(); ctx.fillStyle=mM; rr(ctx,3,57,13,25,[7]); ctx.fill(); rr(ctx,64,57,13,25,[7]); ctx.fill(); ctx.strokeStyle=mD; ctx.lineWidth=2; ci(ctx,9.5,62,5.5); ctx.stroke(); ci(ctx,70.5,62,5.5); ctx.stroke(); ctx.fillStyle=mD; rr(ctx,1,80,17,9,[5]); ctx.fill(); rr(ctx,62,80,17,9,[5]); ctx.fill(); ctx.restore();
        // legs+feet
        ctx.save(); ctx.fillStyle=mD; rr(ctx,21,86,17,10,[4,4,5,5]); ctx.fill(); rr(ctx,42,86,17,10,[4,4,5,5]); ctx.fill(); ctx.fillStyle=mM; rr(ctx,17,93,23,6,[3]); ctx.fill(); rr(ctx,40,93,23,6,[3]); ctx.fill(); ctx.restore();
        // shoulder bolts
        ctx.save(); [17,63].forEach(function(bx){ ci(ctx,bx,57,5.5); ctx.fillStyle=bo; sh(ctx,bo,7); ctx.fill(); ns(ctx); }); ctx.restore();
    }},

};

// ─── Sprite names / defaults ────────────────────────────────────────────────
const SPRITE_NAMES = Object.keys(SPRITES);
const SPRITE_DEFAULT_HUES = { fairy:175, cat:25, dog:30, ghost:245, axolotl:335, kitsune:20, robot:195 };

// ─── Platform colours (for icon badges) ─────────────────────────────────────
const PLATFORM_COLOURS = {
    TWITCH:    '#9146ff',
    KICK:      '#53fc18',
    YOUTUBE:   '#ff0000',
    TROVO:     '#00b87b',
    CASTERLABS:'#a78bfa',
};

// ─── PixieEngine ────────────────────────────────────────────────────────────
function PixieEngine(canvas, settings) {
    this.canvas   = canvas;
    this.ctx      = canvas.getContext('2d');
    this.settings = settings || {};
    this.pixies   = {};          // userId -> pixie state
    this.spritePrefs = {};       // userId -> chosen sprite name
    this.hueMap   = {};          // userId -> assigned hue
    this._running = false;
    this._lastTs  = 0;
    this._boundTick = this._tick.bind(this);
}

PixieEngine.prototype.start = function() {
    this._running = true;
    this._lastTs = performance.now();
    requestAnimationFrame(this._boundTick);
};

PixieEngine.prototype.stop = function() {
    this._running = false;
};

PixieEngine.prototype.updateSettings = function(s) {
    this.settings = s;
};

PixieEngine.prototype._tick = function(ts) {
    if (!this._running) return;
    var dt = Math.min((ts - this._lastTs) / 1000, 0.1);
    this._lastTs = ts;
    this._resize();
    this._update(dt);
    this._render();
    requestAnimationFrame(this._boundTick);
};

PixieEngine.prototype._resize = function() {
    var c = this.canvas;
    if (c.width !== c.clientWidth || c.height !== c.clientHeight) {
        c.width  = c.clientWidth  || window.innerWidth;
        c.height = c.clientHeight || window.innerHeight;
    }
};

PixieEngine.prototype._update = function(dt) {
    var s    = this.settings;
    var W    = this.canvas.width;
    var now  = Date.now();
    var scale = Number(s.spriteScale) || 3;
    var pw   = PX_W * scale;
    var ids  = Object.keys(this.pixies);

    for (var i = 0; i < ids.length; i++) {
        var p = this.pixies[ids[i]];

        // inactivity — start walking off-screen
        if (p.state !== 'exiting' && now - p.lastActiveTime > (Number(s.inactivityMs) || 90000)) {
            p.state = 'exiting';
            p.dir = (p.x < W / 2) ? -1 : 1;
        }

        switch (p.state) {
            case 'entering':
                p.x += p.dir * p.speed * dt * W;
                p.walkPhase += dt * 5;
                // once fully on-screen, start moseying
                if (p.x > 0 && p.x < W - pw) {
                    p.state = 'walking';
                    p.stateTimer = 5 + Math.random() * 10;
                }
                break;

            case 'walking':
                p.x += p.dir * p.speed * dt * W;
                p.walkPhase += dt * 5;
                // soft wall bounce
                if (p.x < -pw * 0.3) { p.x = -pw * 0.3; p.dir = 1; }
                if (p.x > W - pw * 0.7) { p.x = W - pw * 0.7; p.dir = -1; }
                p.stateTimer -= dt;
                if (p.stateTimer <= 0) {
                    p.state = 'idle';
                    p.stateTimer = 2 + Math.random() * 5;
                }
                break;

            case 'idle':
                // very slow breathing bob, no movement
                p.walkPhase += dt * 0.6;
                p.stateTimer -= dt;
                if (p.stateTimer <= 0) {
                    p.state = 'walking';
                    p.stateTimer = 5 + Math.random() * 10;
                    if (Math.random() < 0.45) p.dir = -p.dir; // occasionally turn around
                }
                break;

            case 'exiting':
                p.x += p.dir * p.speed * dt * W * 1.4;
                p.walkPhase += dt * 7;
                if (p.x < -pw * 2 || p.x > W + pw * 2) {
                    delete this.pixies[ids[i]];
                }
                break;
        }
    }
};

PixieEngine.prototype._render = function() {
    var ctx   = this.ctx;
    var s     = this.settings;
    var W     = this.canvas.width;
    var H     = this.canvas.height;
    var scale = Number(s.spriteScale) || 3;
    var now   = Date.now();

    ctx.clearRect(0, 0, W, H);

    var spriteH = PX_H * scale;
    var spriteW = PX_W * scale;
    var floorY  = H - 8;              // bottom baseline

    var ids = Object.keys(this.pixies);
    for (var i = 0; i < ids.length; i++) {
        var p = this.pixies[ids[i]];

        var bob   = Math.abs(Math.sin(p.walkPhase)) * 4 * scale;
        var drawY = floorY - spriteH - bob;   // sprite top-left Y (bob lifts up)
        var drawX = p.x;                      // sprite left X

        // ── draw sprite in scaled + mirrored space ───────────────────────
        ctx.save();
        if (p.dir < 0) {
            // mirror: translate to right edge of sprite, flip, shift back
            ctx.translate(drawX + spriteW, drawY);
            ctx.scale(-scale, scale);
        } else {
            ctx.translate(drawX, drawY);
            ctx.scale(scale, scale);
        }
        if (SPRITES[p.spriteType]) {
            SPRITES[p.spriteType].draw(ctx, p.hue);
        }
        ctx.restore();

        var cx = drawX + spriteW / 2;   // horizontal centre of sprite

        // ── aura glow ───────────────────────────────────────────────────
        ctx.save();
        var auraG = ctx.createRadialGradient(cx, floorY, 0, cx, floorY, spriteW * 0.55);
        auraG.addColorStop(0, 'hsla(' + p.hue + ',75%,65%,0.28)');
        auraG.addColorStop(1, 'transparent');
        ctx.fillStyle = auraG;
        ctx.beginPath();
        ctx.ellipse(cx, floorY, spriteW * 0.55, spriteH * 0.12, 0, 0, Math.PI * 2);
        ctx.fill();
        ctx.restore();

        // ── name tag ────────────────────────────────────────────────────
        var tagY = floorY + Math.round(scale * 4) + 4;
        this._drawNameTag(ctx, cx, tagY, p.displayName, p.hue, scale);

        // ── platform badge ──────────────────────────────────────────────
        if (s.showPlatformIcon) {
            this._drawPlatformBadge(ctx, drawX + spriteW - Math.round(scale * 3), drawY + Math.round(scale * 2), p.platform, scale);
        }

    }
};

PixieEngine.prototype._drawNameTag = function(ctx, cx, y, name, hue, scale) {
    var fontSize = Math.max(10, Math.round(scale * 3.8 + 8));
    ctx.save();
    ctx.font = '800 ' + fontSize + 'px Nunito, sans-serif';
    var tw = ctx.measureText(name).width;
    var pad = Math.max(6, scale * 3);
    var bw = tw + pad * 2;
    var bh = fontSize + Math.round(scale * 1.5) + 2;
    var rx = cx - bw / 2;
    var ry = y - bh / 2;
    ctx.fillStyle = 'rgba(0,0,0,0.55)';
    ctx.strokeStyle = 'rgba(255,255,255,0.12)';
    ctx.lineWidth = 1;
    ctx.beginPath();
    ctx.roundRect(rx, ry, bw, bh, bh / 2);
    ctx.fill();
    ctx.stroke();
    ctx.fillStyle = '#ffffff';
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';
    ctx.fillText(name, cx, y);
    ctx.restore();
};

PixieEngine.prototype._drawPlatformBadge = function(ctx, x, y, platform, scale) {
    var r = Math.max(4, scale * 3);
    var col = PLATFORM_COLOURS[platform] || '#a78bfa';
    ctx.save();
    ctx.fillStyle = col;
    ctx.beginPath();
    ctx.arc(x, y, r, 0, Math.PI * 2);
    ctx.fill();
    ctx.fillStyle = '#000';
    ctx.font = 'bold ' + Math.round(r * 1.1) + 'px sans-serif';
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';
    ctx.fillText((platform || 'T')[0], x, y + 0.5);
    ctx.restore();
};

PixieEngine.prototype.handleChatMessage = function(event) {
    var s = this.settings;
    var sender = event.sender || {};
    var userId = sender.channelId || sender.upid || sender.id;
    if (!userId) return;

    var text = this._extractText(event);

    // !sprite command
    var m = text.match(/^!pixie\s+(\w+)/i);
    if (m) {
        var requested = m[1].toLowerCase();
        // Handle 'H' case-insensitively
        var found = SPRITE_NAMES.find(function(n){ return n.toLowerCase() === requested; });
        if (found) {
            this.spritePrefs[userId] = found;
            if (this.pixies[userId]) this.pixies[userId].spriteType = found;
        }
        return;
    }

    var now = Date.now();

    if (!this.pixies[userId]) {
        var count = Object.keys(this.pixies).length;
        if (count >= (Number(s.maxPixies) || 40)) return;
        this._spawnPixie(userId, sender);
    }

    var p = this.pixies[userId];
    if (!p) return;
    p.lastActiveTime = now;
    if (p.state === 'exiting') {
        p.state = 'walking';
        p.dir = -p.dir;
        p.stateTimer = 5 + Math.random() * 8;
    }
};

PixieEngine.prototype._spawnPixie = function(userId, sender) {
    var s = this.settings;
    var spriteType = this.spritePrefs[userId]
        || SPRITE_NAMES[Math.floor(Math.random() * SPRITE_NAMES.length)];

    var hue;
    if (s.hueShift !== false) {
        if (!this.hueMap[userId]) {
            this.hueMap[userId] = Math.floor(Math.random() * 360);
        }
        hue = this.hueMap[userId];
    } else {
        hue = SPRITE_DEFAULT_HUES[spriteType] || 320;
    }

    var scale = Number(s.spriteScale) || 3;
    var W     = this.canvas.width;
    var speedRange = (Number(s.walkSpeedMax) || 0.07) - (Number(s.walkSpeedMin) || 0.03);
    var speed = (Number(s.walkSpeedMin) || 0.03) + Math.random() * speedRange;
    var dir   = Math.random() < 0.5 ? 1 : -1;
    var startX = dir > 0 ? -(PX_W * scale) : W;

    this.pixies[userId] = {
        userId:         userId,
        displayName:    sender.displayname || sender.username || userId,
        platform:       (sender.platform || 'TWITCH').toUpperCase(),
        spriteType:     spriteType,
        hue:            hue,
        x:              startX,
        dir:            dir,
        speed:          speed,
        walkPhase:      Math.random() * Math.PI * 2,
        lastActiveTime: Date.now(),
        state:          'entering',
        stateTimer:     0,
    };
};

PixieEngine.prototype._extractText = function(event) {
    if (!event.fragments) return '';
    return event.fragments
        .filter(function(f) { return f.type === 'TEXT'; })
        .map(function(f) { return f.raw || f.text || ''; })
        .join('')
        .trim();
};

// ─── Custom sprite loader ────────────────────────────────────────────────────
function loadCustomSprites(jsonStr) {
    var list;
    try { list = JSON.parse(jsonStr || '[]'); } catch(e) { list = []; }
    if (!Array.isArray(list)) list = [];
    list.forEach(function(entry) {
        var name = (entry.name || '').toLowerCase().trim();
        var url  = (entry.url  || '').trim();
        if (!name || !url) return;
        // reload if URL changed, skip if same
        var existing = SPRITE_IMAGES[name];
        if (existing && existing._customUrl === url) return;
        var img = new Image();
        img.crossOrigin = 'anonymous';
        img._customUrl = url;
        img.src = url;
        SPRITE_IMAGES[name] = img;
        if (!SPRITES[name]) {
            SPRITES[name] = _imgSprite(name, 0);
            SPRITE_DEFAULT_HUES[name] = 0;
            SPRITE_NAMES.push(name);
        }
    });
}

// ─── Static helper: draw a sprite onto any canvas ───────────────────────────
function drawSpriteToCanvas(canvas, spriteType, hue) {
    var ctx = canvas.getContext('2d');
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    if (SPRITES[spriteType]) SPRITES[spriteType].draw(ctx, hue);
}
