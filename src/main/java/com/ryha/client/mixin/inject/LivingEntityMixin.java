package com.ryha.client.mixin.inject;

import com.ryha.client.RyHaClient;
import com.ryha.client.event.events.AttackEvent;
import com.ryha.client.module.combat.AntiKnockback;
import com.ryha.client.module.combat.Velocity;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @ModifyVariable(
        method = "takeKnockback",
        at = @At("HEAD"),
        argsOnly = true,
        index = 1 // `strength` parameter
    )
    private double modifyKnockback(double strength) {
        LivingEntity self = (LivingEntity)(Object) this;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || self != mc.player) return strength;
        if (RyHaClient.getInstance() == null) return strength;

        var mm = RyHaClient.getInstance().getModuleManager();

        if (mm.isEnabled(AntiKnockback.class)) return 0.0;

        Velocity vel = mm.get(Velocity.class);
        if (vel != null && vel.isEnabled()) {
            return strength * (vel.horizontal.getValue() / 100.0);
        }
        return strength;
    }
}
