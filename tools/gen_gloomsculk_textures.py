#!/usr/bin/env python3
"""
Generate the "gloomsculk" texture set: sculk-derived terrain and flora for the
Gloomwood biome.

Every colour in the output is sampled from art that already ships - vanilla
``sculk.png`` / ``magma.png`` and BetterNether's gloomwood blocks - so the new
set stays in sync if any of those sources are retouched. Nothing is
hand-picked, the ramps are read out of the source images at run time.

Blocks
------
bleached_gloomsculk  terrain; sculk body with a pale bleached crust on top,
                 the crust reads like ``gloomwood_leaves_alt``. The side keeps
                 sculk's own 4-frame animation so it stays in step with
                 neighbouring vanilla sculk. Bottom face reuses
                 ``minecraft:block/sculk``.
molten_gloomsculk  terrain; sculk crazed with lava fissures that pulse over 8
                 frames. Top and side faces are generated as a pair: the
                 fissures cross every edge at the same offsets, so they run on
                 into the neighbouring block and over the top edge down the
                 sides.
gloomwisp_vine   flora; thin stalk plus an elongated head carrying a small
                 ghost face. Head eyes and mouth are emissive.
pale_gloomgrass_1..3  flora; the bone-grass silhouettes recoloured with the
                 gloomwood transition-log gradient (dark base, bright tips).
gloomgrass_1..3  flora; the same silhouettes on sculk's own ramp - the dark
                 variant that grows mixed in with the pale one.

Usage
-----
    python3 tools/gen_gloomsculk_textures.py                # write into assets
    python3 tools/gen_gloomsculk_textures.py --out /tmp/x   # dry run elsewhere
    python3 tools/gen_gloomsculk_textures.py --preview p.png

Requires Pillow.
"""

from __future__ import annotations

import argparse
import json
import math
import os
import random
from typing import Dict, List, Sequence, Tuple

from PIL import Image

RGBA = Tuple[int, int, int, int]

SIZE = 16
SEED = 0x5C17

# How many distinct pale tones the bleached crust may use (see bleached_top).
PALE_STEPS = 8

# Frames in the shipped bleached_gloomsculk sprites - the base the molten variant is cut out of.
BLEACHED_FRAMES = 4

# The pale gloomgrass shadow lift (see `lift_colour`). LIFT_BLACK is the darkest tone the
# transition-log gradient puts in the tuft today; it lands on LIFT_FLOOR instead, everything at or
# above LIFT_PIVOT is untouched, and the range between is a straight ramp.
LIFT_BLACK = 44.0
LIFT_FLOOR = 78.0
LIFT_PIVOT = 140.0

# Repo layout: <root>/BetterNether/tools/<this file>, <root>/minecraft/...
_TOOLS = os.path.dirname(os.path.abspath(__file__))
_MOD = os.path.dirname(_TOOLS)
_ROOT = os.path.dirname(_MOD)

DEFAULT_VANILLA = os.path.join(
    _ROOT, "minecraft", "src", "main", "resources", "assets", "minecraft", "textures", "block"
)
DEFAULT_ASSETS = os.path.join(
    _MOD, "src", "main", "resources", "assets", "betternether", "textures", "block"
)


# --------------------------------------------------------------------------
# image helpers
# --------------------------------------------------------------------------

def load(directory: str, name: str) -> Image.Image:
    return Image.open(os.path.join(directory, name)).convert("RGBA")


def frames(strip: Image.Image, count: int) -> List[Image.Image]:
    """Split a vertical animation strip into its frames."""
    h = strip.height // count
    return [strip.crop((0, i * h, strip.width, (i + 1) * h)) for i in range(count)]


def luma(c: Sequence[int]) -> float:
    return 0.2126 * c[0] + 0.7152 * c[1] + 0.0722 * c[2]


def saturation(c: Sequence[int]) -> int:
    return max(c[:3]) - min(c[:3])


def opaque_colors(img: Image.Image) -> List[RGBA]:
    return [c for c in set(img.getdata()) if c[3] == 255]


def split_ramp(img: Image.Image, spark_sat: int = 60) -> Tuple[List[RGBA], List[RGBA]]:
    """Return (body ramp sorted dark->bright, saturated sparkle colours)."""
    body, sparks = [], []
    for c in opaque_colors(img):
        (sparks if saturation(c) >= spark_sat and c[2] >= c[0] else body).append(c)
    body.sort(key=luma)
    sparks.sort(key=luma)
    return body, sparks


def pick(ramp: Sequence[RGBA], t: float) -> RGBA:
    """Sample a ramp with t in [0, 1]."""
    if not ramp:
        return (0, 0, 0, 255)
    i = int(round(max(0.0, min(1.0, t)) * (len(ramp) - 1)))
    return ramp[i]


def mix(a: Sequence[int], b: Sequence[int], t: float) -> RGBA:
    t = max(0.0, min(1.0, t))
    return tuple(int(round(a[i] + (b[i] - a[i]) * t)) for i in range(4))  # type: ignore


def lift_colour(c: Sequence[int]) -> RGBA:
    """Raise a dark colour towards LIFT_FLOOR, leaving anything at or above LIFT_PIVOT alone.

    A black-point lift rather than a brightness or gamma change: only the bottom of the range moves,
    so the highlights and the white tips stay exactly as authored. The new luminance is applied as a
    scale factor on RGB, which keeps the hue - adding a constant would wash the colour towards grey.
    """
    old = 0.299 * c[0] + 0.587 * c[1] + 0.114 * c[2]   # Rec.601, as perceived brightness
    if c[3] == 0 or old >= LIFT_PIVOT or old <= 0:
        return tuple(c)  # type: ignore
    new = LIFT_FLOOR + (old - LIFT_BLACK) * (LIFT_PIVOT - LIFT_FLOOR) / (LIFT_PIVOT - LIFT_BLACK)
    f = new / old
    return (min(255, round(c[0] * f)), min(255, round(c[1] * f)), min(255, round(c[2] * f)), c[3])


def lift_shadows(img: Image.Image) -> Image.Image:
    out = blank(img.width, img.height)
    out.putdata([lift_colour(c) for c in img.getdata()])
    return out


def blank(w: int = SIZE, h: int = SIZE) -> Image.Image:
    return Image.new("RGBA", (w, h), (0, 0, 0, 0))


def stack(imgs: Sequence[Image.Image]) -> Image.Image:
    """Join frames into a vertical animation strip."""
    out = Image.new("RGBA", (imgs[0].width, imgs[0].height * len(imgs)), (0, 0, 0, 0))
    for i, im in enumerate(imgs):
        out.paste(im, (0, i * im.height))
    return out


# --------------------------------------------------------------------------
# tiling value noise
# --------------------------------------------------------------------------

def noise(rng: random.Random, period: int, size: int = SIZE) -> List[List[float]]:
    """Bilinear value noise on a `period`-cell lattice that wraps at `size`."""
    grid = [[rng.random() for _ in range(period)] for _ in range(period)]
    step = size / period
    out = []
    for y in range(size):
        gy, fy = divmod(y / step, 1.0)
        gy = int(gy)
        fy = fy * fy * (3 - 2 * fy)  # smoothstep
        row = []
        for x in range(size):
            gx, fx = divmod(x / step, 1.0)
            gx = int(gx)
            fx = fx * fx * (3 - 2 * fx)
            g00 = grid[gy % period][gx % period]
            g10 = grid[gy % period][(gx + 1) % period]
            g01 = grid[(gy + 1) % period][gx % period]
            g11 = grid[(gy + 1) % period][(gx + 1) % period]
            row.append((g00 * (1 - fx) + g10 * fx) * (1 - fy) + (g01 * (1 - fx) + g11 * fx) * fy)
        out.append(row)
    return out


def normalize(field: List[List[float]]) -> List[List[float]]:
    """Rescale a field to exactly [0, 1] so thresholds against it are meaningful."""
    lo = min(min(r) for r in field)
    hi = max(max(r) for r in field)
    if hi - lo < 1e-6:
        return [[0.5] * SIZE for _ in range(SIZE)]
    return [[(v - lo) / (hi - lo) for v in row] for row in field]


def fbm(rng: random.Random, periods: Sequence[int], weights: Sequence[float]) -> List[List[float]]:
    layers = [noise(rng, p) for p in periods]
    total = sum(weights)
    out = []
    for y in range(SIZE):
        out.append([sum(l[y][x] * w for l, w in zip(layers, weights)) / total for x in range(SIZE)])
    return normalize(out)


# --------------------------------------------------------------------------
# sources
# --------------------------------------------------------------------------

