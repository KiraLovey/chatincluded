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

// ─── Sprite definitions ─────────────────────────────────────────────────────
const SPRITES = {

    fairy: { draw(ctx, hue) {
        const sk = '#ffc89a', skD = '#e0a070',
              wC = ha((hue+170)%360,55,78,.45), wS = ha((hue+170)%360,65,62,.8),
              dr = h(hue,65,42), drL = h(hue,60,56), hr = h(hue,60,40);
        // upper wings (large)
        ctx.save();
        ctx.beginPath(); ctx.moveTo(38,40); ctx.bezierCurveTo(14,30,6,12,22,8); ctx.bezierCurveTo(32,4,38,24,38,36); ctx.closePath();
        sh(ctx,wS,6); ctx.fillStyle=wC; ctx.fill(); ns(ctx); ctx.strokeStyle=wS; ctx.lineWidth=1.2; ctx.stroke();
        ctx.beginPath(); ctx.moveTo(43,40); ctx.bezierCurveTo(64,28,74,10,60,8); ctx.bezierCurveTo(50,4,43,24,43,36); ctx.closePath();
        ctx.fillStyle=wC; ctx.fill(); ctx.strokeStyle=wS; ctx.lineWidth=1.2; ctx.stroke();
        // lower wings (smaller)
        ctx.beginPath(); ctx.moveTo(36,48); ctx.bezierCurveTo(18,44,12,58,24,62); ctx.bezierCurveTo(32,66,36,54,36,50); ctx.closePath();
        ctx.fillStyle=wC; ctx.fill(); ctx.strokeStyle=wS; ctx.lineWidth=1.2; ctx.stroke();
        ctx.beginPath(); ctx.moveTo(45,48); ctx.bezierCurveTo(60,44,68,56,58,62); ctx.bezierCurveTo(50,66,45,54,45,50); ctx.closePath();
        ctx.fillStyle=wC; ctx.fill(); ctx.strokeStyle=wS; ctx.lineWidth=1.2; ctx.stroke();
        ctx.restore();
        // wand
        ctx.save(); ctx.strokeStyle=skD; ctx.lineWidth=2.5; ctx.lineCap='round';
        ctx.beginPath(); ctx.moveTo(28,52); ctx.lineTo(14,68); ctx.stroke();
        ci(ctx,13,70,4.5); ctx.fillStyle='#ffd700'; sh(ctx,'#ffaa00',9); ctx.fill(); ns(ctx); ctx.restore();
        // arms
        ctx.save(); ctx.strokeStyle=sk; ctx.lineWidth=5.5; ctx.lineCap='round';
        ctx.beginPath(); ctx.moveTo(31,40); ctx.quadraticCurveTo(22,48,27,54); ctx.stroke();
        ctx.beginPath(); ctx.moveTo(49,40); ctx.quadraticCurveTo(58,48,55,54); ctx.stroke(); ctx.restore();
        // dress (skirt + bodice)
        ctx.save();
        ctx.beginPath(); ctx.moveTo(29,54); ctx.lineTo(20,76); ctx.quadraticCurveTo(40,84,60,76); ctx.lineTo(51,54); ctx.closePath();
        ctx.fillStyle=dr; ctx.fill();
        rr(ctx,29,34,22,22,[4,4,10,10]); ctx.fillStyle=drL; ctx.fill();
        ctx.beginPath(); ctx.moveTo(29,54); ctx.quadraticCurveTo(40,50,51,54); ctx.quadraticCurveTo(40,58,29,54); ctx.closePath();
        ctx.fillStyle='rgba(255,255,255,.55)'; ctx.fill(); ctx.restore();
        // legs + shoes
        ctx.save(); ctx.strokeStyle=sk; ctx.lineWidth=4.5; ctx.lineCap='round';
        ctx.beginPath(); ctx.moveTo(36,74); ctx.lineTo(33,86); ctx.stroke();
        ctx.beginPath(); ctx.moveTo(44,74); ctx.lineTo(48,86); ctx.stroke();
        ctx.fillStyle=drL; ci(ctx,32,89,4); ctx.fill(); ci(ctx,49,89,4); ctx.fill(); ctx.restore();
        // hair bun
        ctx.save(); ctx.fillStyle=hr;
        ci(ctx,40,16,9); ctx.fill();
        ctx.beginPath(); ctx.arc(40,21,11,Math.PI,0); ctx.closePath(); ctx.fill(); ctx.restore();
        // head
        ctx.save(); ci(ctx,40,27,12); ctx.fillStyle=rg(ctx,40,22,12,sk,skD); sh(ctx,ha(28,30,25,.3),5,0,2); ctx.fill(); ns(ctx); ctx.restore();
        // pointed ears
        ctx.save(); ctx.fillStyle=sk;
        ctx.beginPath(); ctx.moveTo(30,22); ctx.lineTo(26,13); ctx.lineTo(34,19); ctx.closePath(); ctx.fill();
        ctx.beginPath(); ctx.moveTo(50,22); ctx.lineTo(54,13); ctx.lineTo(46,19); ctx.closePath(); ctx.fill(); ctx.restore();
        // eyes
        ctx.save(); ctx.fillStyle='#fff';
        ctx.beginPath(); ctx.ellipse(36,27,4.5,5,-.1,0,Math.PI*2); ctx.fill();
        ctx.beginPath(); ctx.ellipse(44,27,4.5,5,.1,0,Math.PI*2); ctx.fill();
        ctx.fillStyle=h(hue,65,42); ctx.beginPath(); ctx.arc(36,28,2.8,0,Math.PI*2); ctx.fill(); ctx.beginPath(); ctx.arc(44,28,2.8,0,Math.PI*2); ctx.fill();
        ctx.fillStyle='#1a1a2e'; ctx.beginPath(); ctx.arc(36,28,1.7,0,Math.PI*2); ctx.fill(); ctx.beginPath(); ctx.arc(44,28,1.7,0,Math.PI*2); ctx.fill();
        ctx.fillStyle='rgba(255,255,255,.9)'; ci(ctx,37,26.5,1.1); ctx.fill(); ci(ctx,45,26.5,1.1); ctx.fill(); ctx.restore();
        // blush
        ctx.save(); ctx.fillStyle=ha(350,80,75,.4);
        ctx.beginPath(); ctx.ellipse(30,31,3.5,2.2,0,0,Math.PI*2); ctx.fill();
        ctx.beginPath(); ctx.ellipse(50,31,3.5,2.2,0,0,Math.PI*2); ctx.fill(); ctx.restore();
        // smile
        ctx.save(); ctx.strokeStyle=ha(350,40,40,.6); ctx.lineWidth=1.5; ctx.lineCap='round';
        ctx.beginPath(); ctx.arc(40,32,3,.2,Math.PI-.2); ctx.stroke(); ctx.restore();
    }},

    axolotl: { draw(ctx, hue) {
        const bL = h(hue,60,78), bD = h(hue,60,55), bM = h(hue,58,66),
              gl = h((hue+30)%360,75,66), glT = h((hue+30)%360,80,88);
        // tail (long, curves up-right)
        ctx.save(); ctx.strokeStyle=bD; ctx.lineWidth=11; ctx.lineCap='round';
        ctx.beginPath(); ctx.moveTo(58,62); ctx.bezierCurveTo(70,50,78,36,72,24); ctx.stroke();
        ctx.strokeStyle=bM; ctx.lineWidth=7;
        ctx.beginPath(); ctx.moveTo(58,62); ctx.bezierCurveTo(70,50,78,36,72,24); ctx.stroke(); ctx.restore();
        // body (slightly horizontal/walking orientation)
        ctx.save(); ctx.beginPath(); ctx.ellipse(38,60,24,18,-.1,0,Math.PI*2);
        ctx.fillStyle=rg(ctx,38,52,24,bL,bD); sh(ctx,ha(hue,50,30,.35),8,0,4); ctx.fill(); ns(ctx);
        ctx.beginPath(); ctx.ellipse(38,62,15,11,-.1,0,Math.PI*2); ctx.fillStyle=ha(hue,35,88,.5); ctx.fill(); ctx.restore();
        // back legs
        ctx.save(); ctx.fillStyle=bD;
        ctx.beginPath(); ctx.ellipse(54,82,9,5,.25,0,Math.PI*2); ctx.fill();
        ctx.beginPath(); ctx.ellipse(64,78,9,5,.15,0,Math.PI*2); ctx.fill();
        ctx.fillStyle=bL;
        ctx.beginPath(); ctx.ellipse(54,81,7,3.5,.25,0,Math.PI*2); ctx.fill();
        ctx.beginPath(); ctx.ellipse(64,77,7,3.5,.15,0,Math.PI*2); ctx.fill(); ctx.restore();
        // front legs
        ctx.save(); ctx.fillStyle=bD;
        ctx.beginPath(); ctx.ellipse(18,80,9,5,-.25,0,Math.PI*2); ctx.fill();
        ctx.beginPath(); ctx.ellipse(30,84,9,5,-.1,0,Math.PI*2); ctx.fill();
        ctx.fillStyle=bL;
        ctx.beginPath(); ctx.ellipse(18,79,7,3.5,-.25,0,Math.PI*2); ctx.fill();
        ctx.beginPath(); ctx.ellipse(30,83,7,3.5,-.1,0,Math.PI*2); ctx.fill(); ctx.restore();
        // gills LEFT (5 fronds + bulbs) — spread wider
        ctx.save(); ctx.lineCap='round'; ctx.strokeStyle=gl; ctx.lineWidth=4;
        ctx.beginPath(); ctx.moveTo(16,32); ctx.quadraticCurveTo(4,18,2,6); ctx.stroke();
        ctx.beginPath(); ctx.moveTo(16,32); ctx.quadraticCurveTo(2,22,-2,12); ctx.stroke();
        ctx.beginPath(); ctx.moveTo(16,32); ctx.quadraticCurveTo(4,30,0,26); ctx.stroke();
        ctx.beginPath(); ctx.moveTo(16,32); ctx.quadraticCurveTo(8,38,4,38); ctx.stroke();
        ctx.beginPath(); ctx.moveTo(16,32); ctx.quadraticCurveTo(10,44,8,46); ctx.stroke();
        ctx.fillStyle=glT; ci(ctx,2,6,5); ctx.fill(); ci(ctx,-2,12,4.5); ctx.fill(); ci(ctx,0,26,4); ctx.fill(); ci(ctx,4,38,3.5); ctx.fill(); ci(ctx,8,46,3); ctx.fill();
        ctx.fillStyle=gl; ci(ctx,2,6,3); ctx.fill(); ci(ctx,-2,12,2.5); ctx.fill(); ci(ctx,0,26,2.2); ctx.fill(); ci(ctx,4,38,2); ctx.fill(); ci(ctx,8,46,1.8); ctx.fill();
        ctx.restore();
        // gills RIGHT (5 fronds + bulbs)
        ctx.save(); ctx.lineCap='round'; ctx.strokeStyle=gl; ctx.lineWidth=4;
        ctx.beginPath(); ctx.moveTo(56,32); ctx.quadraticCurveTo(68,18,70,6); ctx.stroke();
        ctx.beginPath(); ctx.moveTo(56,32); ctx.quadraticCurveTo(72,22,76,12); ctx.stroke();
        ctx.beginPath(); ctx.moveTo(56,32); ctx.quadraticCurveTo(68,30,72,26); ctx.stroke();
        ctx.beginPath(); ctx.moveTo(56,32); ctx.quadraticCurveTo(64,38,68,38); ctx.stroke();
        ctx.beginPath(); ctx.moveTo(56,32); ctx.quadraticCurveTo(62,44,64,46); ctx.stroke();
        ctx.fillStyle=glT; ci(ctx,70,6,5); ctx.fill(); ci(ctx,76,12,4.5); ctx.fill(); ci(ctx,72,26,4); ctx.fill(); ci(ctx,68,38,3.5); ctx.fill(); ci(ctx,64,46,3); ctx.fill();
        ctx.fillStyle=gl; ci(ctx,70,6,3); ctx.fill(); ci(ctx,76,12,2.5); ctx.fill(); ci(ctx,72,26,2.2); ctx.fill(); ci(ctx,68,38,2); ctx.fill(); ci(ctx,64,46,1.8); ctx.fill();
        ctx.restore();
        // head
        ctx.save(); ci(ctx,36,34,19); ctx.fillStyle=rg(ctx,36,28,19,bL,bD); sh(ctx,ha(hue,40,28,.3),6,0,3); ctx.fill(); ns(ctx); ctx.restore();
        // eyes
        ctx.save(); ctx.fillStyle='#1a1a2e';
        ctx.beginPath(); ctx.arc(28,32,5,0,Math.PI*2); ctx.fill();
        ctx.beginPath(); ctx.arc(44,32,5,0,Math.PI*2); ctx.fill();
        ctx.fillStyle='rgba(255,255,255,.9)'; ci(ctx,29.5,30.5,2); ctx.fill(); ci(ctx,45.5,30.5,2); ctx.fill(); ctx.restore();
        // smile
        ctx.save(); ctx.strokeStyle=ha(350,50,42,.65); ctx.lineWidth=1.8; ctx.lineCap='round';
        ctx.beginPath(); ctx.arc(36,40,4,.2,Math.PI-.2); ctx.stroke(); ctx.restore();
        // blush
        ctx.save(); ctx.fillStyle=ha(350,80,75,.38);
        ctx.beginPath(); ctx.ellipse(20,37,5,3,0,0,Math.PI*2); ctx.fill();
        ctx.beginPath(); ctx.ellipse(52,37,5,3,0,0,Math.PI*2); ctx.fill(); ctx.restore();
    }},

    kitsune: { draw(ctx, hue) {
        const fL = h(hue,78,60), fD = h(hue,78,38), wh = '#f8f3ee', no = '#c03348';
        // 5 fanned tails (behind body — draw before body)
        ctx.save(); ctx.lineCap='round';
        var tails = [
            {sx:26,sy:74, cx1:4,cy1:86, cx2:-2,cy2:68, ex:6,ey:56},
            {sx:32,sy:77, cx1:12,cy1:88, cx2:6,cy2:70, ex:14,ey:60},
            {sx:38,sy:79, cx1:28,cy1:90, cx2:18,cy2:76, ex:16,ey:66},
            {sx:44,sy:77, cx1:60,cy1:88, cx2:68,cy2:70, ex:62,ey:60},
            {sx:50,sy:74, cx1:70,cy1:84, cx2:76,cy2:66, ex:70,ey:56},
        ];
        tails.forEach(function(t) {
            ctx.strokeStyle=fD; ctx.lineWidth=16;
            ctx.beginPath(); ctx.moveTo(t.sx,t.sy); ctx.bezierCurveTo(t.cx1,t.cy1,t.cx2,t.cy2,t.ex,t.ey); ctx.stroke();
            ctx.strokeStyle=fL; ctx.lineWidth=11;
            ctx.beginPath(); ctx.moveTo(t.sx,t.sy); ctx.bezierCurveTo(t.cx1,t.cy1,t.cx2,t.cy2,t.ex,t.ey); ctx.stroke();
            ctx.fillStyle=wh; ci(ctx,t.ex,t.ey,7); ctx.fill();
            ctx.fillStyle='rgba(255,255,255,.55)'; ci(ctx,t.ex-1,t.ey-1,4); ctx.fill();
        });
        ctx.restore();
        // body (compact round)
        ctx.save(); ctx.beginPath(); ctx.ellipse(38,66,19,21,0,0,Math.PI*2);
        ctx.fillStyle=rg(ctx,38,56,19,fL,fD); sh(ctx,ha(hue,45,22,.35),8,0,4); ctx.fill(); ns(ctx);
        ctx.beginPath(); ctx.ellipse(38,68,11,14,0,0,Math.PI*2); ctx.fillStyle=wh; ctx.fill(); ctx.restore();
        // paws
        ctx.save(); ctx.fillStyle=fD;
        ctx.beginPath(); ctx.ellipse(24,87,9,6,-.2,0,Math.PI*2); ctx.fill();
        ctx.beginPath(); ctx.ellipse(52,87,9,6,.2,0,Math.PI*2); ctx.fill();
        ctx.fillStyle=fL;
        ctx.beginPath(); ctx.ellipse(24,86,7,4,-.2,0,Math.PI*2); ctx.fill();
        ctx.beginPath(); ctx.ellipse(52,86,7,4,.2,0,Math.PI*2); ctx.fill(); ctx.restore();
        // fox ears
        ctx.save(); ctx.fillStyle=fD;
        ctx.beginPath(); ctx.moveTo(22,26); ctx.lineTo(14,5); ctx.lineTo(34,18); ctx.closePath(); ctx.fill();
        ctx.beginPath(); ctx.moveTo(54,26); ctx.lineTo(62,5); ctx.lineTo(44,18); ctx.closePath(); ctx.fill();
        ctx.fillStyle=ha(350,55,78,.6);
        ctx.beginPath(); ctx.moveTo(23,25); ctx.lineTo(17,9); ctx.lineTo(32,19); ctx.closePath(); ctx.fill();
        ctx.beginPath(); ctx.moveTo(53,25); ctx.lineTo(59,9); ctx.lineTo(46,19); ctx.closePath(); ctx.fill(); ctx.restore();
        // head
        ctx.save(); ci(ctx,38,28,20); ctx.fillStyle=rg(ctx,38,22,20,fL,fD); sh(ctx,ha(hue,40,22,.3),6,0,3); ctx.fill(); ns(ctx); ctx.restore();
        // white face marking
        ctx.save(); ctx.beginPath(); ctx.ellipse(38,34,10,8,0,0,Math.PI*2); ctx.fillStyle=wh; ctx.fill(); ctx.restore();
        // kitsune mask (near right ear)
        ctx.save(); ctx.translate(57,14); ctx.rotate(.4);
        rr(ctx,-9,-7,18,14,[5]); ctx.fillStyle='rgba(248,240,230,.92)'; sh(ctx,'rgba(0,0,0,.2)',3); ctx.fill(); ns(ctx);
        ctx.strokeStyle=fD; ctx.lineWidth=1.5;
        ctx.beginPath(); ctx.arc(0,-1,4,Math.PI+.5,-.5); ctx.stroke();
        ctx.fillStyle=fD; ci(ctx,-3,-3,1.8); ctx.fill(); ci(ctx,3,-3,1.8); ctx.fill();
        ctx.restore();
        // eyes (closed, happy arcs)
        ctx.save(); ctx.strokeStyle='#1a0a00'; ctx.lineWidth=2.8; ctx.lineCap='round';
        ctx.beginPath(); ctx.arc(29,28,5,Math.PI+.35,-.35); ctx.stroke();
        ctx.beginPath(); ctx.arc(47,28,5,Math.PI+.35,-.35); ctx.stroke(); ctx.restore();
        // nose + smile
        ctx.save(); ctx.fillStyle=no;
        ctx.beginPath(); ctx.moveTo(38,33); ctx.lineTo(35.5,36); ctx.lineTo(40.5,36); ctx.closePath(); ctx.fill();
        ctx.strokeStyle=ha(350,45,35,.6); ctx.lineWidth=1.8; ctx.lineCap='round';
        ctx.beginPath(); ctx.moveTo(38,36); ctx.quadraticCurveTo(34,40,31,39); ctx.stroke();
        ctx.beginPath(); ctx.moveTo(38,36); ctx.quadraticCurveTo(42,40,45,39); ctx.stroke(); ctx.restore();
        // blush
        ctx.save(); ctx.fillStyle=ha(350,75,75,.42);
        ctx.beginPath(); ctx.ellipse(21,34,5.5,3.5,0,0,Math.PI*2); ctx.fill();
        ctx.beginPath(); ctx.ellipse(55,34,5.5,3.5,0,0,Math.PI*2); ctx.fill(); ctx.restore();
    }},

    cat: { draw(ctx, hue) {
        const bd = h(hue,32,14), bdL = h(hue,28,22),
              ey = h((hue+155)%360,75,58), col = h(hue,68,52), tag = h(hue,62,64);
        // tail (long, curls right then loops back)
        ctx.save(); ctx.strokeStyle=h(hue,30,18); ctx.lineWidth=10; ctx.lineCap='round';
        ctx.beginPath(); ctx.moveTo(52,70); ctx.bezierCurveTo(68,64,72,48,62,42); ctx.bezierCurveTo(56,38,50,46,56,54); ctx.stroke();
        ctx.strokeStyle=bdL; ctx.lineWidth=7;
        ctx.beginPath(); ctx.moveTo(52,70); ctx.bezierCurveTo(68,64,72,48,62,42); ctx.bezierCurveTo(56,38,50,46,56,54); ctx.stroke(); ctx.restore();
        // body
        ctx.save(); ctx.beginPath(); ctx.ellipse(37,66,22,24,0,0,Math.PI*2);
        ctx.fillStyle=rg(ctx,37,55,22,bdL,bd); sh(ctx,ha(hue,25,10,.4),8,0,4); ctx.fill(); ns(ctx); ctx.restore();
        // paws
        ctx.save(); ctx.fillStyle=bd;
        ctx.beginPath(); ctx.ellipse(25,88,10,6,-.15,0,Math.PI*2); ctx.fill();
        ctx.beginPath(); ctx.ellipse(49,88,10,6,.15,0,Math.PI*2); ctx.fill();
        ctx.fillStyle=bdL;
        ctx.beginPath(); ctx.ellipse(25,87,8,4.5,-.15,0,Math.PI*2); ctx.fill();
        ctx.beginPath(); ctx.ellipse(49,87,8,4.5,.15,0,Math.PI*2); ctx.fill(); ctx.restore();
        // ears (behind head)
        ctx.save(); ctx.fillStyle=bd;
        ctx.beginPath(); ctx.moveTo(22,26); ctx.lineTo(14,6); ctx.lineTo(34,18); ctx.closePath(); ctx.fill();
        ctx.beginPath(); ctx.moveTo(54,26); ctx.lineTo(62,6); ctx.lineTo(44,18); ctx.closePath(); ctx.fill();
        ctx.fillStyle=ha(340,55,45,.5);
        ctx.beginPath(); ctx.moveTo(24,24); ctx.lineTo(18,10); ctx.lineTo(32,19); ctx.closePath(); ctx.fill();
        ctx.beginPath(); ctx.moveTo(52,24); ctx.lineTo(58,10); ctx.lineTo(46,19); ctx.closePath(); ctx.fill(); ctx.restore();
        // head
        ctx.save(); ctx.beginPath(); ctx.ellipse(38,27,22,21,0,0,Math.PI*2);
        ctx.fillStyle=rg(ctx,38,20,22,bdL,bd); sh(ctx,ha(hue,25,10,.35),6,0,3); ctx.fill(); ns(ctx); ctx.restore();
        // collar arc
        ctx.save(); ctx.strokeStyle=col; ctx.lineWidth=6; ctx.lineCap='butt';
        ctx.beginPath(); ctx.arc(38,47,16,0.3,Math.PI-0.3); ctx.stroke();
        ci(ctx,38,55,5); ctx.fillStyle=tag; sh(ctx,tag,5); ctx.fill(); ns(ctx);
        ctx.fillStyle='rgba(255,255,255,.55)'; ci(ctx,36.8,53.8,1.8); ctx.fill(); ctx.restore();
        // eyes
        ctx.save(); ctx.fillStyle='#fff';
        ctx.beginPath(); ctx.ellipse(28,25,6.5,7.5,-.1,0,Math.PI*2); ctx.fill();
        ctx.beginPath(); ctx.ellipse(48,25,6.5,7.5,.1,0,Math.PI*2); ctx.fill();
        ctx.fillStyle=ey;
        ctx.beginPath(); ctx.ellipse(28,26,4.5,6,0,0,Math.PI*2); ctx.fill();
        ctx.beginPath(); ctx.ellipse(48,26,4.5,6,0,0,Math.PI*2); ctx.fill();
        ctx.fillStyle='#0d0d16';
        ctx.beginPath(); ctx.ellipse(28,26,1.8,5,0,0,Math.PI*2); ctx.fill();
        ctx.beginPath(); ctx.ellipse(48,26,1.8,5,0,0,Math.PI*2); ctx.fill();
        ctx.fillStyle='rgba(255,255,255,.9)'; ci(ctx,29.5,24,1.4); ctx.fill(); ci(ctx,49.5,24,1.4); ctx.fill(); ctx.restore();
        // nose + smile
        ctx.save(); ctx.fillStyle=ha(340,55,55,.8);
        ctx.beginPath(); ctx.moveTo(38,34); ctx.lineTo(35.5,37); ctx.lineTo(40.5,37); ctx.closePath(); ctx.fill();
        ctx.strokeStyle=ha(340,30,40,.5); ctx.lineWidth=1.5; ctx.lineCap='round';
        ctx.beginPath(); ctx.moveTo(38,37); ctx.quadraticCurveTo(34,41,31,40); ctx.stroke();
        ctx.beginPath(); ctx.moveTo(38,37); ctx.quadraticCurveTo(42,41,45,40); ctx.stroke(); ctx.restore();
        // whiskers
        ctx.save(); ctx.strokeStyle=ha(0,0,80,.6); ctx.lineWidth=1.2; ctx.lineCap='round';
        [[11,35,27,34],[11,38,27,37],[11,41,27,40]].forEach(function(w){ ctx.beginPath(); ctx.moveTo(w[0],w[1]); ctx.lineTo(w[2],w[3]); ctx.stroke(); });
        [[65,35,49,34],[65,38,49,37],[65,41,49,40]].forEach(function(w){ ctx.beginPath(); ctx.moveTo(w[0],w[1]); ctx.lineTo(w[2],w[3]); ctx.stroke(); }); ctx.restore();
        // body sheen (black cat highlight)
        ctx.save(); ctx.fillStyle=ha(hue,25,38,.12);
        ctx.beginPath(); ctx.ellipse(30,54,8,15,-.3,0,Math.PI*2); ctx.fill(); ctx.restore();
    }},

    dog: { draw(ctx, hue) {
        const fL = h(hue,65,72), fD = h(hue,65,48), fM = h(hue,62,58), mu = h(hue,50,82);
        // tail (small curl, mid-right — not going high)
        ctx.save(); ctx.strokeStyle=fD; ctx.lineWidth=9; ctx.lineCap='round';
        ctx.beginPath(); ctx.moveTo(56,65); ctx.bezierCurveTo(68,60,72,50,64,46); ctx.stroke();
        ctx.strokeStyle=fL; ctx.lineWidth=6;
        ctx.beginPath(); ctx.moveTo(56,65); ctx.bezierCurveTo(68,60,72,50,64,46); ctx.stroke(); ctx.restore();
        // body
        ctx.save(); ctx.beginPath(); ctx.ellipse(37,66,23,22,0,0,Math.PI*2);
        ctx.fillStyle=rg(ctx,37,56,23,fL,fD); sh(ctx,ha(hue,40,25,.3),8,0,4); ctx.fill(); ns(ctx); ctx.restore();
        // paws
        ctx.save(); ctx.fillStyle=fD;
        ctx.beginPath(); ctx.ellipse(24,89,10,7,-.1,0,Math.PI*2); ctx.fill();
        ctx.beginPath(); ctx.ellipse(50,89,10,7,.1,0,Math.PI*2); ctx.fill();
        ctx.fillStyle=fM;
        ctx.beginPath(); ctx.ellipse(24,88,8,5,-.1,0,Math.PI*2); ctx.fill();
        ctx.beginPath(); ctx.ellipse(50,88,8,5,.1,0,Math.PI*2); ctx.fill(); ctx.restore();
        // floppy ears
        ctx.save(); ctx.fillStyle=fD;
        ctx.beginPath(); ctx.ellipse(17,34,9,17,.2,0,Math.PI*2); ctx.fill();
        ctx.beginPath(); ctx.ellipse(59,34,9,17,-.2,0,Math.PI*2); ctx.fill(); ctx.restore();
        // head
        ctx.save(); ci(ctx,38,28,22); ctx.fillStyle=rg(ctx,38,20,22,fL,fD); sh(ctx,ha(hue,40,22,.3),6,0,3); ctx.fill(); ns(ctx); ctx.restore();
        // collar (fixed blue) + tag
        ctx.save(); ctx.strokeStyle='#3a7bd5'; ctx.lineWidth=7; ctx.lineCap='butt';
        ctx.beginPath(); ctx.arc(38,46,18,0.35,Math.PI-0.35); ctx.stroke();
        ci(ctx,38,56,5.5); ctx.fillStyle='#5599ee'; sh(ctx,'#5599ee',6); ctx.fill(); ns(ctx);
        ctx.fillStyle='rgba(255,255,255,.55)'; ci(ctx,36.5,54.5,2); ctx.fill(); ctx.restore();
        // muzzle
        ctx.save(); ctx.beginPath(); ctx.ellipse(38,38,14,11,0,0,Math.PI*2); ctx.fillStyle=mu; ctx.fill(); ctx.restore();
        // eyes
        ctx.save(); ctx.fillStyle='#fff';
        ctx.beginPath(); ctx.ellipse(27,25,6.5,7.5,-.1,0,Math.PI*2); ctx.fill();
        ctx.beginPath(); ctx.ellipse(49,25,6.5,7.5,.1,0,Math.PI*2); ctx.fill();
        ctx.fillStyle='#1a0800';
        ctx.beginPath(); ctx.arc(27,26,4.2,0,Math.PI*2); ctx.fill();
        ctx.beginPath(); ctx.arc(49,26,4.2,0,Math.PI*2); ctx.fill();
        ctx.fillStyle='rgba(255,255,255,.9)'; ci(ctx,28.5,24,1.8); ctx.fill(); ci(ctx,50.5,24,1.8); ctx.fill(); ctx.restore();
        // nose
        ctx.save(); ctx.fillStyle='#2a1000'; ctx.beginPath(); ctx.ellipse(38,35,5.5,4.5,0,0,Math.PI*2); ctx.fill(); ctx.restore();
        // smile + tongue
        ctx.save(); ctx.strokeStyle=ha(15,40,25,.75); ctx.lineWidth=2; ctx.lineCap='round';
        ctx.beginPath(); ctx.arc(38,39,5,.15,Math.PI-.15); ctx.stroke();
        ctx.fillStyle='#e05060'; ctx.beginPath(); ctx.ellipse(38,45,4.5,4,0,0,Math.PI); ctx.fill(); ctx.restore();
        // blush
        ctx.save(); ctx.fillStyle=ha(350,80,72,.38);
        ctx.beginPath(); ctx.ellipse(19,32,5.5,3.5,0,0,Math.PI*2); ctx.fill();
        ctx.beginPath(); ctx.ellipse(57,32,5.5,3.5,0,0,Math.PI*2); ctx.fill(); ctx.restore();
    }},

    ghost: { draw(ctx, hue) {
        const bC = ha(hue,40,94,.95), bD = ha(hue,50,78,.75), gl = ha(hue,65,82,.3), eC = h((hue+180)%360,60,46);
        // outer glow
        ctx.save(); var gw = ctx.createRadialGradient(40,44,8,40,44,46); gw.addColorStop(0,gl); gw.addColorStop(1,'transparent'); ctx.fillStyle=gw; ci(ctx,40,44,46); ctx.fill(); ctx.restore();
        // body: dome top + 3-bump skirt
        ctx.save();
        ctx.beginPath();
        ctx.arc(40,32,28,Math.PI,0);
        ctx.lineTo(68,70);
        ctx.quadraticCurveTo(62,82,55,70);
        ctx.quadraticCurveTo(48,58,40,70);
        ctx.quadraticCurveTo(32,82,25,70);
        ctx.quadraticCurveTo(18,58,12,70);
        ctx.closePath();
        var bg = ctx.createRadialGradient(34,22,3,40,42,36); bg.addColorStop(0,bC); bg.addColorStop(1,bD);
        ctx.fillStyle=bg; sh(ctx,ha(hue,55,52,.5),16,0,4); ctx.fill(); ns(ctx);
        var sg = ctx.createRadialGradient(52,68,2,52,68,32); sg.addColorStop(0,'transparent'); sg.addColorStop(1,ha(hue,35,62,.18));
        ctx.fillStyle=sg; ctx.fill(); ctx.restore();
        // arm nubs
        ctx.save(); ctx.fillStyle=bC;
        ctx.beginPath(); ctx.ellipse(10,52,7,5,.3,0,Math.PI*2); ctx.fill();
        ctx.beginPath(); ctx.ellipse(70,52,7,5,-.3,0,Math.PI*2); ctx.fill(); ctx.restore();
        // eyes
        ctx.save(); ctx.fillStyle='rgba(22,22,45,.85)';
        ctx.beginPath(); ctx.ellipse(30,36,7.5,9,-.1,0,Math.PI*2); ctx.fill();
        ctx.beginPath(); ctx.ellipse(50,36,7.5,9,.1,0,Math.PI*2); ctx.fill();
        ctx.fillStyle=eC;
        ctx.beginPath(); ctx.ellipse(30,37,5,7,0,0,Math.PI*2); ctx.fill();
        ctx.beginPath(); ctx.ellipse(50,37,5,7,0,0,Math.PI*2); ctx.fill();
        ctx.fillStyle='rgba(255,255,255,.88)'; ci(ctx,28,34,2.4); ctx.fill(); ci(ctx,48,34,2.4); ctx.fill(); ctx.restore();
        // smile
        ctx.save(); ctx.strokeStyle='rgba(22,22,45,.55)'; ctx.lineWidth=2; ctx.lineCap='round';
        ctx.beginPath(); ctx.arc(40,54,5,.2,Math.PI-.2); ctx.stroke(); ctx.restore();
        // blush
        ctx.save(); ctx.fillStyle=ha(350,70,78,.32); ctx.beginPath(); ctx.ellipse(21,43,7,4,0,0,Math.PI*2); ctx.fill(); ctx.beginPath(); ctx.ellipse(59,43,7,4,0,0,Math.PI*2); ctx.fill(); ctx.restore();
        // sparkles
        ctx.save(); ctx.fillStyle=ha(hue,80,88,.65); [[9,18,3],[67,26,2.5],[10,56,2.2],[69,60,2.5],[40,6,2.2]].forEach(function(s){ ci(ctx,s[0],s[1],s[2]); ctx.fill(); }); ctx.restore();
    }},

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
const SPRITE_DEFAULT_HUES = { fairy:130, cat:260, dog:28, ghost:220, axolotl:320, kitsune:20, robot:195 };

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
        var drawY = floorY - spriteH + bob;   // sprite top-left Y
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

// ─── Static helper: draw a sprite onto any canvas ───────────────────────────
function drawSpriteToCanvas(canvas, spriteType, hue) {
    var ctx = canvas.getContext('2d');
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    if (SPRITES[spriteType]) SPRITES[spriteType].draw(ctx, hue);
}
