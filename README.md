<p align="center">
  <b><a>Welcome to BetterRTP's repository!</a></b>
</p>

## Where's the Lang files?/Want to Contribute translating?  
All language files are located [here](src/main/resources/lang)
feel free to fork one of the language files and help translate!

## Libraries
BetterRTP uses and is compiled with the following libraries:

- [ParticleLib](https://github.com/ByteZ1337/ParticleLib) (included) - Legacy Minecraft 1.8 particles only. Newer servers use the public Bukkit particle API.
- [PaperLib](https://github.com/PaperMC/PaperLib) (included) - Library for interfacing with PaperMC specific APIs, used for async chunk loading.
- [FoliaLib](https://github.com/TechnicallyCoded/FoliaLib) (included) - Library for interfacing with Folia specific APIs, used for cross-platform timers.

## Build instructions on Ubuntu

mvn clean install

The file will be in the Target file.

## Minecraft 1.21 compatibility patch

Particles on Minecraft 1.9+ now use `Player.spawnParticle` instead of internal
Minecraft packets. Older configuration names (including `EXPLOSION_NORMAL`)
are translated to names available on the running server. Minecraft 1.8 retains
ParticleLib. Particle rendering runs on the player's scheduler, and reloads
clear the previous effects. `/rtp info particles` lists usable effects for the
current server; particles requiring block/item/color data are not supported by
the current configuration and use the fallback effect if configured.

Keep `api-version: '1.13'`: this is a minimum API declaration, not a maximum
Minecraft version. Do not raise it to 1.21 if older servers are still required.

Standalone bridge regression tests (no Minecraft server required):

```powershell
./tools/test-particles.ps1 -ApiJar path/to/spigot-api.jar -GuavaJar path/to/guava.jar
# Add -Legacy for the Minecraft 1.8.8 API; use a JDK 21 installation for 1.21 APIs.
```

Verified against Spigot APIs 1.16.5, 1.20.4, 1.21.1, 1.21.4 and 1.21.5;
1.8.8 legacy-backend detection was also verified. These are bridge-level tests,
not a full server compatibility certification. Full Maven packaging was also
verified with JDK 21 (`mvn -B -ntp package -DskipTests`); the standalone tests
above are executed separately, not by Maven Surefire.

Before production use, test a full build on a separate 1.21 server: plugin
startup, `/rtp`, `/rtp reload`, `/rtp info particles`, all three particle shapes,
cooldowns, and the installed claim-protection integrations. Verify existing
worlds/configuration are backed up before replacing the installed plugin.

## Where's the Wiki?  
The wiki is available [here](../../wiki)!
    
<p align="center">
  <b>Chat with us on Discord</b><br/>
  <a href="https://discord.gg/8Kt4wKm"><img src="https://img.shields.io/discord/182633513474850818.svg?longCache=true&style=flat-square&label=Discord" alt="Discord" /></a><br/>
  <b>Have a Suggestion? Make an issue!</b><br/>
  <a href="../../issues"><img src="https://img.shields.io/github/issues-raw/SuperRonanCraft/BetterRTP.svg?longCache=true&style=flat-square&label=Issues" alt="GitHub issues" /></a><br/>
  <br/>
  <a href="https://www.spigotmc.org/resources/36081/">Thank you for viewing the Wiki for BetterRTP!</a><br/>
  <i><a>Did this wiki help you out? Please give it a <b>Star</b> so I know it's getting use!</a></i><br/>
  <br/>
  <b><i><a href="https://www.spigotmc.org/resources/authors/superronancraft.13025/">Check out my other plugins!</a></i></b>
</p>
