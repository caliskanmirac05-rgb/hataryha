package com.ryha.client.module.combat;

import com.ryha.client.module.Category;
import com.ryha.client.module.Module;

/**
 * Fully cancels all knockback.
 * See LivingEntityMixin for the velocity cancellation.
 */
public class AntiKnockback extends Module {
    public AntiKnockback() {
        super("AntiKnockback", "Completely cancels knockback", Category.COMBAT);
    }
}
