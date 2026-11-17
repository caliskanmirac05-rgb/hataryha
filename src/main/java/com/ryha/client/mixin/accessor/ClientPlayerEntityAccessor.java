package com.ryha.client.mixin.accessor;

import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LocalPlayer.class)
public interface ClientPlayerEntityAccessor {
    @Accessor("sprintTriggerTime")
    int getSprintTicks();
    @Accessor("sprintTriggerTime")
    void setSprintTicks(int ticks);
}
