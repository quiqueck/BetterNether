#!/usr/bin/env python3
"""Resolve every asset reference shipped by BetterNether and list the dangling ones.

Checks, for all hand-written and datagen-generated assets of this mod:
  * blockstates  -> models        (variants[*].model, multipart[*].apply.model)
  * items/       -> models        (every "model" / "base" string in an item definition)
  * models       -> parent models
  * models       -> textures      (textures.* -> textures/<path>.png, '#refs' must resolve)
  * equipment/   -> equipment textures

References are resolved against this mod, the WorldWeaver/BCLib/WunderLib checkouts next to it
and the vanilla client jar from the Loom cache.

Usage: python3 tools/check_asset_refs.py [--unreferenced] [--mc 26.2] [--project ../../26.1/BetterNether]
Exit code 1 if any dangling reference is found.
"""
import argparse
import json
import os
import sys
import zipfile
from pathlib import Path

MOD = "betternether"
HERE = Path(__file__).resolve().parent.parent
LIB_GLOBS = ["WorldWeaver/**/src/main/resources/assets", "BCLib/src/main/resources/assets",
             "WunderLib/src/main/resources/assets"]
BUILTIN_PARENTS = {"builtin/generated", "builtin/entity", "minecraft:builtin/generated",
                   "minecraft:builtin/entity"}


class Assets:
    def __init__(self, mc_version):
        global OWN_ROOTS
        OWN_ROOTS = [HERE / "src/main/resources/assets", HERE / "src/main/generated/assets"]
        self.files = {}  # "ns/kind/path.ext" -> source (Path or None for jar)
        self.own = {}  # subset that belongs to this mod
        ws = HERE.parent
        roots = [r for g in LIB_GLOBS for r in ws.glob(g)]
        for root in roots + OWN_ROOTS:
            for f in root.rglob("*"):
                if f.is_file():
                    key = f.relative_to(root).as_posix()
                    self.files[key] = f
                    if root in OWN_ROOTS:
                        self.own[key] = f
        jar = Path.home() / f".gradle/caches/fabric-loom/{mc_version}/minecraft-client.jar"
        if not jar.exists():
            sys.exit(f"vanilla client jar not found: {jar}")
        self.jar = zipfile.ZipFile(jar)
        if True:
            z = self.jar
            for n in z.namelist():
                if n.startswith("assets/") and not n.endswith("/"):
                    self.files.setdefault(n[len("assets/"):], None)

    def has(self, key):
        return key in self.files

    def json(self, key):
        src = self.files.get(key)
        if src is None:
            return json.loads(self.jar.read("assets/" + key))
        with open(src, encoding="utf-8") as fh:
            return json.load(fh)


def rl(ref):
    ns, _, path = ref.rpartition(":")
    return (ns or "minecraft"), path


def model_key(ref):
    ns, path = rl(ref)
    return f"{ns}/models/{path}.json"


def texture_key(ref):
    ns, path = rl(ref)
    return f"{ns}/textures/{path}.png"


def is_atlas_permutation(ref):
    """Vanilla's trim overlays (trims/items/<piece>_trim_<material>) have no .png: the item atlas builds
    them at runtime from a paletted_permutations source, so they can't be resolved as files."""
    ns, path = rl(ref)
    return ns == "minecraft" and path.startswith("trims/items/") and "_trim_" in path


