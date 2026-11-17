package com.ryha.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.awt.*;

/**
 * Utility class for 3D ESP rendering — updated for MC 26.x / Blaze3D modern API.
 */
public final class RenderUtil {

    private RenderUtil() {}

    private static final Minecraft mc = Minecraft.getInstance();

    /**
     * Draws a 3D axis-aligned bounding box outline around the given box.
     */
    public static void drawBoundingBox(PoseStack matrices, AABB box, Color color, float lineWidth) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.lineWidth(lineWidth);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        matrices.pushPose();

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer  = tesselator.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f matrix       = matrices.last().pose();

        int r = color.getRed(), g = color.getGreen(), b = color.getBlue(), a = color.getAlpha();
        drawBoxEdges(buffer, matrix, box, r, g, b, a);

        MeshData mesh = buffer.build();
        if (mesh != null) {
            BufferUploader.drawWithShader(mesh);
        }

        matrices.popPose();
        RenderSystem.enableDepthTest();
    }

    private static void drawBoxEdges(BufferBuilder buf, Matrix4f mat, AABB box, int r, int g, int b, int a) {
        float x1 = (float) box.minX, y1 = (float) box.minY, z1 = (float) box.minZ;
        float x2 = (float) box.maxX, y2 = (float) box.maxY, z2 = (float) box.maxZ;

        // Bottom
        line(buf, mat, x1, y1, z1, x2, y1, z1, r, g, b, a);
        line(buf, mat, x2, y1, z1, x2, y1, z2, r, g, b, a);
        line(buf, mat, x2, y1, z2, x1, y1, z2, r, g, b, a);
        line(buf, mat, x1, y1, z2, x1, y1, z1, r, g, b, a);
        // Top
        line(buf, mat, x1, y2, z1, x2, y2, z1, r, g, b, a);
        line(buf, mat, x2, y2, z1, x2, y2, z2, r, g, b, a);
        line(buf, mat, x2, y2, z2, x1, y2, z2, r, g, b, a);
        line(buf, mat, x1, y2, z2, x1, y2, z1, r, g, b, a);
        // Verticals
        line(buf, mat, x1, y1, z1, x1, y2, z1, r, g, b, a);
        line(buf, mat, x2, y1, z1, x2, y2, z1, r, g, b, a);
        line(buf, mat, x2, y1, z2, x2, y2, z2, r, g, b, a);
        line(buf, mat, x1, y1, z2, x1, y2, z2, r, g, b, a);
    }

    private static void line(BufferBuilder b, Matrix4f m,
                              float x1, float y1, float z1,
                              float x2, float y2, float z2,
                              int r, int g, int bl, int a) {
        b.addVertex(m, x1, y1, z1).setColor(r, g, bl, a);
        b.addVertex(m, x2, y2, z2).setColor(r, g, bl, a);
    }

    /** Translate the matrix stack to track an entity offset by camera. */
    public static void translateToEntity(PoseStack matrices, Entity entity, float delta) {
        Vec3 cam = mc.getEntityRenderDispatcher().camera.getPosition();
        double x = entity.xo + (entity.getX() - entity.xo) * delta - cam.x;
        double y = entity.yo + (entity.getY() - entity.yo) * delta - cam.y;
        double z = entity.zo + (entity.getZ() - entity.zo) * delta - cam.z;
        matrices.translate(x, y, z);
    }
}
