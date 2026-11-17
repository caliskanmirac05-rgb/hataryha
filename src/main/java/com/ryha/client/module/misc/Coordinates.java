package com.ryha.client.module.misc;

import com.ryha.client.module.Category;
import com.ryha.client.module.Module;
import com.ryha.client.module.setting.BooleanSetting;

/** Shows XYZ coordinates on screen. Rendered via HUDRenderer. */
public class Coordinates extends Module {

    public final BooleanSetting netherCoords = registerSetting(
        new BooleanSetting("NetherCoords","Show Nether equivalents", true));
    public final BooleanSetting biome        = registerSetting(
        new BooleanSetting("Biome", "Show current biome", false));

    public Coordinates() {
        super("Coordinates", "Display XYZ position on screen", Category.MISC);
    }
}
