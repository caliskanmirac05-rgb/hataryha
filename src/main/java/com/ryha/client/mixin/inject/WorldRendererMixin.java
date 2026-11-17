package com.ryha.client.mixin.inject;

import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Placeholder for XRay chunk rendering hooks.
 * Full XRay requires redirecting BlockRenderManager.renderBlock() per-block
 * to skip non-whitelisted blocks — a heavier change that depends on
 * the chunk rebuild pipeline in the target MC version.
 *
 * The class is registered in ryha.mixins.json so it compiles cleanly;
 * real logic is added when targeting the specific render path.
 */
@Mixin(LevelRenderer.class)
public abstract class WorldRendererMixin {
    // XRay hook site: renderChunkLayer / renderBlock
}
