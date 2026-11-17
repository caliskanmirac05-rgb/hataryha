package com.ryha.client.mixin.inject;

import com.ryha.client.RyHaClient;
import com.ryha.client.event.events.KeyEvent;
import com.ryha.client.gui.ClickGUI;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardMixin {

    @Shadow private Minecraft minecraft;

    @Inject(method = "keyPress", at = @At("HEAD"))
    private void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (RyHaClient.getInstance() == null) return;
        if (action != GLFW.GLFW_PRESS) return;

        // Right Shift → open/close ClickGUI
        if (key == GLFW.GLFW_KEY_RIGHT_SHIFT) {
            if (minecraft.gui.screen() instanceof ClickGUI) {
                minecraft.gui.setScreen(null);
            } else {
                minecraft.gui.setScreen(RyHaClient.getInstance().getClickGUI());
            }
            return;
        }

        // Post key event to bus
        RyHaClient.getInstance().getEventBus().post(new KeyEvent(key, action, modifiers));

        // Module keybind dispatch
        if (minecraft.gui.screen() == null) {
            RyHaClient.getInstance().getModuleManager().onKeyPress(key);
        }
    }
}
