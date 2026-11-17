package com.ryha.client.module.player;

import com.ryha.client.module.Category;
import com.ryha.client.module.Module;

/**
 * Removes the delay between placing blocks.
 * Mixin: GameOptionsMixin reduces rightClickDelay to 0.
 */
public class FastPlace extends Module {
    public FastPlace() {
        super("FastPlace", "Removes the block placement delay", Category.PLAYER);
    }
}
