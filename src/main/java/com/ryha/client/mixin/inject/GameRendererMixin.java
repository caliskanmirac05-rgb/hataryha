package com.ryha.client.mixin.inject;

import com.ryha.client.RyHaClient;
import com.ryha.client.event.events.RenderEvent;
import com.ryha.client.module.world.Timer;
import net.minecraft.client.renderer.GameRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "renderWorld", at = @At("HEAD"))
    private void onRenderWorld(float tickDelta, long limitTime, PoseStack matrices, CallbackInfo ci) {
        if (RyHaClient.getInstance() == null) return;
        RyHaClient.getInstance().getEventBus().post(new RenderEvent(matrices, tickDelta));
    }

    // Timer module: modify tickDelta to speed up/slow down
    @ModifyVariable(method = "renderWorld", at = @At("HEAD"), argsOnly = true, index = 1)
    private float modifyTickDelta(float tickDelta) {
        if (RyHaClient.getInstance() == null) return tickDelta;
        Timer timer = RyHaClient.getInstance().getModuleManager().get(Timer.class);
        if (timer == null || !timer.isEnabled()) return tickDelta;
        return (float)(tickDelta * timer.speed.getValue());
    }
}
