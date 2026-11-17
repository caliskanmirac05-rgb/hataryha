package com.ryha.client.mixin.inject;

import com.ryha.client.RyHaClient;
import com.ryha.client.module.combat.Reach;
import com.ryha.client.module.player.FastBreak;
import com.ryha.client.module.world.FastMine;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MultiPlayerGameMode.class)
public abstract class ClientPlayerInteractionManagerMixin {

    @ModifyReturnValue(method = "getReachDistance", at = @At("RETURN"))
    private float modifyReach(float original) {
        if (RyHaClient.getInstance() == null) return original;
        Reach reach = RyHaClient.getInstance().getModuleManager().get(Reach.class);
        if (reach == null || !reach.isEnabled()) return original;
        return (float) reach.attackReach.getValue();
    }

    @ModifyReturnValue(method = "getBlockBreakingSpeed", at = @At("RETURN"))
    private float modifyBreakSpeed(float original) {
        if (RyHaClient.getInstance() == null) return original;
        var mm = RyHaClient.getInstance().getModuleManager();

        FastMine fm = mm.get(FastMine.class);
        if (fm != null && fm.isEnabled()) return Float.MAX_VALUE;

        FastBreak fb = mm.get(FastBreak.class);
        if (fb != null && fb.isEnabled()) return (float)(original * fb.multiplier.getValue());

        return original;
    }
}
