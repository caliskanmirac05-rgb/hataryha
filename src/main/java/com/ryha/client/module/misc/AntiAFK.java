package com.ryha.client.module.misc;

import com.ryha.client.RyHaClient;
import com.ryha.client.event.Subscribe;
import com.ryha.client.event.events.TickEvent;
import com.ryha.client.module.Category;
import com.ryha.client.module.Module;
import com.ryha.client.module.setting.ModeSetting;

public class AntiAFK extends Module {

    private final ModeSetting mode = registerSetting(
        new ModeSetting("Mode", "AFK mode", "Rotate", "Rotate", "Jump", "Move")
    );

    private int ticker = 0;

    public AntiAFK() {
        super("AntiAFK", "Prevents getting kicked for AFK", Category.MISC);
    }

    @Override protected void onEnable()  { RyHaClient.getInstance().getEventBus().registerObject(this); }

    @Subscribe
    public void onTick(TickEvent event) {
        if (mc.player == null) return;
        ticker++;

        switch (mode.getValue()) {
            case "Rotate" -> mc.player.setYRot(mc.player.getYRot() + (ticker % 20 == 0 ? 15 : 0));
            case "Jump"   -> { if (ticker % 60 == 0) mc.player.jumpFromGround(); }
            case "Move"   -> {
                if (ticker % 40 < 20) mc.player.setDeltaMovement(0.05, mc.player.getDeltaMovement().y, 0);
                else                  mc.player.setDeltaMovement(-0.05, mc.player.getDeltaMovement().y, 0);
            }
        }
    }
}
