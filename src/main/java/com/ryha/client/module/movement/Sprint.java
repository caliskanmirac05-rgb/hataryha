package com.ryha.client.module.movement;

import com.ryha.client.RyHaClient;
import com.ryha.client.event.Subscribe;
import com.ryha.client.event.events.TickEvent;
import com.ryha.client.module.Category;
import com.ryha.client.module.Module;
import com.ryha.client.module.setting.BooleanSetting;

public class Sprint extends Module {

    private final BooleanSetting omniSprint = registerSetting(
        new BooleanSetting("OmniSprint", "Sprint in all directions", false));

    public Sprint() {
        super("Sprint", "Always sprints", Category.MOVEMENT);
    }

    @Override protected void onEnable()  { RyHaClient.getInstance().getEventBus().registerObject(this); }

    @Subscribe
    public void onTick(TickEvent event) {
        if (mc.player == null) return;
        boolean moving = mc.player.input.forwardImpulse > 0
            || (omniSprint.isEnabled() && (mc.player.input.leftImpulse != 0));
        if (moving && !mc.player.isInWater()) {
            mc.player.setSprinting(true);
        }
    }
}
