package com.ryha.client.module.world;

import com.ryha.client.RyHaClient;
import com.ryha.client.event.Subscribe;
import com.ryha.client.event.events.TickEvent;
import com.ryha.client.module.Category;
import com.ryha.client.module.Module;
import com.ryha.client.module.setting.SliderSetting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.state.BlockState;

public class AutoFarm extends Module {

    private final SliderSetting range = registerSetting(new SliderSetting("Range", "Farm range", 4.0, 1.0, 6.0, 0.5));
    private long lastAction = 0;

    public AutoFarm() {
        super("AutoFarm", "Automatically harvests and replants crops", Category.WORLD);
    }

    @Override protected void onEnable()  { RyHaClient.getInstance().getEventBus().registerObject(this); }
    @Override protected void onDisable() { RyHaClient.getInstance().getEventBus().unregisterObject(this); }

    @Subscribe
    public void onTick(TickEvent event) {
        if (mc.player == null || mc.level == null || mc.gameMode == null) return;
        if (System.currentTimeMillis() - lastAction < 100) return;

        BlockPos origin = mc.player.blockPosition();
        int r = (int) range.getValue();

        for (BlockPos bp : BlockPos.betweenClosed(origin.offset(-r, -1, -r), origin.offset(r, 1, r))) {
            BlockState state = mc.level.getBlockState(bp);
            Block block = state.getBlock();

            boolean fullyGrown = false;
            if (block instanceof CropBlock crop)            fullyGrown = crop.isMaxAge(state);
            else if (block instanceof SweetBerryBushBlock)  fullyGrown = state.getValue(SweetBerryBushBlock.AGE) == 3;
            else if (block instanceof CocoaBlock)           fullyGrown = state.getValue(CocoaBlock.AGE) == 2;

            if (fullyGrown) {
                mc.gameMode.startDestroyBlock(bp, Direction.UP);
                lastAction = System.currentTimeMillis();
                return;
            }
        }
    }
}
