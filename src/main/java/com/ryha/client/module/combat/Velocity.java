package com.ryha.client.module.combat;

import com.ryha.client.module.Category;
import com.ryha.client.module.Module;
import com.ryha.client.module.setting.SliderSetting;

/**
 * Reduces incoming knockback.
 * Interception occurs in LivingEntityMixin#takeKnockback.
 */
public class Velocity extends Module {

    public final SliderSetting horizontal = registerSetting(new SliderSetting("Horizontal", "Horizontal knockback %", 0, 0, 100, 1));
    public final SliderSetting vertical   = registerSetting(new SliderSetting("Vertical",   "Vertical knockback %",   0, 0, 100, 1));

    public Velocity() {
        super("Velocity", "Reduces knockback received from attacks", Category.COMBAT);
    }
}
