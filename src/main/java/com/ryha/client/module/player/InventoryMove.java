package com.ryha.client.module.player;

import com.ryha.client.module.Category;
import com.ryha.client.module.Module;

/**
 * Allows movement while inventory is open.
 * Hook in MinecraftClientMixin: skip movement input block when screen is open.
 */
public class InventoryMove extends Module {
    public InventoryMove() {
        super("InventoryMove", "Move while inventory/GUI is open", Category.PLAYER);
    }
}
