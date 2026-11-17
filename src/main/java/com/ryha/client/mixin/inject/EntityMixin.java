package com.ryha.client.mixin.inject;

import com.ryha.client.RyHaClient;
import com.ryha.client.module.combat.Hitbox;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @ModifyReturnValue(method = "getBoundingBox", at = @At("RETURN"))
    private AABB modifyBoundingBox(AABB original) {
        Entity self = (Entity)(Object) this;
        if (RyHaClient.getInstance() == null) return original;
        // Don't expand own player bounding box
        if (self == net.minecraft.client.Minecraft.getInstance().player) return original;

        Hitbox hb = RyHaClient.getInstance().getModuleManager().get(Hitbox.class);
        if (hb == null || !hb.isEnabled()) return original;

        double exp = hb.expansion.getValue();
        return original.inflate(exp);
    }
}
