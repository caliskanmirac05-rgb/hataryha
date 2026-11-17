package com.ryha.client.module.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.ryha.client.RyHaClient;
import com.ryha.client.event.Subscribe;
import com.ryha.client.event.events.RenderEvent;
import com.ryha.client.event.events.TickEvent;
import com.ryha.client.module.Category;
import com.ryha.client.module.Module;
import com.ryha.client.module.setting.SliderSetting;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayDeque;
import java.util.Deque;

public class Breadcrumbs extends Module {

    private final SliderSetting maxPoints = registerSetting(new SliderSetting("MaxPoints", "Max trail length", 200, 10, 1000, 10));
    private final Deque<Vec3>   points    = new ArrayDeque<>();
    private Vec3 lastPos = null;

    public Breadcrumbs() {
        super("Breadcrumbs", "Leaves a glowing trail showing where you've been", Category.RENDER);
    }

    @Override protected void onEnable() {
        points.clear();
        RyHaClient.getInstance().getEventBus().registerObject(this);
    }

    @Override protected void onDisable() {
        points.clear();
        RyHaClient.getInstance().getEventBus().unregisterObject(this);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (mc.player == null) return;
        Vec3 pos = mc.player.position();
        if (lastPos == null || pos.distanceTo(lastPos) > 0.3) {
            points.addLast(pos);
            lastPos = pos;
        }
        while (points.size() > (int) maxPoints.getValue()) points.removeFirst();
    }

    @Subscribe
    public void onRender(RenderEvent event) {
        if (points.size() < 2) return;
        Vec3 cam = mc.gameRenderer.getMainCamera().getPosition();

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.lineWidth(2.0f);

        var matrices = event.getMatrices();
        matrices.pushPose();
        matrices.translate(-cam.x, -cam.y, -cam.z);

        Tesselator    tess = Tesselator.getInstance();
        BufferBuilder buf  = tess.begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        var mat = matrices.last().pose();

        int total = points.size();
        int idx   = 0;
        for (Vec3 p : points) {
            float progress = (float) idx / total;
            int   r = (int) (255 * progress);
            int   b = (int) (255 * (1 - progress));
            buf.addVertex(mat, (float) p.x, (float) p.y + 0.05f, (float) p.z).setColor(r, 80, b, 200);
            idx++;
        }

        MeshData mesh = buf.build();
        if (mesh != null) BufferUploader.drawWithShader(mesh);

        matrices.popPose();
        RenderSystem.enableDepthTest();
    }
}
