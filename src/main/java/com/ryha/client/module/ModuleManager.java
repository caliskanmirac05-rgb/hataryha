package com.ryha.client.module;

import com.ryha.client.module.combat.*;
import com.ryha.client.module.misc.*;
import com.ryha.client.module.movement.*;
import com.ryha.client.module.player.*;
import com.ryha.client.module.render.*;
import com.ryha.client.module.world.*;
import com.ryha.client.module.world.Timer;
import com.ryha.client.util.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleManager {

    private final List<Module> modules = new ArrayList<>();

    public ModuleManager() {
        // ── Combat ────────────────────────────────────────────────────────────
        register(new KillAura());
        register(new AimAssist());
        register(new AutoClicker());
        register(new Reach());
        register(new Criticals());
        register(new Velocity());
        register(new AntiKnockback());
        register(new TriggerBot());
        register(new Hitbox());
        register(new AutoTotem());
        register(new AutoArmor());

        // ── Movement ──────────────────────────────────────────────────────────
        register(new Sprint());
        register(new Fly());
        register(new Speed());
        register(new NoSlow());
        register(new Step());
        register(new Spider());
        register(new Jesus());
        register(new HighJump());
        register(new LongJump());
        register(new SafeWalk());

        // ── Player ────────────────────────────────────────────────────────────
        register(new AutoEat());
        register(new FastPlace());
        register(new FastBreak());
        register(new AutoTool());
        register(new ChestStealer());
        register(new InventoryMove());
        register(new Freecam());

        // ── Render ────────────────────────────────────────────────────────────
        register(new PlayerESP());
        register(new MobESP());
        register(new ItemESP());
        register(new ChestESP());
        register(new Tracers());
        register(new NameTags());
        register(new FullBright());
        register(new XRay());
        register(new HUD());
        register(new Breadcrumbs());

        // ── World ─────────────────────────────────────────────────────────────
        register(new Nuker());
        register(new Scaffold());
        register(new Timer());
        register(new FastMine());
        register(new AutoFarm());

        // ── Misc ──────────────────────────────────────────────────────────────
        register(new AntiAFK());
        register(new DiscordRPC());
        register(new Notifications());
        register(new ToggleMessages());
        register(new Coordinates());
        register(new ServerInfo());

        Logger.info("ModuleManager: registered " + modules.size() + " modules");
    }

    private void register(Module m) { modules.add(m); }

    // ── Query helpers ─────────────────────────────────────────────────────────

    public List<Module> getModules()                        { return Collections.unmodifiableList(modules); }

    public List<Module> getByCategory(Category cat) {
        return modules.stream()
                      .filter(m -> m.getCategory() == cat)
                      .collect(Collectors.toList());
    }

    public List<Module> getEnabled() {
        return modules.stream().filter(Module::isEnabled).collect(Collectors.toList());
    }

    public Module getModuleByName(String name) {
        return modules.stream()
                      .filter(m -> m.getName().equalsIgnoreCase(name))
                      .findFirst().orElse(null);
    }

    @SuppressWarnings("unchecked")
    public <T extends Module> T get(Class<T> clazz) {
        return (T) modules.stream()
                          .filter(m -> m.getClass() == clazz)
                          .findFirst()
                          .orElse(null);
    }

    public boolean isEnabled(Class<? extends Module> clazz) {
        Module m = get(clazz);
        return m != null && m.isEnabled();
    }

    /** Called from KeyboardMixin on each key press to handle toggle keybinds. */
    public void onKeyPress(int key) {
        for (Module m : modules) {
            if (!m.getKeybind().isNone() && m.getKeybind().getValue() == key) {
                m.toggle();
            }
        }
    }
}
