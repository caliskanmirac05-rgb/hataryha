package com.ryha.client.module.render;

import com.ryha.client.RyHaClient;
import com.ryha.client.event.Subscribe;
import com.ryha.client.event.events.RenderEvent;
import com.ryha.client.module.Category;
import com.ryha.client.module.Module;
import com.ryha.client.module.setting.BooleanSetting;
import com.ryha.client.util.RenderUtil;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.phys.AABB;

import java.awt.*;

public class MobESP extends Module {

    private final BooleanSetting hostile     = registerSetting(new BooleanSetting("Hostile", "Show hostile mobs", true));
    private final BooleanSetting passive     = registerSetting(new BooleanSetting("Passive", "Show passive mobs", false));
    // 26.2 Chaos Cubed — Sulfur Cubes glow yellow/sulfur-gold for quick identification
    private final BooleanSetting sulfurCubes = registerSetting(new BooleanSetting("SulfurCubes", "Highlight Sulfur Cubes (26.2)", true));

    public MobESP() {
        super("MobESP", "Draw boxes around mobs", Category.RENDER);
    }

    @Override protected void onEnable()  { RyHaClient.getInstance().getEventBus().registerObject(this); }
    @Override protected void onDisable() { RyHaClient.getInstance().getEventBus().unregisterObject(this); }

    private boolean isSulfurCube(net.minecraft.world.entity.Entity e) {
        String name = e.getClass().getSimpleName();
        return name.equals("SulfurCube") || name.equals("SmallSulfurCube");
    }

    @Subscribe
    public void onRender(RenderEvent event) {
        if (mc.level == null) return;

        mc.level.entitiesForRendering().forEach(e -> {
            Color c = null;

            // SulfurCube check first — takes priority over generic Monster colour
            if (sulfurCubes.isEnabled() && isSulfurCube(e)) {
                c = new Color(230, 200, 30, 220); // sulfur-gold
            } else if (hostile.isEnabled() && e instanceof Monster) {
                c = new Color(255, 80, 80, 200);
            } else if (passive.isEnabled() && e instanceof Animal) {
                c = new Color(80, 255, 80, 200);
            }

            if (c == null) return;

            var matrices = event.getMatrices();
            matrices.pushPose();
            RenderUtil.translateToEntity(matrices, e, event.getPartialTick());
            AABB relative = e.getBoundingBox().offset(-e.getX(), -e.getY(), -e.getZ());
            RenderUtil.drawBoundingBox(matrices, relative, c, 1.0f);
            matrices.popPose();
        });
    }
}
