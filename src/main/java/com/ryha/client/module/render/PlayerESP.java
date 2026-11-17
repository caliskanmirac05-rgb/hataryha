package com.ryha.client.module.render;

import com.ryha.client.RyHaClient;
import com.ryha.client.event.Subscribe;
import com.ryha.client.event.events.RenderEvent;
import com.ryha.client.module.Category;
import com.ryha.client.module.Module;
import com.ryha.client.module.setting.BooleanSetting;
import com.ryha.client.module.setting.ModeSetting;
import com.ryha.client.util.RenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.awt.*;

public class PlayerESP extends Module {

    private final ModeSetting    mode      = registerSetting(new ModeSetting("Mode", "ESP mode", "AABB", "AABB", "Outline", "Glow"));
    private final BooleanSetting teamCheck = registerSetting(new BooleanSetting("TeamCheck", "Skip teammates", false));

    public PlayerESP() {
        super("PlayerESP", "Draw boxes/outlines around players", Category.RENDER);
    }

    @Override protected void onEnable()  { RyHaClient.getInstance().getEventBus().registerObject(this); }

    @Subscribe
    public void onRender(RenderEvent event) {
        if (mc.level == null || mc.player == null) return;
        PoseStack matrices = event.getMatrices();

        for (Player player : mc.level.getPlayers()) {
            if (player == mc.player) continue;

            Color color = player.getHealth() < 10 ? new Color(255, 60, 60, 200)
                                                   : new Color(60, 150, 255, 200);

            if (mode.is("AABB")) {
                matrices.pushPose();
                RenderUtil.translateToEntity(matrices, player, event.getPartialTick());
                AABB relative = player.getBoundingBox().offset(-player.getX(), -player.getY(), -player.getZ());
                RenderUtil.drawBoundingBox(matrices, relative, color, 1.5f);
                matrices.popPose();
            }
        }
    }
}
