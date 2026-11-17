package com.ryha.client.gui;

import com.ryha.client.RyHaClient;
import com.ryha.client.module.Category;
import com.ryha.client.module.Module;
import com.ryha.client.module.setting.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.events.CharacterEvent;
import net.minecraft.client.gui.screens.events.KeyEvent;
import net.minecraft.client.gui.screens.events.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.*;

/**
 * RyHa ClickGUI — modern glassmorphism panel.
 *
 * Layout:
 *   • Category tabs across the top
 *   • Module list in the centre panel
 *   • Settings sidebar slides in on hover/click
 *   • Search bar at the top
 */
public class ClickGUI extends Screen {

    // ── Dimensions ────────────────────────────────────────────────────────────
    private static final int PANEL_X      = 20;
    private static final int PANEL_Y      = 20;
    private static final int PANEL_W      = 580;
    private static final int PANEL_H      = 380;
    private static final int TAB_H        = 28;
    private static final int MODULE_H     = 22;
    private static final int SETTING_W    = 180;
    private static final int HEADER_H     = 36;
    private static final int SEARCH_H     = 24;

    // ── Colors (ARGB) ─────────────────────────────────────────────────────────
    private static final int BG_DARK      = 0xE0101014;
    private static final int BG_PANEL     = 0xCC161620;
    private static final int BG_HEADER    = 0xE0100A20;
    private static final int ACCENT       = 0xFF7B2FBE;
    private static final int ACCENT_LIGHT = 0xFF9B4FDE;
    private static final int TEXT_PRIMARY = 0xFFEEEEEE;
    private static final int TEXT_DIM     = 0xFF888899;
    private static final int ENABLED_BG   = 0x3307C3FF;
    private static final int HOVER_BG     = 0x1AFFFFFF;
    private static final int BORDER       = 0x50FFFFFF;

    // ── State ─────────────────────────────────────────────────────────────────
    private Category      selectedCategory = Category.COMBAT;
    private Module        hoveredModule    = null;
    private Module        selectedModule   = null;  // settings open
    private String        searchQuery      = "";
    private boolean       searchFocused    = false;
    private long          openTime         = 0;

    // drag
    private boolean dragging   = false;
    private int     dragOffX   = 0, dragOffY = 0;
    private int     panelX     = PANEL_X, panelY = PANEL_Y;

    // animation per module: 0.0 → 1.0
    private final Map<Module, Float> enabledAnim = new HashMap<>();

    public ClickGUI() {
        super(Component.literal("RyHa ClickGUI"));
    }

    @Override
    public boolean shouldPause() { return false; }

    @Override
    public void init() {
        openTime = System.currentTimeMillis();
    }

    // ── Rendering ─────────────────────────────────────────────────────────────

