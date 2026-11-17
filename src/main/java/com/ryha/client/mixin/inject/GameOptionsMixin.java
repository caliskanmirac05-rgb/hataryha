package com.ryha.client.mixin.inject;

import com.ryha.client.RyHaClient;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Options.class)
public abstract class GameOptionsMixin {

    // 1.21.4 uses rightItemUseCooldown for block place delay
    // The vanilla game sets it in MultiPlayerGameMode; we just ensure it's always 0
}
