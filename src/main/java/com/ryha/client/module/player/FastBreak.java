package com.ryha.client.module.player;

import com.ryha.client.module.Category;
import com.ryha.client.module.Module;
import com.ryha.client.module.setting.SliderSetting;

/**
 * Increases mining speed.
 * Hook in ClientPlayerInteractionManagerMixin increases break progress per tick.
 */
public class FastBreak extends Module {

    public final SliderSetting multiplier = registerSetting(
        new SliderSetting("Multiplier", "Break speed multiplier", 2.0, 1.0, 10.0, 0.5));

    public FastBreak() {
        super("FastBreak", "Increases block breaking speed", Category.PLAYER);
    }
}
