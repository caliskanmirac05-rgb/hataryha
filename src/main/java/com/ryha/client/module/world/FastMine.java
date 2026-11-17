package com.ryha.client.module.world;

import com.ryha.client.module.Category;
import com.ryha.client.module.Module;

/**
 * Alias/companion to FastBreak exposed in the World category.
 * Shares the same mixin hook for increasing block break progress.
 */
public class FastMine extends Module {
    public FastMine() {
        super("FastMine", "Mine blocks instantly (single-tick break)", Category.WORLD);
    }
}
