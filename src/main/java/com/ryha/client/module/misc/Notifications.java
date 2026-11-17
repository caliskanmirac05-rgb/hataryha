package com.ryha.client.module.misc;

import com.ryha.client.module.Category;
import com.ryha.client.module.Module;
import com.ryha.client.module.setting.BooleanSetting;
import com.ryha.client.module.setting.SliderSetting;

/**
 * Controls in-game toast-style notifications from RyHa modules.
 * The notification renderer is in HUDRenderer.
 */
public class Notifications extends Module {

    public final BooleanSetting sound    = registerSetting(new BooleanSetting("Sound",   "Play sound with notification", true));
    public final SliderSetting  duration = registerSetting(new SliderSetting("Duration", "Notification display time (s)", 3.0, 1.0, 10.0, 0.5));

    public Notifications() {
        super("Notifications", "Shows on-screen notifications for events", Category.MISC);
    }
}