class Sources:
    def __init__(self, vanilla: str, assets: str):
        self.vanilla = vanilla
        self.assets = assets
        self.sculk = frames(load(vanilla, "sculk.png"), 4)
        self.magma = frames(load(vanilla, "magma.png"), 3)[0]

        leaves_alt = load(assets, "gloomwood_leaves_alt.png")
        self.leaves_alt = leaves_alt

        # The bleached crust as it actually ships, read back out of the assets.
        #
        # `bleached_top`/`bleached_side` below still build the first version of that block - one static
        # tile - but the shipped art has since been reworked into four animated frames with three
        # variants, and those are what a player sees on the floor. The molten variant is cut out of
        # this rather than out of `bleached_top(src)` for exactly that reason: the two blocks lie next
        # to each other in the same biome, and a molten tile built from the older crust would read as a
        # third kind of rock rather than as the same crust with lava in it.
        self.bleached = {
            face: (
                frames(load(assets, "bleached_gloomsculk_%s.png" % face), BLEACHED_FRAMES),
                frames(load(assets, "bleached_gloomsculk_%s_e.png" % face), BLEACHED_FRAMES),
            )
            for face in ("top", "side")
        }

        # The pale ramp comes from the gloomwood log, not from the bleached
        # leaves, even though the leaves are what the crust is shaped like.
        # gloomwood_leaves_alt is a leaf texture: whether it currently reads
        # pale-on-dark or dark-on-pale is an art decision that flips as the
        # tree is retouched, and when it flipped dark it took "bleached"
        # gloomsculk down to near-black with it. The log's bone tones are what
        # "bleached" actually means here, so the ramp is anchored there.
        # Sparkles still come from the leaves - those stay cyan either way.
        self.pale, _ = split_ramp(load(assets, "gloomwood_log_side.png"))
        _, self.spark = split_ramp(leaves_alt)

        # gloomwood's dark navy ramp, and the dark->bright transition gradient
        self.dark, _ = split_ramp(load(assets, "gloomwood_dark_log_side.png"))
        self.transition = load(assets, "gloomwood_transition_stripped_log_side.png")

        # Sculk's own colours: substrate tones plus its bright cyan speck.
        # Every frame is scanned, not just the first - the brightest speck only
        # appears in some of them, and taking frame 0 alone silently drops the
        # one colour the sparkles are supposed to be picking up.
        sculk_body: List[RGBA] = []
        sculk_spark: List[RGBA] = []
        for f in self.sculk:
            b, s = split_ramp(f, spark_sat=90)
            sculk_body += b
            sculk_spark += s
        self.sculk_body = sorted(set(sculk_body), key=luma)
        self.spark = sorted(set(self.spark) | set(sculk_spark), key=luma)

        # Heat ramp straight off magma - cooled embers through to the hot core -
        # capped with a white-hot tone the vanilla texture does not contain,
        # so the thickest part of a crack has somewhere brighter to go.
        magma_cols = sorted(opaque_colors(self.magma), key=luma)
        self.ember = [c for c in magma_cols if luma(c) < 90]
        self.lava = [c for c in magma_cols if luma(c) >= 90]
        white_hot = mix(self.lava[-1], (255, 248, 214, 255), 0.55)
        self.heat = self.ember + self.lava + [white_hot]

    def transition_at(self, x: int, y: int) -> RGBA:
        return self.transition.getpixel((x % SIZE, max(0, min(SIZE - 1, y))))


# --------------------------------------------------------------------------
# bleached_sculk
# --------------------------------------------------------------------------

def solidify(img: Image.Image) -> Image.Image:
    """Grow opaque pixels into the transparent gaps until the tile is solid.

    Turns the bleached leaf texture into a continuous crust while keeping its
    blotch shapes, so the terrain cover visually belongs to the same tree.
    """
    px = {(x, y): img.getpixel((x, y)) for x in range(SIZE) for y in range(SIZE)}
    while any(p[3] == 0 for p in px.values()):
        grown = {}
        for (x, y), c in px.items():
            if c[3] != 0:
                continue
            near = [
                px[((x + dx) % SIZE, (y + dy) % SIZE)]
                for dx in (-1, 0, 1)
                for dy in (-1, 0, 1)
                if (dx or dy) and px[((x + dx) % SIZE, (y + dy) % SIZE)][3] != 0
            ]
            if near:
                n = len(near)
                grown[(x, y)] = (
                    int(sum(p[0] for p in near) / n * 0.92),
                    int(sum(p[1] for p in near) / n * 0.92),
                    int(sum(p[2] for p in near) / n * 0.92),
                    255,
                )
        if not grown:
            break
        px.update(grown)
    out = blank()
    for (x, y), c in px.items():
        out.putpixel((x, y), c)
    return out


def bleached_top(src: Sources) -> Tuple[Image.Image, Image.Image]:
    rng = random.Random(SEED + 1)
    crust = solidify(src.leaves_alt)
    clouds = fbm(rng, (4, 8), (1.0, 0.45))
    # the crust's own luminance, stretched so it uses the whole pale ramp
    crust_l = normalize([[luma(crust.getpixel((x, y))) for x in range(SIZE)] for y in range(SIZE)])

    out, emit = blank(), blank()
    for y in range(SIZE):
        for x in range(SIZE):
            # re-quantise onto the pale ramp, biased by the cloud field so the
            # leaf blotches turn into moss patches
            # Snapped to a handful of steps rather than sampled continuously.
            # The pale ramp is two dozen tones wide, and letting the crust use
            # all of them turns the mottling into per-pixel static instead of
            # the lichen patches it is meant to read as.
            t = crust_l[y][x] * 0.6 + clouds[y][x] * 0.4
            c = pick(src.pale, round(t * (PALE_STEPS - 1)) / (PALE_STEPS - 1))
            # sculk bleeding through where the crust is thinnest, plus a rim of
            # part-overgrown pixels so the patches do not have hard edges
            bleed = clouds[y][x] * 0.55 + crust_l[y][x] * 0.45
            if bleed < 0.16:
                c = mix(c, pick(src.sculk_body, 0.2), 0.85)
            elif bleed < 0.30:
                c = mix(c, pick(src.sculk_body, 0.6), 0.45)
            out.putpixel((x, y), c)

    for _ in range(5):
        x, y = rng.randrange(SIZE), rng.randrange(SIZE)
        c = pick(src.spark, rng.random())
        out.putpixel((x, y), c)
        emit.putpixel((x, y), c)
    return out, emit


def bleached_side(src: Sources, top: Image.Image) -> Tuple[Image.Image, Image.Image]:
    rng = random.Random(SEED + 2)
    edge = normalize(noise(rng, 4))
    # crust hangs 3-6px down the side, ragged per column
    depth = [3 + int(edge[0][x] * 3.99) for x in range(SIZE)]
    drips = {x for x in range(SIZE) if edge[3][x] > 0.72}

    frames_out, emits = [], []
    for f, sculk in enumerate(src.sculk):
        img = sculk.copy()
        emit = blank()
        for x in range(SIZE):
            d = depth[x]
            for y in range(d):
                # carry the top face's mottling over the lip, shading down as
                # the crust thins out into the sculk
                c = mix(top.getpixel((x, (SIZE - 1 - y) % SIZE)), pick(src.dark, 0.35), (y / d) * 0.6)
                img.putpixel((x, y), c)
            if x in drips:
                img.putpixel((x, d), mix(pick(src.pale, 0.15), sculk.getpixel((x, d)), 0.35))
        # carry the crust sparkles onto the lip, offset per frame so they twinkle
        for i in range(3):
            x = (rng.randrange(SIZE) + f * 5) % SIZE
            y = rng.randrange(max(1, depth[x]))
            c = pick(src.spark, rng.random())
            img.putpixel((x, y), c)
            emit.putpixel((x, y), c)
        # sculk's own bright specks below the crust stay emissive
        for y in range(SIZE):
            for x in range(SIZE):
                if y >= depth[x] and saturation(sculk.getpixel((x, y))) >= 90:
                    emit.putpixel((x, y), sculk.getpixel((x, y)))
        frames_out.append(img)
        emits.append(emit)
    return stack(frames_out), stack(emits)


# --------------------------------------------------------------------------
# molten_gloomsculk
# --------------------------------------------------------------------------

MOLTEN_FRAMES = 8

# How many phase-shifted copies of the molten log's side to emit, and the suffixes they take. The empty
# one is the sprite the block is named after; the rest are the alternates the log model rolls between.
# The geode core's swirl: how many arms the spiral has and how hard they wind with radius.
CORE_ARMS = 3
CORE_TWIST = 7.0

MOLTEN_LOG_PHASES = 4
MOLTEN_LOG_SUFFIXES = ("", "_b", "_c", "_d")

# How wide a crack is at a given point, and therefore how hot it reads. A
# hairline fracture only glows a dull ember; where two cracks meet the rock has
# pulled right open and the lava underneath shows through white-hot.
W_HALO = 0.16   # scorched rock beside the crack
W_THIN = 0.34   # an ordinary stretch of fissure - stays in the ember range
W_WIDE = 0.66   # where a fissure has opened up
W_CORE = 1.00   # a junction, where two fissures meet

# Where a crack leaves the tile, as an offset along the edge.
#
# The same pair is used on all four edges of the top face and on the top and
# bottom of the side face, which is what lets the cracks run from block to
# block and over the top edge down the sides. It has to be *mirror-symmetric*
# about the tile centre: the side texture is used on all four faces, and the
# UV of a cube's east face runs the opposite way round from its north face, so
# a set that read differently reversed would only line up on two sides.
CROSSINGS = (3, 12)


def _on_border(p: Tuple[int, int]) -> bool:
    """True on the outermost ring of the tile - the pixels that meet the neighbouring block."""
    return p[0] in (0, SIZE - 1) or p[1] in (0, SIZE - 1)


