package com.ryha.client.module;

import com.ryha.client.RyHaClient;
import com.ryha.client.module.setting.KeybindSetting;
import com.ryha.client.module.setting.Setting;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for every RyHa module.
 *
 * Subclasses:
 *  - declare settings in their constructor via registerSetting()
 *  - override onEnable() / onDisable() for setup/teardown
 *  - register EventBus listeners inside onEnable(), unregister in onDisable()
 */
public abstract class Module {

    protected final Minecraft mc = Minecraft.getInstance();

    private final String   name;
    private final String   description;
    private final Category category;

    private boolean        enabled    = false;
    private boolean        visible    = true;   // show in HUD?

    private final List<Setting<?>> settings = new ArrayList<>();
    protected final KeybindSetting keybind;

    protected Module(String name, String description, Category category, int defaultKey) {
        this.name        = name;
        this.description = description;
        this.category    = category;
        this.keybind     = new KeybindSetting("Keybind", "Toggle keybind", defaultKey);
        settings.add(keybind);
    }

    protected Module(String name, String description, Category category) {
        this(name, description, category, GLFW.GLFW_KEY_UNKNOWN);
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    public final void toggle() {
        if (enabled) disable(); else enable();
    }

    public final void enable() {
        enabled = true;
        onEnable();
        if (RyHaClient.mc().player != null) {
            sendToggleMessage(true);
        }
    }

    public final void disable() {
        enabled = false;
        onDisable();
        if (RyHaClient.mc().player != null) {
            sendToggleMessage(false);
        }
    }

    /** Override for setup (register event listeners here). */
    protected void onEnable()  {}
    /** Override for teardown. */
    protected void onDisable() {}

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void sendToggleMessage(boolean state) {
        // Send chat message only if ToggleMessages module is enabled or not registered yet
        // (bootstrap guard: ModuleManager may not exist yet)
        if (RyHaClient.getInstance() == null) return;
        ModuleManager mm = RyHaClient.getInstance().getModuleManager();
        if (mm == null) return;
        Module tm = mm.getModuleByName("ToggleMessages");
        if (tm != null && !tm.isEnabled()) return;
        if (mc.player == null) return;
        mc.player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
            RyHaClient.PREFIX + name + (state ? " §aenabled" : " §cdisabled")));
    }

    protected <T extends Setting<?>> T registerSetting(T setting) {
        settings.add(setting);
        return setting;
    }

    // ── Accessors ─────────────────────────────────────────────────────────────

    public String          getName()        { return name; }
    public String          getDescription() { return description; }
    public Category        getCategory()    { return category; }
    public boolean         isEnabled()      { return enabled; }
    public boolean         isVisible()      { return visible; }
    public void            setVisible(boolean v) { this.visible = v; }
    public List<Setting<?>>getSettings()    { return settings; }
    public KeybindSetting  getKeybind()     { return keybind; }

    public void setEnabled(boolean e) {
        if (e != enabled) toggle();
    }
}
