package com.ryha.client.module.movement;

import com.ryha.client.module.Category;
import com.ryha.client.module.Module;
import com.ryha.client.module.setting.BooleanSetting;

/**
 * Prevents speed reduction while eating, blocking, using items, etc.
 * Hook is in ClientPlayerEntityMixin.
 */
public class NoSlow extends Module {

    public final BooleanSetting blocking = registerSetting(new BooleanSetting("Blocking", "No slow while blocking", true));
    public final BooleanSetting eating   = registerSetting(new BooleanSetting("Eating",   "No slow while eating",   true));
    public final BooleanSetting soulsand = registerSetting(new BooleanSetting("Soulsand", "No slow on soul sand",   true));
    public final BooleanSetting cobweb   = registerSetting(new BooleanSetting("Cobweb",   "No slow in cobwebs",     true));

    public NoSlow() {
        super("NoSlow", "Prevents slowdown from items and blocks", Category.MOVEMENT);
    }
}
