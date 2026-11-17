package com.ryha.client.module.world;

import com.ryha.client.RyHaClient;
import com.ryha.client.event.Subscribe;
import com.ryha.client.event.events.TickEvent;
import com.ryha.client.module.Category;
import com.ryha.client.module.Module;
import com.ryha.client.module.setting.SliderSetting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.Optional;

public class Nuker extends Module {

    private final SliderSetting range = registerSetting(new SliderSetting("Range", "Break range", 4.0, 1.0, 6.0, 0.5));

    public Nuker() {
        super("Nuker", "Automatically breaks blocks around the player", Category.WORLD);
    }

    @Override protected void onEnable()  { RyHaClient.getInstance().getEventBus().registerObject(this); }
    @Override protected void onDisable() { RyHaClient.getInstance().getEventBus().unregisterObject(this); }

    @Subscribe
    public void onTick(TickEvent event) {
        LocalPlayer p = mc.player;
        if (p == null || mc.level == null || mc.gameMode == null) return;

        double r = range.getValue();
        BlockPos origin = p.blockPosition();
        int ri = (int) Math.ceil(r);

        Vec3 playerPos = p.position();

        Optional<BlockPos> target = BlockPos.betweenClosed(
                origin.offset(-ri, -ri, -ri), origin.offset(ri, ri, ri))
            .filter(bp -> !mc.level.getBlockState(bp).isAir()
                       && mc.level.getBlockState(bp).getBlock() != Blocks.BEDROCK
                       && playerPos.distanceTo(Vec3.atCenterOf(bp)) <= r)
            .min(Comparator.comparingDouble(bp -> playerPos.distanceTo(Vec3.atCenterOf(bp))));

        target.ifPresent(bp -> mc.gameMode.continueDestroyBlock(bp, Direction.UP));
    }
}
