package com.ryha.client.config;

import com.ryha.client.RyHaClient;
import com.ryha.client.module.Module;
import com.ryha.client.module.setting.Setting;
import com.ryha.client.util.Logger;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.*;
import java.util.Properties;

/**
 * Saves/loads module states and settings to a .properties file in .minecraft/ryha/.
 */
public class ConfigManager {

    private final Path configDir;
    private final Path configFile;

    public ConfigManager() {
        configDir  = FabricLoader.getInstance().getGameDir().resolve("ryha");
        configFile = configDir.resolve("config.properties");
    }

    public void save() {
        try {
            Files.createDirectories(configDir);
            Properties props = new Properties();
            for (Module m : RyHaClient.getInstance().getModuleManager().getModules()) {
                props.setProperty(m.getName() + ".enabled", String.valueOf(m.isEnabled()));
                props.setProperty(m.getName() + ".visible",  String.valueOf(m.isVisible()));
                for (Setting<?> s : m.getSettings()) {
                    props.setProperty(m.getName() + "." + s.getName(), s.serialise());
                }
            }
            try (OutputStream out = Files.newOutputStream(configFile)) {
                props.store(out, "RyHa Client Configuration");
            }
            Logger.info("Config saved.");
        } catch (IOException e) {
            Logger.error("Failed to save config", e);
        }
    }

    public void load() {
        if (!Files.exists(configFile)) return;
        try (InputStream in = Files.newInputStream(configFile)) {
            Properties props = new Properties();
            props.load(in);
            for (Module m : RyHaClient.getInstance().getModuleManager().getModules()) {
                String enabled = props.getProperty(m.getName() + ".enabled");
                if ("true".equals(enabled) && !m.isEnabled()) m.toggle();
                String visible = props.getProperty(m.getName() + ".visible");
                if (visible != null) m.setVisible(Boolean.parseBoolean(visible));

                for (Setting<?> s : m.getSettings()) {
                    String val = props.getProperty(m.getName() + "." + s.getName());
                    if (val != null) {
                        try { s.deserialise(val); }
                        catch (Exception ex) { Logger.warn("Could not load setting " + s.getName()); }
                    }
                }
            }
            Logger.info("Config loaded.");
        } catch (IOException e) {
            Logger.error("Failed to load config", e);
        }
    }
}
