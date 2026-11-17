package com.ryha.client.module.render;

import com.ryha.client.module.Category;
import com.ryha.client.module.Module;
import com.ryha.client.module.setting.BooleanSetting;

/**
 * Makes most blocks transparent except ores and valuable blocks.
 * Hook in WorldRendererMixin: skip rendering non-whitelisted blocks.
 */
public class XRay extends Module {

    public final BooleanSetting diamonds    = registerSetting(new BooleanSetting("Diamonds",    "Show diamond ore",         true));
    public final BooleanSetting emeralds    = registerSetting(new BooleanSetting("Emeralds",    "Show emerald ore",         true));
    public final BooleanSetting iron        = registerSetting(new BooleanSetting("Iron",        "Show iron ore",            true));
    public final BooleanSetting gold        = registerSetting(new BooleanSetting("Gold",        "Show gold ore",            true));
    public final BooleanSetting ancient     = registerSetting(new BooleanSetting("Ancient",     "Show ancient debris",      true));
    public final BooleanSetting chests      = registerSetting(new BooleanSetting("Chests",      "Show chests",              true));
    public final BooleanSetting ores        = registerSetting(new BooleanSetting("AllOres",     "Show all ores",            false));
    // 26.2 Chaos Cubed — new Sulfur Caves ore family
    public final BooleanSetting sulfur      = registerSetting(new BooleanSetting("Sulfur",      "Show sulfur ore (26.2)",   true));
    public final BooleanSetting cinnabar    = registerSetting(new BooleanSetting("Cinnabar",    "Show cinnabar ore (26.2)", true));
    public final BooleanSetting potentSulfur= registerSetting(new BooleanSetting("PotentSulfur","Show potent sulfur (26.2)",true));

    public XRay() {
        super("XRay", "See through blocks to find ores and chests", Category.RENDER);
    }

    @Override
    protected void onEnable()  { reloadChunks(); }
    @Override
    protected void onDisable() { reloadChunks(); }

    private void reloadChunks() {
        if (mc.levelRenderer != null) mc.levelRenderer.allChanged();
    }
}
