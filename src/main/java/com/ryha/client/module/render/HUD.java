package com.ryha.client.module.render;

import com.ryha.client.module.Category;
import com.ryha.client.module.Module;
import com.ryha.client.module.setting.BooleanSetting;
import com.ryha.client.module.setting.ModeSetting;

/**
 * Controls the RyHa HUD overlay.
 * Rendered by HUDRenderer which is hooked in InGameHudMixin.
 */
public class HUD extends Module {

    public final BooleanSetting showFPS    = registerSetting(new BooleanSetting("FPS",    "Show FPS counter",        true));
    public final BooleanSetting showCPS    = registerSetting(new BooleanSetting("CPS",    "Show CPS counter",        true));
    public final BooleanSetting showPing   = registerSetting(new BooleanSetting("Ping",   "Show server ping",        true));
    public final BooleanSetting showCoords = registerSetting(new BooleanSetting("Coords", "Show coordinates",        true));
    public final BooleanSetting showSpeed  = registerSetting(new BooleanSetting("Speed",  "Show movement speed",     true));
    public final BooleanSetting showTime   = registerSetting(new BooleanSetting("Time",   "Show real-world time",    false));
    public final BooleanSetting showModList= registerSetting(new BooleanSetting("ModList","Show active module list", true));
    public final ModeSetting    colorMode  = registerSetting(new ModeSetting("Color", "Module list color", "RGB", "RGB", "Static", "Gradient"));

    public HUD() {
        super("HUD", "Main heads-up display overlay", Category.RENDER);
    }
}
