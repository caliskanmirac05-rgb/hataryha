package com.ryha.client.module.combat;

import com.ryha.client.module.Category;
import com.ryha.client.module.Module;
import com.ryha.client.module.setting.SliderSetting;

/**
 * Increases the player's attack reach.
 * The actual interception happens in ClientPlayerInteractionManagerMixin.
 */
public class Reach extends Module {

    public final SliderSetting attackReach   = registerSetting(new SliderSetting("AttackReach", "Attack reach distance", 3.5, 3.0, 6.0, 0.1));
    public final SliderSetting interactReach = registerSetting(new SliderSetting("InteractReach","Interact reach distance", 4.5, 3.0, 6.0, 0.1));

    public Reach() {
        super("Reach", "Increases attack and interaction reach", Category.COMBAT);
    }
}
