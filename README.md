[![](https://jitpack.io/v/quiqueck/BetterNether.svg)](https://jitpack.io/#quiqueck/BetterNether)

# BetterNether
BetterNether Mod for Fabric, MC 1.19

Building:
* Clone repo
* Edit gradle.properties if necessary
* Open `build.gradle` as new Project in IntelliJ

# Local Versions of BetterX mods
By default, the mod will try to load other BetterX dependencies from
local folder. If you have the sources for BClib installed in a folder 
called **BCLib** that is a sibling of this repo's folder, it will 
be used instead of the binary version from our online repo.

You configure the inclusion of other BetterX dependencies in the
`settings.betterx.gradle` file.