def walk_item_models(node, out):
    if isinstance(node, dict):
        for k, v in node.items():
            if k in ("model", "base") and isinstance(v, str):
                out.append(v)
            else:
                walk_item_models(v, out)
    elif isinstance(node, list):
        for v in node:
            walk_item_models(v, out)


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--mc", default="26.2")
    ap.add_argument("--unreferenced", action="store_true",
                    help="also list own models no blockstate/item/model points at")
    ap.add_argument("--project", type=Path, help="BetterNether checkout to scan (default: this one)")
    args = ap.parse_args()
    if args.project:
        global HERE
        HERE = args.project.resolve()
    a = Assets(args.mc)
    problems = []
    referenced_models = set()
    registry = HERE / "src/main/generated/block_registrations.json"
    blocks = {e["id"] for e in json.loads(registry.read_text())} if registry.exists() else None
    leaf_models = set()

    def need_model(src, ref):
        if ref in BUILTIN_PARENTS:
            return
        key = model_key(ref)
        referenced_models.add(key)
        if not a.has(key):
            problems.append((src, "model", ref))

    for key in sorted(a.own):
        ns, kind, *_ = key.split("/")
        if not key.endswith(".json") or kind not in ("blockstates", "items", "models", "equipment"):
            continue
        data = a.json(key)
        if kind == "blockstates":
            block = f"{ns}:" + key[len(f"{ns}/blockstates/"):-len(".json")]
            if ns == MOD and blocks is not None and block not in blocks:
                problems.append((key, "orphan-blockstate", f"{block} is not a registered block"))
            if "forge_marker" in data:
                problems.append((key, "legacy-blockstate", "forge_marker format is not loadable"))
                continue
            refs = []
            for v in (data.get("variants") or {}).values():
                refs += v if isinstance(v, list) else [v]
            for part in data.get("multipart") or []:
                app = part.get("apply")
                refs += app if isinstance(app, list) else [app]
            for r in refs:
                need_model(key, r["model"])
                leaf_models.add(r["model"])
        elif kind == "items":
            refs = []
            walk_item_models(data.get("model"), refs)
            for r in refs:
                need_model(key, r)
                leaf_models.add(r)
        elif kind == "models":
            if "parent" in data:
                need_model(key, data["parent"])
            textures = dict(data.get("textures") or {})
            for name, t in textures.items():
                if isinstance(t, dict):
                    t = t.get("sprite")
                if not isinstance(t, str):
                    continue
                if t.startswith("#"):
                    continue  # resolved by a child or by the elements; checked below
                if is_atlas_permutation(t):
                    continue
                if not a.has(texture_key(t)):
                    problems.append((key, "texture", f"{name} = {t}"))
        elif kind == "equipment":
            for layer, entries in (data.get("layers") or {}).items():
                for e in entries:
                    ns2, path = rl(e["texture"])
                    tk = f"{ns2}/textures/entity/equipment/{layer}/{path}.png"
                    if not a.has(tk):
                        problems.append((key, "equipment-texture", tk))

    # every '#var' a leaf model's elements use must resolve to a real texture through the parent chain
    for ref in sorted(leaf_models):
        chain, k = [], model_key(ref)
        while k and a.has(k) and len(chain) < 32:
            d = a.json(k)
            chain.append(d)
            parent = d.get("parent")
            k = model_key(parent) if parent and parent not in BUILTIN_PARENTS else None
        if not chain or (k and not a.has(k)):
            continue  # missing model/parent already reported
        textures = {}
        for d in reversed(chain):
            textures.update(d.get("textures") or {})
        elements = next((d["elements"] for d in chain if "elements" in d), [])
        used = {f["texture"][1:] for el in elements for f in (el.get("faces") or {}).values()
                if f.get("texture", "").startswith("#")}
        for var in sorted(used):
            t, seen = textures.get(var), set()
            while isinstance(t, str) and t.startswith("#") and t not in seen:
                seen.add(t)
                t = textures.get(t[1:])
            if isinstance(t, dict):
                t = t.get("sprite")
            if not isinstance(t, str) or t.startswith("#"):
                problems.append((model_key(ref), "texture-var", f"#{var} never assigned"))

    for src, what, ref in problems:
        print(f"DANGLING {what:18} {ref:60} <- {src}")
    print(f"{len(problems)} dangling reference(s)")

    if args.unreferenced:
        own_models = {k for k in a.own if k.startswith(f"{MOD}/models/")}
        stale = sorted(own_models - referenced_models)
        for k in stale:
            print(f"UNREFERENCED {k}")
        print(f"{len(stale)} unreferenced model(s) (some may be used from code, e.g. special renderers)")

    return 1 if problems else 0


if __name__ == "__main__":
    sys.exit(main())
