## CivRadar (FML 1.12.2)
A radar mod for Civcraft

All the gradle stuff is from [Lunatrius](https://github.com/Lunatrius/Schematica) so ty for that

Installing and Using CivRadar
---
1. Run Minecraft 1.12.2 at least once
2. Download the [Forge 14.23.1.2555 Installer](http://files.minecraftforge.net/maven/net/minecraftforge/forge/1.12.2-14.23.1.2555/forge-1.12.2-14.23.1.2555-installer.jar) or [another version](http://files.minecraftforge.net) (OTHER VERSIONS ARE NOT OFFICIALLY SUPPORTED BUT MAY WORK)
3. Run the installer and install forge
4. [Open your .minecraft folder](http://minecraft.gamepedia.com/.minecraft)
5. Download the [latest CivRadar release](http://github.com/CivcraftMods/civradar/releases)
5. if you don't see a folder called 'mods', create one, then put the CivRadar jar in the mods folder
6. Open the minecraft launcher
7. Create a new profile (under "Launch options") and select the version 'release 1.12.2-forge1.12.2-14.23.1.2555'
8. Run the forge profile and proceed to enjoy the mod!

Compiling from Source
---

This mod is compiled using the Forge Mod Loader (FML) mod pack which includes data from the Minecraft Coder Pack (MCP).

To compile this mod from the source code provided

1. Clone the repo
2. Open le command line
3. run gradlew setupDevWorkspace
4. run gradlew build
5. BOOM! it'll be in the build/libs folder
