# Wurst 7.50.3 – Forge 1.20.1 port (unofficial)

Port of Wurst Client `v7.50.3-MC1.20.1` from Fabric to **Minecraft Forge 1.20.1 (47.x)**.

## How it's built

The source still uses Yarn names. It's built with **Architectury Loom** (`loom.platform=forge`),
which remaps the output to Forge's SRG names. Because of that, almost none of the ~600 source
files had to change.

```sh
# Gradle needs Java 21 to run (the mod itself targets Java 17)
export JAVA_HOME=/path/to/jdk-21
./gradlew build            # -> build/libs/Wurst-Client-Forge-v7.50.3-MC1.20.1-Forge.jar
./gradlew runClient        # dev client
./gradlew runE2eTest       # boots the game and runs Wurst's end-to-end test
```

## What changed vs. the Fabric version

| Fabric | Forge port |
|---|---|
| `fabric.mod.json` | `META-INF/mods.toml` + `pack.mcmeta` |
| `ModInitializer` entrypoint | `@Mod("wurst")` class; the real init runs from `LanguageManagerMixin` when `MinecraftClient`'s constructor creates the `LanguageManager`, so it stays on the render thread and runs before the first resource reload, as on Fabric |
| `wurst.accesswidener` | converted to `META-INF/accesstransformer.cfg` at build time; added 10 `RenderPhase`/`RenderLayer` entries that Fabric API used to widen |
| Fabric API `Screens.getButtons` | `net.wurstclient.forge.Screens` (returns a copy; `GameMenuScreenMixin` now calls `addDrawableChild`) |
| `ClientLoginConnectionEvents.INIT` | new `ClientLoginNetworkHandlerMixin` |
| `FabricLoader` (mod list, versions, dev env, game dir) | `net.wurstclient.forge.ForgePlatform` / `FMLPaths` |
| MixinExtras (bundled in Fabric Loader) | `mixinextras-forge` bundled via Jar-in-Jar |
| Sinytra Connector HUD workaround | removed (not needed natively) |
| `AbstractBlockRenderContextMixin` (Fabric Indigo renderer, X-Ray opacity) | replaced by `ChunkRebuildTaskMixin`. It wraps Forge's `BakedModel.getRenderTypes()` call to put every block on the translucent layer, and wraps `renderBatched()` so non-ore blocks get an `AlphaScalingVertexConsumer` that scales vertex alpha. Vanilla `RenderLayers.getBlockLayer` (which upstream's `RenderLayersMixin` hooks) isn't used by Forge's chunk builder. |

## Test status (2026-11-01, macOS arm64)
`./gradlew runE2eTest` passes these: title screen + Alt Manager button, world creation,
**mixin audit (every injection applies)**, AutoMine, Freecam, X-Ray (incl. opacity mode, checked
visually in the screenshots), `.copyitem`, `.modify`,
piston crash test, Wurst Options in the game menu, clean quit.

Two upstream tests are timing-sensitive on this machine:
- `NoFallHackTest`: the health check runs before the integrated server applies fall damage. It
  fails the **same way on unmodified upstream Fabric Wurst** (control run), so the port didn't
  cause it.
- `GiveCmdTest`: sometimes the previous test's `/clear` reaches the server after `.give`, so the
  1-tick check sees an empty slot. It's intermittent (passed on the latest run) and passes with a
  20-tick wait. It wasn't checked on Fabric.

## Other Forge-specific changes
- `EntityMixin` (AntiWaterPush) now targets Forge's fluid-push lambda
  `lambda$updateFluidHeightAndDoFluidPushing$29`, because Forge replaces vanilla's
  `updateMovementInFluid` body. Lambda numbering can change between Forge versions, so recheck
  this if you bump `forge_version`.
- The HUD (Wurst logo, hack list, etc.) is drawn from Forge's `RenderGuiEvent.Post`, because
  `ForgeGui` overrides `InGameHud.render()`.
- Title screen: Forge puts a "Mods" button on the Realms row, so that row becomes three 64px
  buttons [Mods][Realms][Alt Manager]. "Minecraft Realms" gets clipped at that width.

## Known limitations
- **Sodium compatibility mixins** are `@Pseudo` and target Fabric Sodium's packages. On Forge
  (Embeddium/Rubidium) they just don't apply. Those mods also replace the vanilla chunk builder,
  so `ChunkRebuildTaskMixin` (X-Ray opacity) won't run with them installed either.
- Analytics still report to Wurst's Plausible instance (now with a `forge_version` prop). This can
  be turned off in Wurst Options.
- Spotless and the upstream release/upload tasks were removed from `build.gradle`.
