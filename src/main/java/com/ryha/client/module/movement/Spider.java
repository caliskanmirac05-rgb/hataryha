package com.ryha.client.module.movement;

import com.ryha.client.RyHaClient;
import com.ryha.client.event.Subscribe;
import com.ryha.client.event.events.TickEvent;
import com.ryha.client.module.Category;
import com.ryha.client.module.Module;
import com.ryha.client.module.setting.SliderSetting;

public class Spider extends Module {

    private final SliderSetting speed = registerSetting(new SliderSetting("ClimbSpeed", "Wall climb speed", 0.2, 0.05, 1.0, 0.05));

    public Spider() {
        super("Spider", "Allows climbing walls without a ladder", Category.MOVEMENT);
    }

    @Override protected void onEnable()  { RyHaClient.getInstance().getEventBus().registerObject(this); }

    @Subscribe
    public void onTick(TickEvent event) {
        if (mc.player == null) return;
        if (!mc.player.horizontalCollision) return;
        if (mc.player.onGround()) return;

        // Push upward when holding against a wall
        boolean moving = mc.player.input.forwardImpulse != 0 || mc.player.input.leftImpulse != 0;
        if (moving) {
            mc.player.setDeltaMovement(mc.player.getDeltaMovement().x, speed.getValue(), mc.player.getDeltaMovement().z);
        }
    }
}
