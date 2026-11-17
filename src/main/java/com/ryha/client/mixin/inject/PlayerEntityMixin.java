package com.ryha.client.mixin.inject;

import com.ryha.client.RyHaClient;
import com.ryha.client.event.events.AttackEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerEntityMixin {

    @Inject(method = "attack", at = @At("HEAD"))
    private void onAttack(Entity target, CallbackInfo ci) {
        if (RyHaClient.getInstance() == null) return;
        RyHaClient.getInstance().getEventBus().post(new AttackEvent(target));
    }
}