    @Override
    public void extractRenderState(GuiGraphicsExtractor ctx, int mx, int my, float delta) {
        // tick animations
        tickAnimations();

        int pw = PANEL_W + (selectedModule != null ? SETTING_W : 0);

        // Blurred background tint
        ctx.fill(0, 0, width, height, 0x80000000);

        // ── Main panel ────────────────────────────────────────────────────────
        // Drop shadow
        for (int i = 4; i >= 1; i--) {
            ctx.fill(panelX - i, panelY - i, panelX + pw + i, panelY + PANEL_H + i,
                     0x05000000 * (5 - i));
        }

        // Panel background
        fillRounded(ctx, panelX, panelY, pw, PANEL_H, BG_PANEL);
        // Border glow
        drawRoundedBorder(ctx, panelX, panelY, pw, PANEL_H, BORDER);

        // ── Header ────────────────────────────────────────────────────────────
        fillRounded(ctx, panelX, panelY, pw, HEADER_H, BG_HEADER);
        ctx.text(font, "✦ RyHa Client", panelX + 10, panelY + 11, ACCENT_LIGHT, true);
        // Version tag
        ctx.text(font, "v1.0", panelX + pw - 28, panelY + 11, TEXT_DIM, false);

        // ── Search bar ────────────────────────────────────────────────────────
        int sY = panelY + HEADER_H;
        ctx.fill(panelX, sY, panelX + pw - (selectedModule != null ? SETTING_W : 0), sY + SEARCH_H, 0xCC0D0D18);
        ctx.text(font,
            searchQuery.isEmpty() && !searchFocused ? "§7Search modules..." : "§f" + searchQuery + (searchFocused ? "§7|" : ""),
            panelX + 10, sY + 7, TEXT_PRIMARY, false);

        // ── Category tabs ─────────────────────────────────────────────────────
        int tabsY  = sY + SEARCH_H;
        int tabW   = (pw - (selectedModule != null ? SETTING_W : 0)) / Category.values().length;
        for (int i = 0; i < Category.values().length; i++) {
            Category cat  = Category.values()[i];
            int      tx   = panelX + i * tabW;
            boolean  sel  = cat == selectedCategory;
            int      bgC  = sel ? ACCENT : 0x00000000;
            ctx.fill(tx, tabsY, tx + tabW, tabsY + TAB_H, bgC);
            if (sel) ctx.fill(tx, tabsY + TAB_H - 2, tx + tabW, tabsY + TAB_H, ACCENT_LIGHT);
            ctx.centeredText(font,
                cat.getIcon() + " " + cat.getName(),
                tx + tabW / 2, tabsY + 9,
                sel ? TEXT_PRIMARY : TEXT_DIM);
        }

        // ── Module list ───────────────────────────────────────────────────────
        int listY = tabsY + TAB_H;
        int listH = PANEL_H - HEADER_H - SEARCH_H - TAB_H;
        ctx.enableScissor(panelX, listY, panelX + pw - (selectedModule != null ? SETTING_W : 0), listY + listH);

        List<Module> visible = getVisibleModules();
        hoveredModule = null;
        for (int i = 0; i < visible.size(); i++) {
            Module m  = visible.get(i);
            int    mY = listY + i * MODULE_H;
            if (mY + MODULE_H > listY + listH) break;

            boolean hovered = mx >= panelX && mx <= panelX + pw - (selectedModule != null ? SETTING_W : 0)
                           && my >= mY && my <= mY + MODULE_H;
            if (hovered) hoveredModule = m;

            float anim = enabledAnim.getOrDefault(m, 0f);

            if (m.isEnabled()) ctx.fill(panelX, mY, panelX + pw - (selectedModule != null ? SETTING_W : 0), mY + MODULE_H, ENABLED_BG);
            if (hovered)        ctx.fill(panelX, mY, panelX + pw - (selectedModule != null ? SETTING_W : 0), mY + MODULE_H, HOVER_BG);

            // Animated enabled stripe on left
            if (anim > 0) {
                int stripeH = (int)(MODULE_H * anim);
                ctx.fill(panelX, mY + (MODULE_H - stripeH) / 2, panelX + 3, mY + (MODULE_H + stripeH) / 2, ACCENT_LIGHT);
            }

            // Module name
            int nameColor = m.isEnabled() ? TEXT_PRIMARY : TEXT_DIM;
            ctx.text(font, m.getName(), panelX + 12, mY + 7, nameColor, true);

            // Settings gear indicator
            if (!m.getSettings().isEmpty()) {
                ctx.text(font, "⚙", panelX + pw - (selectedModule != null ? SETTING_W : 0) - 15, mY + 7, TEXT_DIM, false);
            }

            // Separator line
            ctx.fill(panelX + 8, mY + MODULE_H - 1, panelX + pw - (selectedModule != null ? SETTING_W : 0) - 8, mY + MODULE_H, 0x15FFFFFF);
        }
        ctx.disableScissor();

        // ── Settings panel ────────────────────────────────────────────────────
        if (selectedModule != null) {
            int sx = panelX + pw - SETTING_W;
            drawSettingsPanel(ctx, selectedModule, sx, panelY, SETTING_W, PANEL_H, mx, my);
        }

        super.extractRenderState(ctx, mx, my, delta);
    }

