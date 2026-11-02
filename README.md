# Wurst-Forge-Port
A forge port of the client Wurst, ported over using Claude Code's Opus 5.5

This is an **unofficial** port of [Wurst Client](https://github.com/Wurst-Imperium/Wurst7) v7.50.3
for Minecraft 1.20.1 from Fabric to **Minecraft Forge 1.20.1 (47.x)**. It is not affiliated with
or supported by Wurst-Imperium; please don't report problems with this port to them.

## Installation

1. Install [Minecraft Forge](https://files.minecraftforge.net/) 1.20.1 (47.x).
2. Put `Wurst-Client-Forge-v7.50.3-MC1.20.1-Forge.jar` in your `mods` folder.
   Fabric API is **not** needed; MixinExtras is bundled in the jar.

You need a licensed copy of Minecraft Java Edition. Wurst is a cheat client, not a pirate client.

## Building

Gradle has to run on **Java 21**. The mod itself targets Java 17, which Gradle downloads on its own.

```sh
git clone https://github.com/Luxander030/Wurst-Forge-Port.git
cd Wurst-Forge-Port
export JAVA_HOME=/path/to/jdk-21
./gradlew build        # -> build/libs/Wurst-Client-Forge-v7.50.3-MC1.20.1-Forge.jar
./gradlew runClient    # start a dev client
./gradlew runE2eTest   # boot the game and run Wurst's end-to-end test
```

## What changed vs. upstream

See [FORGE_PORT.md](FORGE_PORT.md) for everything changed for Forge, test status and known
limitations.

## Credits & license

Wurst Client is made by [Wurst-Imperium](https://github.com/Wurst-Imperium) and contributors
(Alexander01998 et al.). This port is licensed, like the original, under the
[GNU General Public License v3.0](LICENSE) or later.
