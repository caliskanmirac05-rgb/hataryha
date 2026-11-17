package com.ryha.client.module.misc;

import com.ryha.client.module.Category;
import com.ryha.client.module.Module;
import com.ryha.client.module.setting.BooleanSetting;

/** Displays server IP, TPS, and player count in HUD. */
public class ServerInfo extends Module {

    public final BooleanSetting showIP      = registerSetting(new BooleanSetting("IP",      "Show server IP",      true));
    public final BooleanSetting showTPS     = registerSetting(new BooleanSetting("TPS",     "Show server TPS",     true));
    public final BooleanSetting showPlayers = registerSetting(new BooleanSetting("Players", "Show player count",   false));

    public ServerInfo() {
        super("ServerInfo", "Displays server information in HUD", Category.MISC);
    }
}
