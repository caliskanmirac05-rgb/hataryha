package com.ryha.client.module.render;

import com.ryha.client.RyHaClient;
import com.ryha.client.event.Subscribe;
import com.ryha.client.event.events.RenderEvent;
import com.ryha.client.module.Category;
import com.ryha.client.module.Module;
import com.ryha.client.util.RenderUtil;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;

import java.awt.*;

public class ItemESP extends Module {
    public ItemESP() {
        super("ItemESP", "Highlight dropped items on the ground", Category.RENDER);
    }

    @Override protected void onEnable() { RyHaClient.getInstance().getEventBus().registerObject(this); }

    @Subscribe
    public void onRender(RenderEvent event) {
        if (mc.level == null) return;
        Color c = new Color(255, 215, 0, 200);
        mc.level.entitiesForRendering().stream()
            .filter(e -> e instanceof ItemEntity)
            .forEach(e -> {
                var m = event.getMatrices();
                m.pushPose();
                RenderUtil.translateToEntity(m, e, event.getPartialTick());
                AABB rel = e.getBoundingBox().offset(-e.getX(), -e.getY(), -e.getZ());
                RenderUtil.drawBoundingBox(m, rel, c, 1.0f);
                m.popPose();
            });
    }
}
