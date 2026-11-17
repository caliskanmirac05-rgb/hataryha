package com.ryha.client.module.world;

import com.ryha.client.module.Category;
import com.ryha.client.module.Module;
import com.ryha.client.module.setting.SliderSetting;

/**
 * Changes the client-side tick rate (game speed).
 * Hook in MinecraftClientMixin: multiplies the tick delta.
 */
public class Timer extends Module {

    public final SliderSetting speed = registerSetting(
        new SliderSetting("Speed", "Timer multiplier (1 = normal)", 1.0, 0.1, 10.0, 0.1));

    public Timer() {
        super("Timer", "Speeds up or slows down game tick rate", Category.WORLD);
    }
}