    private void drawSettingsPanel(GuiGraphicsExtractor ctx, Module module, int x, int y, int w, int h, int mx, int my) {
        ctx.fill(x, y, x + w, y + h, 0xE0100A18);
        drawRoundedBorder(ctx, x, y, w, h, 0x40FFFFFF);

        // Header
        ctx.fill(x, y, x + w, y + HEADER_H, 0xE0120B22);
        ctx.text(font, "§b" + module.getName(), x + 8, y + 11, TEXT_PRIMARY, true);
        // Close button
        ctx.text(font, "§c✕", x + w - 14, y + 11, TEXT_DIM, false);

        int settingY = y + HEADER_H + 4;
        for (Setting<?> s : module.getSettings()) {
            if (settingY + 20 > y + h) break;

            ctx.text(font, s.getName(), x + 8, settingY, TEXT_DIM, false);

            if (s instanceof BooleanSetting bs) {
                boolean on  = bs.isEnabled();
                int toggleX = x + w - 32;
                ctx.fill(toggleX, settingY - 1, toggleX + 24, settingY + 11, on ? 0xFF1E7A3A : 0xFF333344);
                ctx.fill(on ? toggleX + 13 : toggleX + 1, settingY + 1, on ? toggleX + 23 : toggleX + 11, settingY + 9,
                         on ? 0xFF44DD66 : 0xFF888899);
            } else if (s instanceof SliderSetting sl) {
                double pct  = (sl.getValue() - sl.getMin()) / (sl.getMax() - sl.getMin());
                int barW    = w - 20;
                ctx.fill(x + 8, settingY + 10, x + 8 + barW, settingY + 15, 0xFF222233);
                ctx.fill(x + 8, settingY + 10, x + 8 + (int)(barW * pct), settingY + 15, ACCENT);
                ctx.text(font, String.format("%.1f", sl.getValue()),
                    x + w - 28, settingY + 10, TEXT_DIM, false);
            } else if (s instanceof ModeSetting ms) {
                ctx.text(font, "< " + ms.getValue() + " >",
                    x + w - 60, settingY, ACCENT_LIGHT, false);
            } else if (s instanceof KeybindSetting ks) {
                ctx.text(font, "[" + ks.getKeyName() + "]",
                    x + w - 50, settingY, TEXT_DIM, false);
            }

            settingY += 22;
            ctx.fill(x + 4, settingY - 2, x + w - 4, settingY - 1, 0x10FFFFFF);
        }
    }

    // ── Mouse interaction ─────────────────────────────────────────────────────

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mx = event.x(), my = event.y();
        int button = event.button();
        int imx = (int) mx, imy = (int) my;

        // drag
        if (imx >= panelX && imx <= panelX + 150 && imy >= panelY && imy <= panelY + HEADER_H) {
            dragging = true;
            dragOffX = imx - panelX;
            dragOffY = imy - panelY;
            return true;
        }

        // settings close button
        if (selectedModule != null) {
            int sx = panelX + PANEL_W;
            if (imx >= sx + SETTING_W - 18 && imx <= sx + SETTING_W - 6
             && imy >= panelY + 7 && imy <= panelY + HEADER_H - 7) {
                selectedModule = null;
                return true;
            }
        }

        // search bar
        int sY = panelY + HEADER_H;
        int pw = PANEL_W + (selectedModule != null ? SETTING_W : 0);
        if (imx >= panelX && imx <= panelX + pw - (selectedModule != null ? SETTING_W : 0)
         && imy >= sY && imy <= sY + SEARCH_H) {
            searchFocused = true;
            return true;
        } else {
            searchFocused = false;
        }

        // category tabs
        int tabsY = sY + SEARCH_H;
        if (imy >= tabsY && imy <= tabsY + TAB_H) {
            int tabW = PANEL_W / Category.values().length;
            int idx  = (imx - panelX) / tabW;
            if (idx >= 0 && idx < Category.values().length) {
                selectedCategory = Category.values()[idx];
                selectedModule   = null;
                return true;
            }
        }

        // module list
        int listY = tabsY + TAB_H;
        List<Module> vis = getVisibleModules();
        for (int i = 0; i < vis.size(); i++) {
            Module m  = vis.get(i);
            int    mY = listY + i * MODULE_H;
            if (imx >= panelX && imx <= panelX + PANEL_W && imy >= mY && imy <= mY + MODULE_H) {
                if (button == 0) {
                    m.toggle();
                } else if (button == 1) {
                    selectedModule = (selectedModule == m) ? null : m;
                }
                return true;
            }
        }

