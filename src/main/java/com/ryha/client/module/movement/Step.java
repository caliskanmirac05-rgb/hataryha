package com.ryha.client.module.movement;

import com.ryha.client.RyHaClient;
import com.ryha.client.event.Subscribe;
import com.ryha.client.event.events.TickEvent;
import com.ryha.client.module.Category;
import com.ryha.client.module.Module;
import com.ryha.client.module.setting.SliderSetting;

public class Step extends Module {

    private final SliderSetting height = registerSetting(new SliderSetting("Height", "Step height in blocks", 1.0, 0.5, 2.0, 0.5));

    public Step() {
        super("Step", "Allows stepping over blocks without jumping", Category.MOVEMENT);
    }

    @Override protected void onEnable()  {
        RyHaClient.getInstance().getEventBus().registerObject(this);
        if (mc.player != null) mc.player.setMaxUpStep((float) height.getValue());
    }

    @Override protected void onDisable() {
        if (mc.player != null) mc.player.setMaxUpStep(0.6f);
    }

    @Subscribe
    public void onTick(TickEvent e) {
        if (mc.player != null) mc.player.setMaxUpStep((float) height.getValue());
    }
}
