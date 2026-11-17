package com.ryha.client.module.render;

import com.ryha.client.module.Category;
import com.ryha.client.module.Module;
import com.ryha.client.module.setting.BooleanSetting;
import com.ryha.client.module.setting.SliderSetting;

/**
 * Renders nametags above players with health/armor information.
 * Rendering hook in WorldRendererMixin.
 */
public class NameTags extends Module {

    public final BooleanSetting health   = registerSetting(new BooleanSetting("Health", "Show health", true));
    public final BooleanSetting ping     = registerSetting(new BooleanSetting("Ping",   "Show ping",   true));
    public final BooleanSetting items    = registerSetting(new BooleanSetting("Items",  "Show held item", false));
    public final SliderSetting  scale    = registerSetting(new SliderSetting("Scale",   "Tag scale", 1.0, 0.5, 3.0, 0.1));

    public NameTags() {
        super("NameTags", "Enhanced nametags with health, ping, and gear", Category.RENDER);
    }
}
