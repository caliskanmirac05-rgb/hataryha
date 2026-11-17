package com.ryha.client.module.combat;

import com.ryha.client.module.Category;
import com.ryha.client.module.Module;
import com.ryha.client.module.setting.SliderSetting;

/**
 * Expands entity hitboxes client-side.
 * Interception in EntityMixin#getBoundingBox.
 */
public class Hitbox extends Module {

    public final SliderSetting expansion = registerSetting(new SliderSetting("Size", "Hitbox expansion", 0.15, 0.0, 1.0, 0.01));

    public Hitbox() {
        super("Hitbox", "Expands entity hitboxes to make them easier to hit", Category.COMBAT);
    }
}
