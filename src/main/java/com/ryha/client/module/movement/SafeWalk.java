package com.ryha.client.module.movement;

import com.ryha.client.module.Category;
import com.ryha.client.module.Module;

/**
 * Prevents falling off edges (like sneaking).
 * Hook in ClientPlayerEntityMixin: always returns true for isSneaking()
 * when checking edge detection.
 */
public class SafeWalk extends Module {
    public SafeWalk() {
        super("SafeWalk", "Prevents walking off edges", Category.MOVEMENT);
    }
}
