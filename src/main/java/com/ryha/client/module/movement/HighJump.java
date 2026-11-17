package com.ryha.client.module.movement;

import com.ryha.client.RyHaClient;
import com.ryha.client.event.Subscribe;
import com.ryha.client.event.events.TickEvent;
import com.ryha.client.module.Category;
import com.ryha.client.module.Module;
import com.ryha.client.module.setting.SliderSetting;

public class HighJump extends Module {

    private final SliderSetting boost = registerSetting(new SliderSetting("Boost", "Jump velocity boost", 0.5, 0.1, 5.0, 0.1));

    public HighJump() {
        super("HighJump", "Jump extra high", Category.MOVEMENT);
    }

    @Override protected void onEnable()  { RyHaClient.getInstance().getEventBus().registerObject(this); }

    @Subscribe
    public void onTick(TickEvent event) {
        if (mc.player == null) return;
        if (mc.player.onGround() && mc.options.keyJump.isDown()) {
            mc.player.setDeltaMovement(mc.player.getDeltaMovement().x,
                                  0.42f + boost.getValue(),
                                  mc.player.getDeltaMovement().z);
        }
    }
}
