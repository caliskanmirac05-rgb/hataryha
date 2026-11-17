package com.ryha.client.module.world;

import com.ryha.client.RyHaClient;
import com.ryha.client.event.Subscribe;
import com.ryha.client.event.events.TickEvent;
import com.ryha.client.module.Category;
import com.ryha.client.module.Module;
import com.ryha.client.module.setting.BooleanSetting;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public class Scaffold extends Module {

    private final BooleanSetting tower = registerSetting(new BooleanSetting("Tower", "Tower up when jumping", true));
    private final BooleanSetting safe  = registerSetting(new BooleanSetting("SafeWalk", "Enable safe walk while scaffolding", true));

    public Scaffold() {
        super("Scaffold", "Automatically places blocks beneath your feet", Category.WORLD);
    }

    @Override protected void onEnable()  { RyHaClient.getInstance().getEventBus().registerObject(this); }

    @Subscribe
    public void onTick(TickEvent event) {
        if (mc.player == null || mc.level == null || mc.gameMode == null) return;

        BlockPos below = mc.player.blockPosition().below();
        if (!mc.level.getBlockState(below).isAir()) return; // already solid

        // Find a block item in hotbar
        var inv = mc.player.getInventory();
        int slot = -1;
        for (int i = 0; i < 9; i++) {
            if (inv.getItem(i).getItem() instanceof BlockItem) {
                slot = i;
                break;
            }
        }
        if (slot < 0) return;

        int prev = inv.selected;
        inv.setSelectedHotbarSlot(slot);

        // Place against the bottom face of the air block (i.e. from below looking up)
        BlockPos placeOn = below.below(); // block to place against
        BlockState target = mc.level.getBlockState(placeOn);
        if (!target.isAir()) {
            BlockHitResult hit = new BlockHitResult(
                new Vec3(below.getX() + 0.5, below.getY(), below.getZ() + 0.5),
                Direction.UP, placeOn, false);
            mc.gameMode.interactBlock(mc.player, InteractionHand.MAIN_HAND, hit);
        }

        inv.setSelectedHotbarSlot(prev);

        // Tower mode: extra upward velocity while holding jump
        if (tower.isEnabled() && mc.options.keyJump.isDown() && mc.player.onGround()) {
            mc.player.setDeltaMovement(mc.player.getDeltaMovement().x, 0.42, mc.player.getDeltaMovement().z);
        }
    }
}
