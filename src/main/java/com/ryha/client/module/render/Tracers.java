package com.ryha.client.module.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.ryha.client.RyHaClient;
import com.ryha.client.event.Subscribe;
import com.ryha.client.event.events.RenderEvent;
import com.ryha.client.module.Category;
import com.ryha.client.module.Module;
import com.ryha.client.module.setting.BooleanSetting;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.awt.*;

public class Tracers extends Module {

    private final BooleanSetting players = registerSetting(new BooleanSetting("Players", "Tracers to players", true));
    private final BooleanSetting mobs    = registerSetting(new BooleanSetting("Mobs",    "Tracers to mobs",    false));

    public Tracers() {
        super("Tracers", "Draw lines from crosshair to entities", Category.RENDER);
    }

    @Override protected void onEnable()  { RyHaClient.getInstance().getEventBus().registerObject(this); }
    @Override protected void onDisable() { RyHaClient.getInstance().getEventBus().unregisterObject(this); }

    @Subscribe
    public void onRender(RenderEvent event) {
        if (mc.level == null || mc.player == null) return;

        Camera camera = mc.gameRenderer.getMainCamera();
        Vec3 origin   = camera.getPosition();
        var matrices  = event.getMatrices();

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.lineWidth(1.0f);

        matrices.pushPose();
        matrices.translate(-origin.x, -origin.y, -origin.z);

        Tesselator   tess = Tesselator.getInstance();
        BufferBuilder buf  = tess.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        var mat = matrices.last().pose();

        mc.level.entitiesForRendering().forEach(e -> {
            if (!(e instanceof Player p) || p == mc.player) return;
            if (!players.isEnabled()) return;

            int r = 60, g = 150, b = 255, a = 180;
            buf.addVertex(mat, (float) origin.x, (float) origin.y, (float) origin.z).setColor(r, g, b, a);
            buf.addVertex(mat, (float) p.getX(), (float) p.getEyeY(), (float) p.getZ()).setColor(r, g, b, a);
        });

        MeshData mesh = buf.build();
        if (mesh != null) BufferUploader.drawWithShader(mesh);

        matrices.popPose();
        RenderSystem.enableDepthTest();
    }
}