def bridge(rng: random.Random, start: int, lo: int = 1, hi: int = SIZE - 2
           ) -> List[Tuple[int, int]]:
    """A wandering path across the tile that begins and ends on `start`.

    Returns (step, drift) pairs for step 0..15. Pinning both ends to the same
    offset is what makes the crack continue into the next tile: the path
    leaving row 15 at column `start` meets the path entering row 0 at column
    `start` in the block below.
    """
    path = []
    drift = start
    for step in range(SIZE):
        path.append((step, drift))
        # Only ever step somewhere the path can still get home from. Checking
        # after the fact instead lets the walk strand itself one column out,
        # and the crack then misses its opposite number by a pixel.
        left = SIZE - 1 - (step + 1)
        # Steps of up to two, not one. A +-1 walk pinned at both ends can only wander about four
        # columns from its start, and what comes out is a line with a slight lean - two of those
        # crossing read as a drawn grid rather than as cracked rock.
        reachable = [
            d for d in range(drift - 2, drift + 3)
            if lo <= d <= hi and abs(d - start) <= left
        ]
        if reachable:
            r = rng.random()
            if r < 0.10:
                want = drift - 2
            elif r < 0.40:
                want = drift - 1
            elif r < 0.60:
                want = drift
            elif r < 0.90:
                want = drift + 1
            else:
                want = drift + 2
            drift = want if want in reachable else min(
                reachable, key=lambda d: abs(d - want)
            )
    return path


