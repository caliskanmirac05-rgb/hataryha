package com.ryha.client.module.movement;

import com.ryha.client.RyHaClient;
import com.ryha.client.event.Subscribe;
import com.ryha.client.event.events.TickEvent;
import com.ryha.client.module.Category;
import com.ryha.client.module.Module;
import com.ryha.client.module.setting.SliderSetting;

public class LongJump extends Module {

    private final SliderSetting distance = registerSetting(new SliderSetting("Distance", "Horizontal boost", 1.5, 0.5, 5.0, 0.1));

    public LongJump() {
        super("LongJump", "Jump farther horizontally", Category.MOVEMENT);
    }

    @Override protected void onEnable()  { RyHaClient.getInstance().getEventBus().registerObject(this); }

    @Subscribe
    public void onTick(TickEvent event) {
        if (mc.player == null) return;
        if (!mc.player.onGround() || !mc.options.keyJump.isDown()) return;

        double yaw = Math.toRadians(mc.player.getYRot());
        mc.player.setDeltaMovement(
            -Math.sin(yaw) * distance.getValue() * 0.3,
            0.42,
             Math.cos(yaw) * distance.getValue() * 0.3
        );
    }
}
