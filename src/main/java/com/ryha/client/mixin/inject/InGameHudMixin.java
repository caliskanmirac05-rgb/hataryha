package com.ryha.client.mixin.inject;

import com.ryha.client.gui.hud.HUDRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class InGameHudMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(GuiGraphicsExtractor context, float tickDelta, CallbackInfo ci) {
        HUDRenderer.render(context, tickDelta);
    }
}
