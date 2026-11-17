package com.ryha.client.module.misc;

import com.ryha.client.module.Category;
import com.ryha.client.module.Module;

/** When enabled, all module toggles send a chat message. */
public class ToggleMessages extends Module {
    public ToggleMessages() {
        super("ToggleMessages", "Show chat message when toggling modules", Category.MISC);
    }
}
