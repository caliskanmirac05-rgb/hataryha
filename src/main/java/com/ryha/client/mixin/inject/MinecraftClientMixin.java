package com.ryha.client.mixin.inject;

import com.ryha.client.RyHaClient;
import com.ryha.client.module.player.InventoryMove;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {

    @Shadow public Gui gui;

    /**
     * InventoryMove: temporarily hide the open screen from vanilla keybind
     * handling so movement keys fire even when a GUI is open.
     *
     * In MC 26.2 the current screen lives in Minecraft.gui.
     * gui.screen() returns the current Screen; gui.setScreen(null) hides it.
     */
    @Inject(method = "handleKeybinds", at = @At("HEAD"))
    private void inventoryMoveHide(CallbackInfo ci) {
        if (RyHaClient.getInstance() == null) return;
        InventoryMove im = RyHaClient.getInstance().getModuleManager().get(InventoryMove.class);
        if (im != null && im.isEnabled() && gui.screen() != null) {
            inventoryMove$savedScreen = gui.screen();
            gui.setScreen(null);
        }
    }

    @Inject(method = "handleKeybinds", at = @At("TAIL"))
    private void inventoryMoveRestore(CallbackInfo ci) {
        if (inventoryMove$savedScreen != null) {
            gui.setScreen(inventoryMove$savedScreen);
            inventoryMove$savedScreen = null;
        }
    }

    private transient Screen inventoryMove$savedScreen = null;
}
