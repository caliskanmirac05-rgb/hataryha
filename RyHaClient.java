package com.ryha.client;

import com.ryha.client.config.ConfigManager;
import com.ryha.client.event.EventBus;
import com.ryha.client.gui.ClickGUI;
import com.ryha.client.gui.hud.HUDRenderer;
import com.ryha.client.module.ModuleManager;
import com.ryha.client.util.Logger;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

/**
 * RyHa Client — Main entrypoint
 * Initializes all subsystems in order.
 */
public class RyHaClient implements ClientModInitializer {

    public static final String NAME    = "RyHa Client";
    public static final String VERSION = "1.0.0";
    public static final String PREFIX  = "§b[§3RyHa§b]§r ";

    private static RyHaClient INSTANCE;

    private EventBus      eventBus;
    private ModuleManager moduleManager;
    private ClickGUI      clickGUI;
    private ConfigManager configManager;

    @Override
    public void onInitializeClient() {
        INSTANCE = this;
        Logger.info("Initializing " + NAME + " v" + VERSION);

        this.eventBus      = new EventBus();
        this.moduleManager = new ModuleManager();
        this.clickGUI      = new ClickGUI();
        this.configManager = new ConfigManager();

        // Register HUD renderer via Fabric API (replaces InGameHudMixin — Gui.render signature changed in 26.2)
        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                ResourceLocation.fromNamespaceAndPath("ryha", "hud"),
                (gfx, dt) -> HUDRenderer.render(gfx, dt.getGameTimeDeltaPartialTick(false))
        );

        // Register tick event to dispatch per-tick module updates
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            eventBus.post(new com.ryha.client.event.events.TickEvent(client));
        });

        // Load saved config last
        configManager.load();

        Logger.info(NAME + " ready. " + moduleManager.getModules().size() + " modules loaded.");
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public static RyHaClient getInstance()   { return INSTANCE; }
    public EventBus      getEventBus()       { return eventBus; }
    public ModuleManager getModuleManager()  { return moduleManager; }
    public ClickGUI      getClickGUI()       { return clickGUI; }
    public ConfigManager getConfigManager()  { return configManager; }

    public static Minecraft mc() { return Minecraft.getInstance(); }
}
