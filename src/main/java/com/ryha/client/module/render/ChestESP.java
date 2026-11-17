package com.ryha.client.module.render;

import com.ryha.client.RyHaClient;
import com.ryha.client.event.Subscribe;
import com.ryha.client.event.events.RenderEvent;
import com.ryha.client.module.Category;
import com.ryha.client.module.Module;
import com.ryha.client.util.RenderUtil;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.awt.*;

public class ChestESP extends Module {

    public ChestESP() {
        super("ChestESP", "Highlights chests, barrels and shulker boxes", Category.RENDER);
    }

    @Override protected void onEnable()  { RyHaClient.getInstance().getEventBus().registerObject(this); }
    @Override protected void onDisable() { RyHaClient.getInstance().getEventBus().unregisterObject(this); }

    @Subscribe
    public void onRender(RenderEvent event) {
        if (mc.level == null || mc.player == null) return;
        Vec3 cam = mc.gameRenderer.getMainCamera().getPosition();

        mc.level.blockEntityList.forEach(be -> {
            Color c = null;
            if (be instanceof ChestBlockEntity || be instanceof BarrelBlockEntity)
                c = new Color(255, 165, 0, 200);
            else if (be instanceof ShulkerBoxBlockEntity)
                c = new Color(180, 60, 220, 200);
            else if (be instanceof EnderChestBlockEntity)
                c = new Color(100, 50, 200, 200);
            if (c == null) return;

            var pos      = be.getBlockPos();
            var matrices = event.getMatrices();
            matrices.pushPose();
            matrices.translate(pos.getX() - cam.x, pos.getY() - cam.y, pos.getZ() - cam.z);
            RenderUtil.drawBoundingBox(matrices, new AABB(0, 0, 0, 1, 1, 1), c, 1.5f);
            matrices.popPose();
        });
    }
}
