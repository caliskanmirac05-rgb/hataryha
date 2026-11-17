package com.ryha.client.module.movement;

import com.ryha.client.RyHaClient;
import com.ryha.client.event.Subscribe;
import com.ryha.client.event.events.TickEvent;
import com.ryha.client.module.Category;
import com.ryha.client.module.Module;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;

public class Jesus extends Module {

    public Jesus() {
        super("Jesus", "Walk on water and lava", Category.MOVEMENT);
    }

    @Override protected void onEnable()  { RyHaClient.getInstance().getEventBus().registerObject(this); }

    @Subscribe
    public void onTick(TickEvent event) {
        if (mc.player == null || mc.level == null) return;
        if (mc.player.getAbilities().flying) return;

        // Check if standing in fluid
        BlockPos pos = mc.player.blockPosition();
        var block    = mc.level.getBlockState(pos).getBlock();
        boolean inFluid = block == Blocks.WATER || block == Blocks.LAVA;

        if (inFluid) {
            mc.player.setDeltaMovement(mc.player.getDeltaMovement().x, 0.04, mc.player.getDeltaMovement().z);
        }
    }
}
