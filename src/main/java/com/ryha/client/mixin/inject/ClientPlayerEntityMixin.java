package com.ryha.client.mixin.inject;

import com.ryha.client.RyHaClient;
import com.ryha.client.event.events.MoveEvent;
import com.ryha.client.module.movement.NoSlow;
import com.ryha.client.module.movement.SafeWalk;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class ClientPlayerEntityMixin {

    /** Post MoveEvent on each movement. */
    @Inject(method = "move", at = @At("HEAD"))
    private void onMove(MoverType type, Vec3 movement, CallbackInfo ci) {
        if (RyHaClient.getInstance() == null) return;
        RyHaClient.getInstance().getEventBus().post(
            new MoveEvent(type, movement.x, movement.y, movement.z));
    }

    @Redirect(
        method = "aiStep",
        at = @At(value = "INVOKE",
                 target = "Lnet/minecraft/client/player/LocalPlayer;isShiftKeyDown()Z",
                 ordinal = 0))
    private boolean safewalkIsSneaking(LocalPlayer instance) {
        if (RyHaClient.getInstance() != null
         && RyHaClient.getInstance().getModuleManager().isEnabled(SafeWalk.class)) {
            return true;
        }
        return instance.isShiftKeyDown();
    }
}