def lattice(rng: random.Random, vertical_only: bool
            ) -> Tuple[Dict[Tuple[int, int], float], Dict[Tuple[int, int], float]]:
    """Build a crack lattice as (width, phase) maps.

    Both faces use cracks on both axes. The side could get away with only the
    verticals - they are what carries the lava down from the top face - but two
    lone vertical runs repeat obviously once the block is stacked, and the
    horizontals cost nothing: the side texture meets itself across a block's
    vertical corners and between neighbours, at the same CROSSINGS offsets, so
    they line up there too.
    """
    width: Dict[Tuple[int, int], float] = {}
    phase: Dict[Tuple[int, int], float] = {}
    verticals, horizontals = [], []

    for u0 in CROSSINGS:
        verticals.append([(d, s) for s, d in bridge(rng, u0)])
    if not vertical_only:
        for v0 in CROSSINGS:
            horizontals.append([(s, d) for s, d in bridge(rng, v0)])

    def connect(path: List[Tuple[int, int]]) -> List[Tuple[int, int]]:
        """Fill the cell a two-step jump skipped over.

        The bridge advances one row per step but may shift up to two columns, and (1, 2) apart is not
        adjacent - left as-is the fissure comes out dotted rather than cracked. Inserting the midpoint
        at the previous row turns the jump into two diagonal moves, both of which touch.
        """
        out: List[Tuple[int, int]] = []
        for i, p in enumerate(path):
            if i > 0:
                q = path[i - 1]
                if abs(p[0] - q[0]) == 2:
                    out.append(((p[0] + q[0]) // 2, p[1]))
                elif abs(p[1] - q[1]) == 2:
                    out.append((p[0], (p[1] + q[1]) // 2))
            out.append(p)
        return out

    verticals = [connect(v) for v in verticals]
    horizontals = [connect(h) for h in horizontals]

    for path in verticals + horizontals:
        for i, p in enumerate(path):
            width[p] = max(width.get(p, 0.0), W_THIN)
            # the pulse runs along the path, so on a side face it reads as
            # lava flowing down the block
            phase.setdefault(p, (i / SIZE) * math.tau)

    # Short dead-end splinters off the lattice. Without them the pinned-down
    # bridges read as a regular grid, and a regular grid is exactly what makes
    # the repeat obvious once the block is laid out over a wall of terrain.
    trunk = [p for path in verticals + horizontals for p in path]
    for _ in range(4 if horizontals else 3):
        x, y = trunk[rng.randrange(len(trunk))]
        dx, dy = rng.choice(((1, 0), (-1, 0), (0, 1), (0, -1), (1, 1), (-1, -1)))
        for _ in range(rng.randint(2, 4)):
            x, y = x + dx, y + dy
            # splinters must not reach an edge: only the lattice may cross one
            if not (1 <= x <= SIZE - 2 and 1 <= y <= SIZE - 2):
                break
            width.setdefault((x, y), W_THIN)
            phase.setdefault((x, y), rng.random() * math.tau)

    # open the fissure up here and there, and fully at the junctions
    for path in verticals + horizontals:
        for p in path:
            if rng.random() < 0.10:
                width[p] = max(width[p], W_WIDE)
    if horizontals:
        vset = {p for path in verticals for p in path}
        for path in horizontals:
            for p in path:
                near = [(p[0] + dx, p[1] + dy) for dx in (-1, 0, 1) for dy in (-1, 0, 1)]
                if any(q in vset for q in near):
                    width[p] = W_CORE

    halo = {}
    for (x, y) in list(width):
        for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)):
            q = ((x + dx) % SIZE, (y + dy) % SIZE)
            if q not in width and rng.random() < 0.24:
                halo[q] = W_HALO
                phase.setdefault(q, phase[(x, y)])
    width.update(halo)
    return width, phase


def assert_crossings(width: Dict[Tuple[int, int], float], edges: Sequence[str]) -> None:
    """Check each named edge is crossed at exactly the offsets in CROSSINGS.

    This is the whole basis for the cracks lining up between neighbouring
    blocks and between a block's top face and its sides, so it is verified
    rather than assumed.
    """
    core = {p for p, w in width.items() if w > W_HALO}
    seen = {
        "north": sorted({x for x, y in core if y == 0}),
        "south": sorted({x for x, y in core if y == SIZE - 1}),
        "west": sorted({y for x, y in core if x == 0}),
        "east": sorted({y for x, y in core if x == SIZE - 1}),
    }
    for name in edges:
        if tuple(seen[name]) != tuple(sorted(CROSSINGS)):
            raise AssertionError(
                "%s edge is crossed at %r, expected %r - cracks would not meet "
                "the neighbouring block" % (name, seen[name], sorted(CROSSINGS))
            )
    for name, offsets in seen.items():
        if name not in edges and offsets:
            raise AssertionError(
                "%s edge should stay clear but is crossed at %r" % (name, offsets)
            )


# How far the crust hangs on over a fissure that crosses its lower edge, in pixels. The crust is
# the last thing off the block, so where a vein runs under it the pale keeps going for this many
# rows on top of the lava instead of being cut by it.
CRUST_DRAPE = 1


def is_crust(c: Sequence[int]) -> bool:
    """Pale crust rather than the sculk under it. Sculk's cyan specks are bright but saturated."""
    return c[3] != 0 and luma(c) > 70 and saturation(c) < 60


def crust_line(base: Image.Image) -> List[int]:
    """How many rows of pale crust hang in each column of the bleached side."""
    depths = []
    for x in range(SIZE):
        d = 0
        while d < SIZE and is_crust(base.getpixel((x, d))):
            d += 1
        depths.append(d)
    return depths


def drape_crust(img: Image.Image, emit: Image.Image, base: Image.Image, base_emit: Image.Image,
                width: Dict[Tuple[int, int], float]) -> None:
    """Put the crust back on top of the fissures, in place, on the two edges and over the veins.

    The fissures are drawn into the crust band, and a vein crossing the band's lower edge takes the
    drip with it - which is what flattened the band into a plain stripe. Draping the crust back over
    those crossings is the cheaper half of the fix and the truer one: the crust is a skin over the
    rock, so where it meets a crack it hangs over the glow rather than stopping at it.

    The two edge columns get the whole band back, not just the drip. A fissure crosses the tile edge
    at a fixed offset, and where that offset lands inside the crust the pale line ended one pixel
    higher than on the bleached block next door - the join read as a step. Their cracks still meet
    below the crust, at the other crossing.
    """
    for x, depth in enumerate(crust_line(base)):
        if not depth:
            continue
        if x in (0, SIZE - 1):
            rows = range(depth)              # the seam: the crust line has to match the neighbour
        elif width.get((x, depth), 0) > W_HALO:
            rows = range(depth, depth + CRUST_DRAPE)   # a vein under the drip: hang on over it
        else:
            continue
        for y in rows:
            if y >= SIZE:
                break
            # a draped pixel takes the colour of the crust directly above it
            img.putpixel((x, y), base.getpixel((x, min(y, depth - 1))))
            emit.putpixel((x, y), base_emit.getpixel((x, min(y, depth - 1))))


def molten_face(src: Sources, rng: random.Random, vertical_only: bool, face: str
                ) -> Tuple[Image.Image, Image.Image]:
    """Crack the bleached crust open with lava fissures. `face` is "top" or "side".

    The rock here is the bleached crust rather than vanilla sculk, which is what it used to be: the
    molten block is the biome's own floor with the rock pulled open, not a separate dark stone that
    happens to be hot, and against a floor that is now four fifths bleached the sculk-based version
    read as a third material.

    The crust has fewer frames than the fissures do, so each crust frame is held across two molten
    frames - the same stretch the sculk base was already getting, so its glimmers run at the pace
    sculk's specks used to.
    """
    width, phase = lattice(rng, vertical_only)
    assert_crossings(
        width,
        ("north", "south") if vertical_only else ("north", "south", "east", "west"),
    )

    bases, base_emits = src.bleached[face]

    out, emits = [], []
    for f in range(MOLTEN_FRAMES):
        t = f / MOLTEN_FRAMES
        i = (f * len(bases)) // MOLTEN_FRAMES
        base = bases[i]
        img = base.copy()
        # The crust's own glimmers keep glowing through - they are the block's, not the fissures'.
        # Any of them a fissure runs over is overwritten below, in both the colour and the glow.
        emit = base_emits[i].copy()
        for (x, y), w in width.items():
            # A wave of heat travels along each crack. Width sets how hot a
            # pixel can get, the pulse sets how much of that it is showing, so
            # a hairline never reaches the bright end of the ramp and a junction
            # spends part of the cycle white-hot.
            pulse = 0.5 + 0.5 * math.sin(math.tau * t - phase[(x, y)])
            temp = w * (0.55 + 0.45 * pulse)
            # Where a fissure leaves the tile it is held at full heat, whatever the pulse is doing.
            # The two blocks either side of a seam are at different points in the wave, so without this
            # a crack that lines up perfectly still meets a cold one half the time and the join reads
            # as two separate cracks that happen to touch.
            if w > W_HALO and _on_border((x, y)):
                temp = max(temp, 0.92)
            c = pick(src.heat, temp)
            if w <= W_HALO:
                # halo: scorched rock rather than open lava
                scorched = mix(base.getpixel((x, y)), c, 0.35 + 0.35 * pulse)
                img.putpixel((x, y), scorched)
                # a glimmer inside the scorch reads as the scorch, not as a cold speck over it
                if emit.getpixel((x, y))[3]:
                    emit.putpixel((x, y), scorched)
            else:
                img.putpixel((x, y), c)
                # only what is genuinely glowing lights up
                if temp >= W_THIN * 0.6:
                    emit.putpixel((x, y), c)
                else:
                    emit.putpixel((x, y), (0, 0, 0, 0))
        if face == "side":
            drape_crust(img, emit, base, base_emits[i], width)
        out.append(img)
        emits.append(emit)
    return stack(out), stack(emits)


# --------------------------------------------------------------------------
# wisp_vine
# --------------------------------------------------------------------------

# . transparent   o outline   l light body   m mid body   d shaded body
# E eye (dark)    G eye glint (emissive)     U mouth (emissive)
#
# Big dark 2x3 eyes are what makes the face read at 16px - a cyan-on-pale eye
# is the right colour but disappears at this size, so the eye is cut out of the
# sculk navy and only the glint carries the glow.
GHOST = [
    "................",
    ".....oooooo.....",
    "....ollllllo....",
    "...ollllllllo...",
    "...ollllllllo...",
    "...olGEllGEmo...",
    "...olEEllEEmo...",
    "...olEEllEEmo...",
    "...ollllllllo...",
    "...olllUUllmo...",
    "...olllUUllmo...",
    "...ollllmmmmo...",
    "...ommmmmmmmo...",
    "...oddddddddo...",
    "...ddd.dd.ddd...",
    "................",
]


# Which rows of GHOST the eyes occupy, top first. `blink` closes them from the top down.
EYE_ROWS = (5, 6, 7)


def wisp_head(src: Sources, face: bool = True, blink: int = 0) -> Tuple[Image.Image, Image.Image]:
    """The wisp's head. With `face` false the eyes and mouth are left as plain body.

    The head model shows the faced texture on a single side and the blank one on the other three, so a
    wisp has exactly one face rather than four, and the blockstate's four rotations decide which way it
    looks.

    `blink` closes the eyes from the top down - 0 open, 1 half, 2 down to a shut line. The glint follows
    the topmost row still open, and goes out entirely once the eye is shut, which is what sells it as a
    blink rather than the eye simply shrinking.
    """
    assert all(len(r) == SIZE for r in GHOST), "ghost map must be 16 wide"
    assert len(GHOST) == SIZE, "ghost map must be 16 tall"

    out, emit = blank(), blank()
    glint = pick(src.spark, 1.0)
    eye = pick(src.dark, 0.0)
    mouth = mix(pick(src.dark, 0.0), pick(src.spark, 0.4), 0.4)
    body = {
        # The outline was the near-black bottom of the dark ramp, which put a hard ink line round
        # a pale ghost - too harsh against the gloomwood palette, and it read as a sticker rather
        # than something standing in the biome. This is the top of that same ramp lifted towards
        # the pale tones: still the darkest thing on the wisp, but a slate that belongs to the wood.
        "o": mix(pick(src.dark, 1.0), pick(src.pale, 0.0), 0.30),
        "l": pick(src.pale, 1.0),
        "m": pick(src.pale, 0.55),
        "d": pick(src.pale, 0.2),
    }
    for y, row in enumerate(GHOST):
        for x, ch in enumerate(row):
            if ch == ".":
                continue
            if ch in body:
                # a little top-to-bottom falloff so the head reads as rounded
                c = mix(body[ch], pick(src.dark, 0.4), (y / SIZE) * 0.28)
                out.putpixel((x, y), c)
            elif not face or (ch in ("E", "G") and y < EYE_ROWS[0] + blink):
                # blank side, or an eye row the blink has closed: fall back to the body tone
                c = mix(body["l"], pick(src.dark, 0.4), (y / SIZE) * 0.28)
                out.putpixel((x, y), c)
            elif ch in ("E", "G"):
                lit = y == EYE_ROWS[0] + blink and x in (5, 9) and blink < len(EYE_ROWS) - 1
                out.putpixel((x, y), glint if lit else eye)
                if lit:
                    emit.putpixel((x, y), glint)
            elif ch == "U":
                out.putpixel((x, y), mouth)
                emit.putpixel((x, y), mouth)
    return out, emit


def veined(src: Sources) -> Tuple[Image.Image, Image.Image, Image.Image, Image.Image]:
    """The single block between the sculk floor and the netherrack under it.

    Returns (top, top_emissive, side, side_emissive).

    Built like a grass block rather than as a blend: sculk on the top face, netherrack on the bottom,
    and a side that carries the whole change across one block with an interlocking edge. An even mix
    through the whole cube was the first attempt and read as neither material - a muddy third rock
    rather than one turning into the other.

    Because the transition lives inside the block, only ever one layer of it may be placed. Two stacked
    would show the netherrack bottom of the upper block against the sculk top of the lower one - the
    exact hard seam this exists to remove. The biome's surface rule places it at depth 1 for that
    reason.

    The sculk half animates on sculk's own four frames and cadence, so it stays in step with the real
    sculk sitting on top of it.
    """
    rng = random.Random(SEED + 8)
    netherrack = load(src.vanilla, "netherrack.png")
    # only the top face wants a soft field - the side edge below is deliberately not built from noise
    blotch = fbm(rng, (4, 8), (1.0, 0.5))

    # A stepped edge, not a wave.
    #
    # The bleached crust above this block already hangs on smooth per-column noise, and when the two
    # sit one above the other in a cut face the eye reads them as the same motif twice. Rock parting
    # from rock has no business drooping like moss anyway: this holds a depth flat for a few columns
    # and then steps, and the teeth below interlock the two materials squarely.
    depth: List[int] = []
    runs: List[Tuple[int, int]] = []
    while len(depth) < SIZE:
        d = rng.choice((3, 4, 4, 5, 6, 7))
        n = min(rng.randint(2, 4), SIZE - len(depth))
        runs.append((d, n))
        depth += [d] * n
    # carry the first run's depth across the seam so the wrap is a plateau, not a step every 16 px
    for i in range(SIZE - runs[-1][1], SIZE):
        depth[i] = depth[0]

    # square teeth of one material driven into the other, two wide
    tongues: Dict[int, int] = {}
    flecks: set = set()
    for _ in range(3):
        x0 = rng.randrange(SIZE)
        for x in (x0, (x0 + 1) % SIZE):
            tongues[x] = depth[x] + rng.randint(1, 2)
    for _ in range(3):
        x0 = rng.randrange(SIZE)
        d = depth[x0 % SIZE]
        for x in (x0, (x0 + 1) % SIZE):
            for dy in range(rng.randint(1, 2)):
                flecks.add((x % SIZE, d - 1 - dy))

    tops, top_emits, sides, side_emits = [], [], [], []
    for f, sculk in enumerate(src.sculk):
        # top face: sculk, with a little of the rock beneath showing through
        top = sculk.copy()
        top_emit = blank()
        for y in range(SIZE):
            for x in range(SIZE):
                if blotch[y][x] > 0.86:
                    top.putpixel((x, y), mix(netherrack.getpixel((x, y)), sculk.getpixel((x, y)), 0.35))
                elif saturation(sculk.getpixel((x, y))) >= 90:
                    top_emit.putpixel((x, y), sculk.getpixel((x, y)))
        tops.append(top)
        top_emits.append(top_emit)

        side = blank()
        side_emit = blank()
        for x in range(SIZE):
            limit = tongues.get(x, depth[x])
            for y in range(SIZE):
                sculked = y < limit or (x, y) in flecks
                if sculked:
                    c = sculk.getpixel((x, y))
                    # fade the last row into the rock so the edge is a change, not a cut
                    if y == limit - 1:
                        c = mix(c, netherrack.getpixel((x, y)), 0.35)
                    elif saturation(c) >= 90:
                        side_emit.putpixel((x, y), c)
                else:
                    c = netherrack.getpixel((x, y))
                side.putpixel((x, y), c)
        sides.append(side)
        side_emits.append(side_emit)

    return stack(tops), stack(top_emits), stack(sides), stack(side_emits)


def molten_log(src: Sources) -> Tuple[List[Image.Image], List[Image.Image], Image.Image, Image.Image]:
    """The dark gloomwood log with lava in it, for the block at the foot of a tree.

    Returns (side frames, side emissive frames, top, top emissive) - the side is handed back unstacked
    so the caller can roll the frame order and emit several sprites that are the same fissures at
    different points in the pulse.

    The fissures climb out of the bottom edge and peter out before they reach the top, which is the
    whole point of the block - it reads as heat coming up out of the ground into the trunk. That makes
    the texture directional, so unlike the gloomsculk faces it deliberately does not tile vertically;
    it only ever wants to be one block tall. It still wraps horizontally, because a log is seen from
    four sides and the seams between them are real.
    """
    rng = random.Random(SEED + 9)
    side_base = load(src.assets, "gloomwood_dark_log_side.png")
    top_base = load(src.assets, "gloomwood_dark_log_top.png")

    width: Dict[Tuple[int, int], float] = {}
    phase: Dict[Tuple[int, int], float] = {}
    for _ in range(4):
        x = rng.randrange(SIZE)
        y = SIZE - 1
        climb = rng.randint(7, 13)
        for i in range(climb):
            if y < 0:
                break
            # the fissure narrows as it climbs, so the taper is in the block's width, not its colour
            w = W_WIDE * (1.0 - i / climb) + W_THIN * 0.5
            p = (x % SIZE, y)
            if width.get(p, 0.0) < w:
                width[p] = w
                phase[p] = (i / 9.0) * math.tau
            if i < climb * 0.4 and rng.random() < 0.4:
                q = ((x + rng.choice((-1, 1))) % SIZE, y)
                width.setdefault(q, w * 0.7)
                phase.setdefault(q, phase[p])
            y -= 1
            x += rng.choice((-1, 0, 0, 1))

    sides, side_emits = [], []
    for f in range(MOLTEN_FRAMES):
        t = f / MOLTEN_FRAMES
        img = side_base.copy()
        emit = blank()
        for (x, y), w in width.items():
            pulse = 0.5 + 0.5 * math.sin(math.tau * t - phase[(x, y)])
            temp = w * (0.55 + 0.45 * pulse)
            c = pick(src.heat, temp)
            img.putpixel((x, y), c)
            if temp >= W_THIN * 0.6:
                emit.putpixel((x, y), c)
        sides.append(img)
        side_emits.append(emit)

    # The end grain is almost clean: the fissures have thinned out by the time they get there, so it
    # carries a few embers in the rings rather than open lava.
    top = top_base.copy()
    top_emit = blank()
    for _ in range(3):
        x, y = rng.randrange(SIZE), rng.randrange(SIZE)
        c = pick(src.ember, 0.5 + rng.random() * 0.5)
        top.putpixel((x, y), c)
        top_emit.putpixel((x, y), c)
    return sides, side_emits, top, top_emit


def geode(src: Sources) -> Tuple[Image.Image, Image.Image, Image.Image, Image.Image, Image.Image]:
    """The gloomsculk geode: a sculk shell cracked open over a frosted cap, with lava inside.

    Returns (side, top, core, core_emissive, side_emissive).

    The shell's rim is a third edge shape, and deliberately so - the bleached crust droops, the veined
    transition steps, and this one is jagged, because it is meant to read as something broken open
    rather than one material fading into another. All three can end up in the same view.

    The frost is not glass. It thickens towards the edges of a face with an irregular boundary, carries
    sculk grit where it meets the shell, and is studded with little x-shaped crystals - a smooth even
    pane reads as a window, where this wants to read as something frozen over a hole in the rock.
    """
    rng = random.Random(SEED + 11)
    FROST_CLEAR = 120     # alpha in the middle of a face, where the core shows through
    FROST_SOLID = 250     # alpha at the rim, where the frost has all but closed over
    edge = normalize(noise(rng, 4))
    creep = fbm(rng, (4, 8), (1.0, 0.6))
    grit = fbm(random.Random(SEED + 14), (8, 16), (1.0, 0.7))

    def frost_alpha(x, y, top_limit):
        d = min(x, SIZE - 1 - x, max(0, y), max(0, top_limit - 1 - y)) / (SIZE / 2.0)
        d = max(0.0, min(1.0, d * 1.6 + (creep[y][x] - 0.5) * 0.55))
        return int(round(FROST_SOLID + (FROST_CLEAR - FROST_SOLID) * d))

    def frost_pixel(x, y, top_limit):
        """A frost pixel, gritted with sculk where it is close to the shell."""
        a = frost_alpha(x, y, top_limit)
        depth = min(x, SIZE - 1 - x, max(0, y), max(0, top_limit - 1 - y)) / (SIZE / 2.0)
        t = 0.45 + (1.0 - y / max(1, top_limit)) * 0.5
        c = pick(src.pale, min(1.0, t + rng.random() * 0.12))
        # sculk grit crowds the frost where it meets the shell and thins out towards the middle
        if grit[y][x] > 0.55 + depth * 0.45:
            c = mix(c, pick(src.sculk_body, 0.3 + rng.random() * 0.5), 0.55 + rng.random() * 0.35)
            a = min(255, a + 30)
        return (c[0], c[1], c[2], a)

    def stud(img, cx, cy, limit):
        """A small x of hard bright pixels - the crystal habit, drawn sharp rather than shaded."""
        bright = pick(src.pale, 1.0)
        for dx, dy in ((0, 0), (-1, -1), (1, -1), (-1, 1), (1, 1)):
            x, y = cx + dx, cy + dy
            if not (0 <= x < SIZE and 0 <= y < limit):
                continue
            img.putpixel((x, y), (bright[0], bright[1], bright[2], 255 if dx == 0 else 236))

    # Jagged rim, sitting low on the block: the frosted cap is the part worth seeing, so the shell only
    # holds the bottom few rows rather than half the face.
    rim = []
    v = 11
    for x in range(SIZE):
        v += rng.choice((-2, -1, -1, 1, 1, 2))
        v = max(8, min(13, v))
        rim.append(v)
    rim[-1] = (rim[0] + rim[-2]) // 2   # keep the wrap from stepping hard

    studs = [(rng.randrange(2, SIZE - 2), rng.randrange(2, max(3, min(rim) - 2))) for _ in range(4)]

    sides, side_emits = [], []
    for sculk in src.sculk:
        side = blank()
        emit = blank()
        for x in range(SIZE):
            for y in range(SIZE):
                if y >= rim[x]:
                    c = sculk.getpixel((x, y))
                    side.putpixel((x, y), c)
                    if saturation(c) >= 90:
                        emit.putpixel((x, y), c)
                else:
                    side.putpixel((x, y), frost_pixel(x, y, rim[x]))
        for cx, cy in studs:
            stud(side, cx, cy, min(rim))
        sides.append(side)
        side_emits.append(emit)

    top = blank()
    for y in range(SIZE):
        for x in range(SIZE):
            top.putpixel((x, y), frost_pixel(x, y, SIZE))
    for _ in range(5):
        stud(top, rng.randrange(2, SIZE - 2), rng.randrange(2, SIZE - 2), SIZE)

    # The core: lava turning over rather than merely brightening. A spiral in polar coordinates whose
    # phase advances one whole turn across the frame list, so it loops seamlessly however long it runs.
    cores, core_emits = [], []
    centre = (SIZE - 1) / 2.0
    grain = fbm(random.Random(SEED + 12), (4, 8), (1.0, 0.5))
    for f in range(MOLTEN_FRAMES):
        t = f / MOLTEN_FRAMES
        img = blank()
        emit = blank()
        for y in range(SIZE):
            for x in range(SIZE):
                dx, dy = x - centre, y - centre
                r = math.hypot(dx, dy) / centre
                theta = math.atan2(dy, dx)
                # arms twist with radius, which is what makes it read as flow rather than a pinwheel
                swirl = 0.5 + 0.5 * math.sin(CORE_ARMS * theta + CORE_TWIST * r - math.tau * t)
                v = 0.30 + swirl * 0.45 + grain[y][x] * 0.25 - r * 0.12
                c = pick(src.heat, max(0.0, min(1.0, v)))
                img.putpixel((x, y), c)
                emit.putpixel((x, y), c)
        cores.append(img)
        core_emits.append(emit)

    return stack(sides), top, stack(cores), stack(core_emits), stack(side_emits)


# Crystal clusters, one shard list per size. Each shard is (column, base row, tip row, half-width).
CRYSTAL_SIZES = {
    "": ((4, 15, 7, 1), (7, 15, 2, 1), (10, 15, 6, 1), (12, 15, 9, 0)),
    "_b": ((6, 15, 6, 1), (9, 15, 9, 0), (11, 15, 11, 0)),
    "_c": ((7, 15, 10, 1), (10, 15, 12, 0)),
}

# Where a shard stops being rock and starts being lava, as a fraction of its height. Kept high on
# purpose: it is sculk that happens to be glowing at the tip, not a lava spike with a base.
CRYSTAL_ROCK_UP_TO = 0.58
CRYSTAL_GLOW_FROM = 0.70


def crystal(src: Sources, shards) -> Tuple[Image.Image, Image.Image]:
    """Shards growing out of a geode: sculk at the root, lava at the tip.

    Returns (texture, emissive). Only the hot part is emissive - a shard lit end to end reads as a lamp,
    where the point of it is that the heat is coming up from inside the rock.

    The root is sculk's own dark teal with no ember in it. An earlier version blended through the magma
    ramp on the way up, and the bottom half came out maroon - which read as netherrack rather than as
    the sculk the crystal is growing out of. The blend now goes straight from the sculk tones into the
    heat ramp.
    """
    rng = random.Random(SEED + 13)
    out, emit = blank(), blank()
    for cx, base, tip, half in shards:
        span = base - tip
        for y in range(tip, base + 1):
            up = (base - y) / max(1, span)
            w = half if up < 0.55 else 0
            for x in range(cx - w, cx + w + 1):
                if not 0 <= x < SIZE:
                    continue
                if up < CRYSTAL_ROCK_UP_TO:
                    c = pick(src.sculk_body, 0.15 + rng.random() * 0.45)
                elif up < CRYSTAL_GLOW_FROM:
                    t = (up - CRYSTAL_ROCK_UP_TO) / (CRYSTAL_GLOW_FROM - CRYSTAL_ROCK_UP_TO)
                    c = mix(pick(src.sculk_body, 0.5), pick(src.heat, 0.45), t)
                else:
                    c = pick(src.heat, 0.45 + (up - CRYSTAL_GLOW_FROM) / (1 - CRYSTAL_GLOW_FROM) * 0.5)
                    emit.putpixel((x, y), c)
                out.putpixel((x, y), c)
    return out, emit


# The geode's own crystals, in three sizes. Same shard geometry as the sculk crystals; what differs is
# the palette - these grow out of the frosted cap, so they start as frost rather than as rock.
GEODE_CRYSTAL_ROCK_UP_TO = 0.45


def geode_crystal(src: Sources, shards) -> Tuple[Image.Image, Image.Image]:
    """A crystal budded off a geode: frost at the base, lava at the tip.

    Returns (texture, emissive). The base is the same pale the frosted cap uses, so a crystal reads as
    the cap having grown outwards rather than as a separate mineral stuck to it, and the gradient runs
    the whole length into the heat ramp instead of stepping through a rock band.
    """
    rng = random.Random(SEED + 15)
    out, emit = blank(), blank()
    for cx, base, tip, half in shards:
        span = base - tip
        for y in range(tip, base + 1):
            up = (base - y) / max(1, span)
            w = half if up < 0.55 else 0
            for x in range(cx - w, cx + w + 1):
                if not 0 <= x < SIZE:
                    continue
                if up < GEODE_CRYSTAL_ROCK_UP_TO:
                    c = pick(src.pale, 0.55 + rng.random() * 0.35)
                else:
                    t = (up - GEODE_CRYSTAL_ROCK_UP_TO) / (1 - GEODE_CRYSTAL_ROCK_UP_TO)
                    c = mix(pick(src.pale, 0.9), pick(src.heat, 0.4 + t * 0.55), t)
                    if t > 0.45:
                        emit.putpixel((x, y), c)
                out.putpixel((x, y), c)
    return out, emit


def lamp_core(src: Sources) -> Tuple[Image.Image, Image.Image]:
    """The lamp's core: the geode's swirl driven white-hot.

    Same spiral, pushed up the heat ramp and washed towards white. A lamp has to look like it is putting
    out more light than the geode it was made from, and at 16 the ramp simply runs out of orange - so the
    extra brightness has to come from desaturating towards white rather than from a hotter colour.
    """
    cores, emits = [], []
    centre = (SIZE - 1) / 2.0
    grain = fbm(random.Random(SEED + 12), (4, 8), (1.0, 0.5))
    white = (255, 249, 226, 255)
    for f in range(MOLTEN_FRAMES):
        t = f / MOLTEN_FRAMES
        img = blank()
        emit = blank()
        for y in range(SIZE):
            for x in range(SIZE):
                dx, dy = x - centre, y - centre
                r = math.hypot(dx, dy) / centre
                theta = math.atan2(dy, dx)
                swirl = 0.5 + 0.5 * math.sin(CORE_ARMS * theta + CORE_TWIST * r - math.tau * t)
                v = 0.55 + swirl * 0.35 + grain[y][x] * 0.2 - r * 0.10
                c = pick(src.heat, max(0.0, min(1.0, v)))
                # the hottest parts blow out to white, which is what sells it as brighter
                c = mix(c, white, max(0.0, (v - 0.72) / 0.28) * 0.8)
                img.putpixel((x, y), c)
                emit.putpixel((x, y), c)
        cores.append(img)
        emits.append(emit)
    return stack(cores), stack(emits)


# Where the vine's orange tips can appear, as a fraction of the leaf's length from the stem outwards.
VINE_TIP_FROM = 0.72


def _vine_leaf_colour(src: Sources, t: float, top: float, hot: bool):
    """One leaf pixel `t` of the way out from the stem. Returns (colour, glows)."""
    if hot and t >= VINE_TIP_FROM:
        return pick(src.heat, 0.45 + (t - VINE_TIP_FROM) / (1 - VINE_TIP_FROM) * 0.45), True
    return mix(pick(src.sculk_body, 0.55), pick(src.pale, top), t), False


def _round_leaf(out, emit, src: Sources, cx: float, cy: float, r: float, top: float, hot: bool):
    """Stamp one of the gloomgrass's round leaves, centred on (cx, cy).

    Squashed slightly on y so it reads as a leaf rather than a ball, and shaded outwards from the
    centre - the same dark-to-pale run the strap leaves use, just radial.
    """
    ri = int(math.ceil(r))
    for dy in range(-ri, ri + 1):
        for dx in range(-ri, ri + 1):
            d = math.hypot(dx / r, dy / (r * 0.85))
            if d > 1.0:
                continue
            x, y = int(round(cx + dx)), int(round(cy + dy))
            if not (0 <= x < SIZE and 0 <= y < SIZE):
                continue
            c, glows = _vine_leaf_colour(src, min(1.0, d), top, hot)
            out.putpixel((x, y), c)
            if glows:
                emit.putpixel((x, y), c)


def sculk_vine(src: Sources, bottom: bool) -> Tuple[Image.Image, Image.Image]:
    """A vine for the sculk ceiling: dark stem, leaves shading out to the light bark tones.

    Returns (texture, emissive). `bottom` draws the closing segment.

    A thin strand: the strap leaves reach three pixels at most and a good half of the rows carry no
    leaf at all, so the silhouette stays wiry rather than bushy. Every so often a leaf comes out round
    instead - the gloomgrass's leaf shape, borrowed so the two plants read as growing from the same
    place - and a few finish on the heat ramp, which is the only part that glows.

    Leaves run the full height including rows 0 and 15. Leaving those rows empty puts a one-pixel gap
    at every block boundary, which on a hanging strand reads as the vine being chopped into pieces.

    The closing segment tapers: leaves shorten as they go down, the stem drops to a single pixel and
    ends in a point, and a round gloomgrass leaf sits on the very tip.
    """
    rng = random.Random(SEED + 16 + (1 if bottom else 0))
    out, emit = blank(), blank()
    stem_x = (7, 8)

    # where the closing segment stops being two pixels wide, and where its point ends
    NARROW_FROM, POINT_END = 8, 11

    stem_rows = range(0, POINT_END) if bottom else range(SIZE)
    for y in stem_rows:
        cols = stem_x if (not bottom or y < NARROW_FROM) else (stem_x[1],)
        for x in cols:
            out.putpixel((x, y), pick(src.dark, 0.15 + rng.random() * 0.5))

    # leaves stop well above the point, so the taper is a bare stem rather than a shrinking bush
    leaf_rows = range(0, NARROW_FROM) if bottom else range(SIZE)
    # Chosen up front rather than rolled per leaf. At a believable per-leaf rate a whole segment can
    # come out with no orange on it at all, and since every strand is built from the same two sprites
    # that means no orange anywhere - so the count is picked first and handed to specific leaves.
    leaves = []
    for y in leaf_rows:
        if rng.random() < 0.42:
            continue
        # on the closing segment the reach shrinks with depth, which is what makes it read as pointed
        cap = max(1, 3 - y // 4) if bottom else 3
        for side in (-1, 1):
            if rng.random() < 0.45:
                continue
            leaves.append((y, side, cap, 0.45 + rng.random() * 0.55, cap >= 2 and rng.random() < 0.22))
    hot_leaves = set(rng.sample(range(len(leaves)), min(len(leaves), 1 if bottom else 2)))

    for n, (y, side, cap, top, round_leaf) in enumerate(leaves):
        hot = n in hot_leaves
        anchor = stem_x[0] if side < 0 else stem_x[1]
        if round_leaf:
            # a round gloomgrass leaf instead of a strap one
            _round_leaf(out, emit, src, anchor + side * 1.8, y, 1.4, top, hot)
            continue
        span = rng.randint(1, cap)
        for i in range(1, span + 1):
            x = anchor + side * i
            if not 0 <= x < SIZE:
                break
            c, glows = _vine_leaf_colour(src, i / span, top, hot)
            out.putpixel((x, y), c)
            if glows:
                emit.putpixel((x, y), c)

    if bottom:
        # the round grass leaf capping the point - the brightest thing on the strand, and the reason
        # the tip does not just stop
        _round_leaf(out, emit, src, stem_x[1], POINT_END + 2, 2.6, 1.0, False)
        for _ in range(2):
            x = stem_x[1] + rng.randint(-2, 2)
            y = POINT_END + rng.randint(1, 4)
            if out.getpixel((x % SIZE, y % SIZE))[3] == 0:
                continue
            c = pick(src.heat, 0.55 + rng.random() * 0.35)
            out.putpixel((x % SIZE, y % SIZE), c)
            emit.putpixel((x % SIZE, y % SIZE), c)
    return out, emit


def wisp_glow(src: Sources) -> Image.Image:
    """The disc that caps the wisp's head, top and bottom.

    A dark radial gradient with a noisy edge: darkest in the middle, fading out to nothing before it
    reaches the rim so it never shows a hard circle against the head's inner wall. It reads as the hollow
    the soul fire is burning out of, and the darkness is what makes the fire above it carry.
    """
    rng = random.Random(SEED + 7)
    field = fbm(rng, (4, 8), (1.0, 0.5))
    out = blank()
    centre = (SIZE - 1) / 2.0
    for y in range(SIZE):
        for x in range(SIZE):
            d = math.hypot(x - centre, y - centre) / centre
            # push the radius around per pixel so the edge breaks up instead of ringing
            d = min(1.0, max(0.0, d + (field[y][x] - 0.5) * 0.40))
            if d >= 1.0:
                continue
            t = 1.0 - d
            c = mix(pick(src.dark, 0.55), pick(src.dark, 0.0), t)
            out.putpixel((x, y), (c[0], c[1], c[2], int(round(255 * (t ** 0.75)))))
    return out


def wisp_stem(src: Sources, seed: int, lo: int, hi: int) -> Tuple[Image.Image, Image.Image]:
    """One stalk segment, taking transition-log rows `lo`..`hi` over its height.

    A wisp is at most three blocks tall - head plus two stalk segments - so the
    two segments split the gradient between them rather than each running the
    whole ramp: the upper one takes the bright half and the lower one carries
    on from where it left off into the dark. Stacking them reads as a single
    gradient from the head down to the ground.
    """
    rng = random.Random(seed)
    out, emit = blank(), blank()
    wobble = normalize(noise(rng, 4))
    for y in range(SIZE):
        # The stalk drifts by at most a pixel, but is pinned to centre on the
        # first and last row: those are the rows that butt against the segment
        # above and below, and a stalk that jumped sideways at the block
        # boundary would read as broken.
        if y in (0, SIZE - 1):
            off = 0
        else:
            off = -1 if wobble[y][0] < 0.33 else (1 if wobble[y][0] > 0.72 else 0)
        left = 7 + off
        row = lo + round((y / (SIZE - 1)) * (hi - lo))
        for x in (left, left + 1):
            out.putpixel((x % SIZE, y), src.transition_at(x, row))
        # occasional whisker
        if wobble[y][8] > 0.80:
            side = left - 1 if rng.random() < 0.5 else left + 2
            out.putpixel(
                (side % SIZE, y),
                mix(src.transition_at(side, row), pick(src.dark, 0.3), 0.35),
            )
    for _ in range(2):
        y = rng.randrange(2, SIZE - 2)
        x = (7 + rng.choice((-1, 2))) % SIZE
        c = pick(src.spark, rng.random())
        out.putpixel((x, y), c)
        emit.putpixel((x, y), c)
    return out, emit


# --------------------------------------------------------------------------
# pale_grass / gloom_grass
# --------------------------------------------------------------------------

def pale_grass(src: Sources, bone: Image.Image, index: int) -> Tuple[Image.Image, Image.Image]:
    """Recolour a bone-grass silhouette with the transition-log gradient.

    The gradient is normalised over each variant's own bounding box, so a short
    tuft spans the same dark-base-to-bright-tip range as a tall stalk.

    The shadows are then lifted (see `lift_shadows`). The transition log runs from near-black to
    bone, which is right for a metre of trunk read at arm's length and wrong for a tuft: on the pale
    floor the dark half of the gradient turned a field of these into static.
    """
    rng = random.Random(SEED + 10 + index)
    solid = [(x, y) for y in range(SIZE) for x in range(SIZE) if bone.getpixel((x, y))[3] > 0]
    if not solid:
        return blank(), blank()
    y0 = min(y for _, y in solid)
    y1 = max(y for _, y in solid)
    span = max(1, y1 - y0)

    out, emit = blank(), blank()
    for x, y in solid:
        alpha = bone.getpixel((x, y))[3]
        t = (y - y0) / span
        # column jitter keeps neighbouring stalks from banding identically
        c = src.transition_at(x + (index * 3), int(round(t * (SIZE - 1))))
        if t < 0.25:  # lift the very tips so they read as bleached
            c = mix(c, pick(src.pale, 1.0), 0.35 * (1 - t / 0.25))
        out.putpixel((x, y), (c[0], c[1], c[2], alpha))

    out = lift_shadows(out)

    # a spark on one of the topmost pixels, echoing the bleached leaves
    tips = [(x, y) for x, y in solid if y <= y0 + 1]
    for x, y in rng.sample(tips, min(2, len(tips))):
        # lifted like the body it sits in, and written into both maps, so the emissive overlay and
        # the albedo under it are the same colour
        c = lift_colour(pick(src.spark, rng.random()))
        out.putpixel((x, y), c)
        emit.putpixel((x, y), c)
    return out, emit


def gloom_grass(src: Sources, bone: Image.Image, index: int) -> Tuple[Image.Image, Image.Image]:
    """The dark counterpart of `pale_grass`: the same silhouette in sculk's own colours.

    Sculk's substrate ramp alone is five near-black tones, and a plant painted out of it would be
    invisible standing on the sculk floor it grows from - the whole tuft would read as a hole in the
    ground. So the ramp is sculk's body run on into its cyan speck tones: the base disappears into the
    floor and the tips come up teal, which is the same dark-to-bright reading the pale variant has,
    just on the other end of the biome's palette. The very brightest speck is held back for the
    emissive tips, so the glow is a highlight rather than the colour the whole plant already is.
    """
    rng = random.Random(SEED + 20 + index)
    solid = [(x, y) for y in range(SIZE) for x in range(SIZE) if bone.getpixel((x, y))[3] > 0]
    if not solid:
        return blank(), blank()
    y0 = min(y for _, y in solid)
    y1 = max(y for _, y in solid)
    span = max(1, y1 - y0)

    # body ramp: sculk's substrate at the bottom, its cyan tones at the top, minus the brightest speck
    ramp = sorted(set(src.sculk_body) | set(src.spark[:-1]), key=luma)
    # Per-column, not per-variant. The pale grass gets its jitter for free by sampling a real texture
    # sideways; a one-dimensional ramp has no sideways, so neighbouring stalks would step through the
    # tones in lockstep and the tuft would come out banded.
    jitter = [(rng.random() - 0.5) * 0.22 for _ in range(SIZE)]

    out, emit = blank(), blank()
    for x, y in solid:
        alpha = bone.getpixel((x, y))[3]
        t = (y - y0) / span
        c = pick(ramp, (1.0 - t) + jitter[x])
        out.putpixel((x, y), (c[0], c[1], c[2], alpha))

    # the sculk speck itself, on the tips - the same gesture pale_grass makes with the bleached sparkle
    tips = [(x, y) for x, y in solid if y <= y0 + 1]
    for x, y in rng.sample(tips, min(2, len(tips))):
        c = pick(src.spark, 0.8 + rng.random() * 0.2)
        out.putpixel((x, y), c)
        emit.putpixel((x, y), c)
    return out, emit


# --------------------------------------------------------------------------
# output
# --------------------------------------------------------------------------

# Blink timing. The gaps are deliberately uneven and mutually indivisible so the loop does not read as
# a metronome - roughly 35 seconds and five blinks per cycle, which is rare enough to catch the eye
# without turning the wisp into a flashing light. interpolate is off: a blink has to snap.
BLINK_FRAMES = [
    {"index": 0, "time": 97}, {"index": 1, "time": 1},
    {"index": 2, "time": 2}, {"index": 1, "time": 1},
    {"index": 0, "time": 163}, {"index": 1, "time": 1},
    {"index": 2, "time": 2}, {"index": 1, "time": 1},
    {"index": 0, "time": 71}, {"index": 1, "time": 1},
    {"index": 2, "time": 3}, {"index": 1, "time": 1},
    {"index": 0, "time": 229}, {"index": 1, "time": 1},
    {"index": 2, "time": 2}, {"index": 1, "time": 1},
    {"index": 0, "time": 131}, {"index": 1, "time": 1},
    {"index": 2, "time": 2}, {"index": 1, "time": 1},
]

# How many blink phases to emit and the sprite suffixes they take. As with the molten log, "_e" can
# never be one of them - it is the emissive convention.
BLINK_PHASES = 4
BLINK_SUFFIXES = ("", "_b", "_c", "_d")


def rotate_blink(offset: int) -> str:
    """The blink frame list, started `offset` ticks into its cycle.

    A wisp's blink lives in the timeline rather than in the pixels: the three frames are identical
    between phases and only the order and the leading time differ. So where the molten log needed
    rolled sprites, the wisps need rolled *mcmeta* - the sprite copies exist purely because a .mcmeta
    file belongs to exactly one texture.

    An offset lands inside an entry more often than not, so that entry is split rather than dropped and
    whatever was skipped is replayed at the end. Every phase therefore keeps the same cycle length and
    the same number of blinks; only the moment they happen moves.
    """
    total = sum(f["time"] for f in BLINK_FRAMES)
    offset %= total
    out = []
    remaining = offset
    for f in BLINK_FRAMES:
        if remaining <= 0:
            out.append(dict(f))
        elif remaining >= f["time"]:
            remaining -= f["time"]
        else:
            out.append({"index": f["index"], "time": f["time"] - remaining})
            remaining = 0
    rest = total - sum(f["time"] for f in out)
    for f in BLINK_FRAMES:
        if rest <= 0:
            break
        take = min(rest, f["time"])
        out.append({"index": f["index"], "time": take})
        rest -= take
    return json.dumps(
        {"animation": {"interpolate": False, "frametime": 1, "frames": out}}, indent=2
    ) + "\n"


def mcmeta(frametime: int, interpolate: bool = True) -> str:
    return json.dumps({"animation": {"frametime": frametime, "interpolate": interpolate}}, indent=2) + "\n"


def build(vanilla: str, assets: str) -> Dict[str, object]:
    src = Sources(vanilla, assets)
    files: Dict[str, object] = {}

    top, top_e = bleached_top(src)
    side, side_e = bleached_side(src, top)
    files["bleached_gloomsculk_top.png"] = top
    files["bleached_gloomsculk_top_e.png"] = top_e
    files["bleached_gloomsculk_side.png"] = side
    files["bleached_gloomsculk_side_e.png"] = side_e
    # match vanilla sculk's cadence so adjacent blocks stay in step
    files["bleached_gloomsculk_side.png.mcmeta"] = mcmeta(20)
    files["bleached_gloomsculk_side_e.png.mcmeta"] = mcmeta(20)

    # Top and sides are generated separately but share CROSSINGS, so the
    # fissures run over the block's top edge and continue down the sides.
    # A texture animates per sprite, not per block, so every molten log in a forest pulses on the same
    # beat off a single sprite - which reads as one machine rather than several fires. There is no
    # per-block phase in the format, so the phase is baked in instead: the same fissures rolled to four
    # evenly spaced points in the cycle, emitted as four sprites the log model picks between at random.
    # MOLTEN_LOG_PHASES has to divide MOLTEN_FRAMES for the spacing to stay even.
    log_sides, log_side_es, log_top, log_top_e = molten_log(src)
    step = MOLTEN_FRAMES // MOLTEN_LOG_PHASES
    for i, suffix in enumerate(MOLTEN_LOG_SUFFIXES):
        roll = i * step
        base = "gloomwood_dark_molten_log_side" + suffix
        files[base + ".png"] = stack(log_sides[roll:] + log_sides[:roll])
        files[base + "_e.png"] = stack(log_side_es[roll:] + log_side_es[:roll])
        files[base + ".png.mcmeta"] = mcmeta(4)
        files[base + "_e.png.mcmeta"] = mcmeta(4)
    files["gloomwood_dark_molten_log_top.png"] = log_top
    files["gloomwood_dark_molten_log_top_e.png"] = log_top_e

    for name, bottom in (("gloomsculk_vine", False), ("gloomsculk_vine_bottom", True)):
        v, v_e = sculk_vine(src, bottom)
        files[name + ".png"] = v
        files[name + "_e.png"] = v_e

    for suffix, shards in CRYSTAL_SIZES.items():
        gc, gc_e = geode_crystal(src, shards)
        files["gloomsculk_geode_crystal%s.png" % suffix] = gc
        files["gloomsculk_geode_crystal%s_e.png" % suffix] = gc_e
    lamp, lamp_e = lamp_core(src)
    files["gloomsculk_lamp_core.png"] = lamp
    files["gloomsculk_lamp_core_e.png"] = lamp_e
    files["gloomsculk_lamp_core.png.mcmeta"] = mcmeta(4)
    files["gloomsculk_lamp_core_e.png.mcmeta"] = mcmeta(4)

    for suffix, shards in CRYSTAL_SIZES.items():
        crys, crys_e = crystal(src, shards)
        files["gloomsculk_crystal%s.png" % suffix] = crys
        files["gloomsculk_crystal%s_e.png" % suffix] = crys_e

    geo_side, geo_top, geo_core, geo_core_e, geo_side_e = geode(src)
    files["gloomsculk_geode_side.png"] = geo_side
    files["gloomsculk_geode_side_e.png"] = geo_side_e
    files["gloomsculk_geode_top.png"] = geo_top
    files["gloomsculk_geode_core.png"] = geo_core
    files["gloomsculk_geode_core_e.png"] = geo_core_e
    for n in ("side", "side_e"):
        files["gloomsculk_geode_%s.png.mcmeta" % n] = mcmeta(20)   # sculk's cadence
    for n in ("core", "core_e"):
        files["gloomsculk_geode_%s.png.mcmeta" % n] = mcmeta(4)    # the molten cadence

    vein_top, vein_top_e, vein_side, vein_side_e = veined(src)
    files["veined_gloomsculk_top.png"] = vein_top
    files["veined_gloomsculk_top_e.png"] = vein_top_e
    files["veined_gloomsculk_side.png"] = vein_side
    files["veined_gloomsculk_side_e.png"] = vein_side_e
    # sculk's own cadence, so the two pulse together where they meet
    for n in ("top", "top_e", "side", "side_e"):
        files["veined_gloomsculk_%s.png.mcmeta" % n] = mcmeta(20)

    mol_top, mol_top_e = molten_face(src, random.Random(SEED + 3), False, face="top")
    mol_side, mol_side_e = molten_face(src, random.Random(SEED + 4), False, face="side")
    files["molten_gloomsculk_top.png"] = mol_top
    files["molten_gloomsculk_top_e.png"] = mol_top_e
    files["molten_gloomsculk_side.png"] = mol_side
    files["molten_gloomsculk_side_e.png"] = mol_side_e
    for n in ("top", "top_e", "side", "side_e"):
        files["molten_gloomsculk_%s.png.mcmeta" % n] = mcmeta(4)

    # open / half / shut, stacked into one strip and driven by an irregular frame list
    blink_frames = [wisp_head(src, blink=b) for b in range(len(EYE_ROWS))]
    head = stack([f[0] for f in blink_frames])
    head_e = stack([f[1] for f in blink_frames])
    still = blink_frames[0][0]
    blank_head, _ = wisp_head(src, face=False)
    # the two segments share the gradient: 0-7 under the head, 8-15 below that
    stem, stem_e = wisp_stem(src, SEED + 5, 0, 7)
    lower, lower_e = wisp_stem(src, SEED + 6, 8, SIZE - 1)
    # One sprite per blink phase. The pixels are identical - it is the mcmeta that differs - but a
    # .mcmeta belongs to one texture, so the copies are what carry the phases.
    blink_cycle = sum(f["time"] for f in BLINK_FRAMES)
    for i, suffix in enumerate(BLINK_SUFFIXES):
        meta = rotate_blink(i * blink_cycle // BLINK_PHASES)
        files["gloomwisp_vine_head%s.png" % suffix] = head
        files["gloomwisp_vine_head%s_e.png" % suffix] = head_e
        files["gloomwisp_vine_head%s.png.mcmeta" % suffix] = meta
        files["gloomwisp_vine_head%s_e.png.mcmeta" % suffix] = meta
    # A still frame of vanilla's soul fire, so the item icon can carry the same crossed flame the block
    # does without the icon animating. Lifted rather than drawn: it has to match the block exactly.
    files["gloomwisp_vine_fire_still.png"] = frames(load(src.vanilla, "soul_fire_0.png"), 32)[12]

    # the item model uses this one: a still frame, so the icon neither blinks nor burns
    files["gloomwisp_vine_head_still.png"] = still
    files["gloomwisp_vine_head_side.png"] = blank_head
    files["gloomwisp_vine_glow.png"] = wisp_glow(src)
    files["gloomwisp_vine_stem.png"] = stem
    files["gloomwisp_vine_stem_e.png"] = stem_e
    files["gloomwisp_vine_stem_lower.png"] = lower
    files["gloomwisp_vine_stem_lower_e.png"] = lower_e

    for i in (1, 2, 3):
        bone = load(assets, f"bone_grass_{i}.png")
        g, g_e = pale_grass(src, bone, i)
        files[f"pale_gloomgrass_{i}.png"] = g
        files[f"pale_gloomgrass_{i}_e.png"] = g_e
        d, d_e = gloom_grass(src, bone, i)
        files[f"gloomgrass_{i}.png"] = d
        files[f"gloomgrass_{i}_e.png"] = d_e

    return files


def preview(files: Dict[str, object], path: str, scale: int = 14) -> None:
    """Contact sheet of every generated PNG, animations laid out left to right."""
    rows = []
    for name, obj in files.items():
        if not isinstance(obj, Image.Image):
            continue
        n = obj.height // SIZE
        row = Image.new("RGBA", (SIZE * n + (n - 1), SIZE), (0, 0, 0, 0))
        for i in range(n):
            row.paste(obj.crop((0, i * SIZE, SIZE, (i + 1) * SIZE)), (i * (SIZE + 1), 0))
        rows.append((name, row))

    pad = 6
    width = max(r.width for _, r in rows) * scale + pad * 2
    height = sum(r.height * scale + pad for _, r in rows) + pad
    sheet = Image.new("RGBA", (width, height), (26, 28, 32, 255))
    y = pad
    for _, r in rows:
        big = r.resize((r.width * scale, r.height * scale), Image.NEAREST)
        sheet.paste(big, (pad, y), big)
        y += big.height + pad
    sheet.save(path)


def main() -> None:
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--vanilla", default=DEFAULT_VANILLA, help="vanilla block texture directory")
    ap.add_argument("--assets", default=DEFAULT_ASSETS, help="betternether block texture directory (sources)")
    ap.add_argument("--out", default=None, help="output directory (default: --assets)")
    ap.add_argument("--preview", default=None, help="also write a scaled-up contact sheet here")
    ap.add_argument("--force", action="store_true", help="overwrite existing files")
    args = ap.parse_args()

    out_dir = args.out or args.assets
    files = build(args.vanilla, args.assets)

    os.makedirs(out_dir, exist_ok=True)
    existing = [n for n in files if os.path.exists(os.path.join(out_dir, n))]
    if existing and not args.force:
        raise SystemExit(
            "refusing to overwrite %d existing file(s), pass --force:\n  %s"
            % (len(existing), "\n  ".join(sorted(existing)))
        )

    for name, obj in files.items():
        path = os.path.join(out_dir, name)
        if isinstance(obj, Image.Image):
            obj.save(path)
        else:
            with open(path, "w") as fh:
                fh.write(obj)
    print("wrote %d files to %s" % (len(files), out_dir))

    if args.preview:
        preview(files, args.preview)
        print("preview: %s" % args.preview)


if __name__ == "__main__":
    main()
