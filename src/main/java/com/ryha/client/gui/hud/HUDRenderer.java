package com.ryha.client.gui.hud;

import com.ryha.client.RyHaClient;
import com.ryha.client.module.Module;
import com.ryha.client.module.render.HUD;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.multiplayer.PlayerInfo;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Renders the RyHa in-game HUD:
 *  • Top-right: active module list (colour-shifting)
 *  • Top-left: watermark
 *  • Bottom-left: stats (FPS, CPS, Ping, Coords, Speed)
 *
 * Called from InGameHudMixin after vanilla HUD render.
 */
public class HUDRenderer {

    private static final Minecraft mc = Minecraft.getInstance();

    // CPS tracking
    private static final Deque<Long> leftClicks  = new ArrayDeque<>();
    private static final Deque<Long> rightClicks = new ArrayDeque<>();

    public static void registerClick(boolean right) {
        (right ? rightClicks : leftClicks).addLast(System.currentTimeMillis());
    }

    private static int getCPS(Deque<Long> q) {
        long now = System.currentTimeMillis();
        q.removeIf(t -> now - t > 1000);
        return q.size();
    }

    // ── Main render entry ─────────────────────────────────────────────────────

    public static void render(GuiGraphicsExtractor ctx, float delta) {
        HUD hudModule = RyHaClient.getInstance().getModuleManager().get(HUD.class);
        if (hudModule == null || !hudModule.isEnabled()) return;

        int sw = mc.getWindow().getGuiScaledWidth();
        int sh = mc.getWindow().getGuiScaledHeight();

        renderWatermark(ctx);
        renderModuleList(ctx, hudModule, sw);
        renderStats(ctx, hudModule, sw, sh);
    }

    // ── Watermark ─────────────────────────────────────────────────────────────

    private static void renderWatermark(GuiGraphicsExtractor ctx) {
        long   t   = System.currentTimeMillis();
        int    rgb = hsvToRgb((t / 20f) % 360f, 0.8f, 1.0f);
        String txt = "RyHa §fClient";
        ctx.text(mc.font, "§b" + txt, 4, 4, rgb, true);
        ctx.text(mc.font, "§7v1.0", 4, 14, 0xFFAAAAAA, true);
    }

    // ── Module list (top-right, sorted by name length) ────────────────────────

    private static void renderModuleList(GuiGraphicsExtractor ctx, HUD hud, int sw) {
        if (!hud.showModList.isEnabled()) return;

        List<Module> enabled = RyHaClient.getInstance().getModuleManager().getEnabled()
            .stream()
            .filter(Module::isVisible)
            .sorted(Comparator.comparingInt(m -> -mc.font.width(m.getName())))
            .toList();

        int y = 2;
        long t = System.currentTimeMillis();
        for (int i = 0; i < enabled.size(); i++) {
            Module m   = enabled.get(i);
            String name= m.getName();
            int    w   = mc.font.width(name);
            int    x   = sw - w - 4;

            int col;
            if ("RGB".equals(hud.colorMode.getValue())) {
                col = hsvToRgb((t / 15f + i * 25f) % 360f, 0.8f, 1.0f);
            } else if ("Gradient".equals(hud.colorMode.getValue())) {
                float pct = enabled.isEmpty() ? 0 : (float) i / enabled.size();
                col = lerpColor(0xFF7B2FBE, 0xFF2FBEFF, pct);
            } else {
                col = 0xFFC07AFF;
            }

            // shadow rect
            ctx.fill(x - 2, y - 1, sw, y + 10, 0x60000000);
            // accent line
            ctx.fill(sw - 2, y - 1, sw, y + 10, col);
            ctx.text(mc.font, name, x, y, col, true);
            y += 11;
        }
    }

    // ── Stats ─────────────────────────────────────────────────────────────────

    private static void renderStats(GuiGraphicsExtractor ctx, HUD hud, int sw, int sh) {
        LocalPlayer p = mc.player;
        if (p == null) return;

        int  x = 4, y = sh - 60;
        int  bg = 0x70000000;
        int  fg = 0xFFDDDDDD;
        int  ac = 0xFF7B2FBE;

        ctx.fill(x - 2, y - 2, x + 130, y + 56, 0x50000000);

        if (hud.showFPS.isEnabled()) {
            drawStatLine(ctx, x, y, "FPS", String.valueOf(Minecraft.fps), ac, fg);
            y += 10;
        }
        if (hud.showCPS.isEnabled()) {
            drawStatLine(ctx, x, y, "CPS", getCPS(leftClicks) + "L / " + getCPS(rightClicks) + "R", ac, fg);
            y += 10;
        }
        if (hud.showPing.isEnabled()) {
            int ping = getPing();
            int pingColor = ping < 80 ? 0xFF44DD66 : ping < 150 ? 0xFFDDDD44 : 0xFFDD4444;
            drawStatLine(ctx, x, y, "Ping", ping + "ms", pingColor, fg);
            y += 10;
        }
        if (hud.showCoords.isEnabled()) {
            drawStatLine(ctx, x, y, "XYZ",
                (int)p.getX() + " / " + (int)p.getY() + " / " + (int)p.getZ(), ac, fg);
            y += 10;
        }
        if (hud.showSpeed.isEnabled()) {
            double speed = Math.sqrt(p.getDeltaMovement().x * p.getDeltaMovement().x
                                   + p.getDeltaMovement().z * p.getDeltaMovement().z);
            drawStatLine(ctx, x, y, "Speed", String.format("%.2f b/s", speed * 20), ac, fg);
            y += 10;
        }
        if (hud.showTime.isEnabled()) {
            drawStatLine(ctx, x, y, "Time",
                LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")), ac, fg);
        }
    }

    private static void drawStatLine(GuiGraphicsExtractor ctx, int x, int y, String label, String value, int labelColor, int valueColor) {
        ctx.text(mc.font, label + "§r: ", x, y, labelColor, true);
        ctx.text(mc.font, value, x + mc.font.width(label + ": "), y, valueColor, true);
    }

    private static int getPing() {
        if (mc.player == null || mc.getConnection() == null) return 0;
        PlayerInfo entry = mc.getConnection().getPlayerInfo(mc.player.getUUID());
        return entry != null ? entry.getLatency() : 0;
    }

    // ── Color math ────────────────────────────────────────────────────────────

    static int hsvToRgb(float h, float s, float v) {
        int i  = (int)(h / 60) % 6;
        float f = h / 60 - (int)(h / 60);
        float p = v * (1 - s), q = v * (1 - f * s), t = v * (1 - (1 - f) * s);
        float r, g, b;
        switch (i) {
            case 0 -> { r = v; g = t; b = p; }
            case 1 -> { r = q; g = v; b = p; }
            case 2 -> { r = p; g = v; b = t; }
            case 3 -> { r = p; g = q; b = v; }
            case 4 -> { r = t; g = p; b = v; }
            default-> { r = v; g = p; b = q; }
        }
        return 0xFF000000 | ((int)(r*255) << 16) | ((int)(g*255) << 8) | (int)(b*255);
    }

    static int lerpColor(int a, int b, float t) {
        int ar = (a >> 16) & 0xFF, ag = (a >> 8) & 0xFF, ab = a & 0xFF;
        int br = (b >> 16) & 0xFF, bg = (b >> 8) & 0xFF, bb = b & 0xFF;
        int r  = (int)(ar + (br - ar) * t);
        int g  = (int)(ag + (bg - ag) * t);
        int bv = (int)(ab + (bb - ab) * t);
        return 0xFF000000 | (r << 16) | (g << 8) | bv;
    }
}
