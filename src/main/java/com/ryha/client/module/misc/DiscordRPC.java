package com.ryha.client.module.misc;

import com.ryha.client.module.Category;
import com.ryha.client.module.Module;
import com.ryha.client.module.setting.BooleanSetting;

/**
 * Discord Rich Presence integration.
 * Uses a simple IPC socket to communicate with the Discord client.
 * Full implementation requires the discord-rpc or discord4j library.
 */
public class DiscordRPC extends Module {

    public final BooleanSetting showServer = registerSetting(new BooleanSetting("ShowServer", "Show current server in RPC", true));
    public final BooleanSetting showMods   = registerSetting(new BooleanSetting("ShowMods",   "Show active module count",  false));

    public DiscordRPC() {
        super("DiscordRPC", "Shows RyHa Client status in Discord", Category.MISC);
    }

    @Override
    protected void onEnable() {
        // Discord IPC handshake would go here with a library
        // Placeholder until discord-rpc dependency is added to build.gradle
    }

    @Override
    protected void onDisable() {
        // Disconnect from Discord IPC
    }
}