        // settings panel interaction
        if (selectedModule != null) {
            int sx = panelX + PANEL_W;
            handleSettingsClick(selectedModule, sx, panelY, imx, imy, button);
        }

        return super.mouseClicked(event, doubleClick);
    }

    private void handleSettingsClick(Module module, int x, int y, int mx, int my, int button) {
        int settingY = y + HEADER_H + 4;
        for (Setting<?> s : module.getSettings()) {
            if (s instanceof BooleanSetting bs) {
                int toggleX = x + SETTING_W - 32;
                if (mx >= toggleX && mx <= toggleX + 24 && my >= settingY - 1 && my <= settingY + 11) {
                    bs.toggle();
                }
            } else if (s instanceof ModeSetting ms) {
                if (mx >= x + SETTING_W - 65 && my >= settingY - 2 && my <= settingY + 12) {
                    ms.next();
                }
            }
            settingY += 22;
        }
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        if (dragging) {
            panelX = (int) event.x() - dragOffX;
            panelY = (int) event.y() - dragOffY;
            return true;
        }
        return super.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        dragging = false;
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double hAmount, double vAmount) {
        // Settings panel slider
        if (selectedModule != null) {
            int sx = panelX + PANEL_W;
            if (mx >= sx && mx <= sx + SETTING_W) {
                int settingY = panelY + HEADER_H + 4;
                for (Setting<?> s : selectedModule.getSettings()) {
                    if (s instanceof SliderSetting sl
                     && my >= settingY && my <= settingY + 20) {
                        sl.setValue(sl.getValue() + vAmount * sl.getStep());
                        return true;
                    }
                    settingY += 22;
                }
            }
        }
        return super.mouseScrolled(mx, my, hAmount, vAmount);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        int keyCode = event.keyCode();
        if (keyCode == 256) { // ESC
            Minecraft.getInstance().gui.setScreen(null);
            return true;
        }
        if (searchFocused) {
            if (keyCode == 259 && !searchQuery.isEmpty()) { // Backspace
                searchQuery = searchQuery.substring(0, searchQuery.length() - 1);
            }
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        if (searchFocused) {
            searchQuery += event.character();
            return true;
        }
        return super.charTyped(event);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private List<Module> getVisibleModules() {
        return RyHaClient.getInstance().getModuleManager().getByCategory(selectedCategory)
            .stream()
            .filter(m -> searchQuery.isEmpty()
                      || m.getName().toLowerCase().contains(searchQuery.toLowerCase())
                      || m.getDescription().toLowerCase().contains(searchQuery.toLowerCase()))
            .toList();
    }

    private void tickAnimations() {
        for (Module m : RyHaClient.getInstance().getModuleManager().getModules()) {
            float current = enabledAnim.getOrDefault(m, 0f);
            float target  = m.isEnabled() ? 1f : 0f;
            enabledAnim.put(m, current + (target - current) * 0.2f);
        }
    }

    // ── Primitive drawing helpers ─────────────────────────────────────────────

    private void fillRounded(GuiGraphicsExtractor ctx, int x, int y, int w, int h, int color) {
        // Approximate rounded rect with slightly inset corners
        ctx.fill(x + 3, y, x + w - 3, y + h, color);
        ctx.fill(x, y + 3, x + 3, y + h - 3, color);
        ctx.fill(x + w - 3, y + 3, x + w, y + h - 3, color);
    }

    private void drawRoundedBorder(GuiGraphicsExtractor ctx, int x, int y, int w, int h, int color) {
        ctx.fill(x + 2, y, x + w - 2, y + 1, color);         // top
        ctx.fill(x + 2, y + h - 1, x + w - 2, y + h, color); // bottom
        ctx.fill(x, y + 2, x + 1, y + h - 2, color);         // left
        ctx.fill(x + w - 1, y + 2, x + w, y + h - 2, color); // right
    }
}
