package com.ryha.client.module.movement;

import com.ryha.client.RyHaClient;
import com.ryha.client.event.Subscribe;
import com.ryha.client.event.events.TickEvent;
import com.ryha.client.module.Category;
import com.ryha.client.module.Module;
import com.ryha.client.module.setting.ModeSetting;
import com.ryha.client.module.setting.SliderSetting;

public class Fly extends Module {

    private final ModeSetting   mode  = registerSetting(new ModeSetting("Mode", "Fly mode", "Vanilla", "Vanilla", "Creative", "Glide"));
    private final SliderSetting speed = registerSetting(new SliderSetting("Speed", "Fly speed", 1.5, 0.1, 10.0, 0.1));

    public Fly() {
        super("Fly", "Allows flying in survival mode", Category.MOVEMENT);
    }

    @Override protected void onEnable()  {
        RyHaClient.getInstance().getEventBus().registerObject(this);
        if (mc.player != null) mc.player.getAbilities().mayfly = true;
    }

    @Override protected void onDisable() {
        if (mc.player == null) return;
        mc.player.getAbilities().mayfly = false;
        mc.player.getAbilities().flying      = false;
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (mc.player == null) return;
        var abilities = mc.player.getAbilities();

        switch (mode.getValue()) {
            case "Vanilla" -> {
                abilities.mayfly = true;
                abilities.flying      = true;
                abilities.flyingSpeed = (float) (speed.getValue() * 0.05f);
            }
            case "Creative" -> {
                mc.player.getAbilities().flyingSpeed = (float) (speed.getValue() * 0.05);
            }
            case "Glide" -> {
                mc.player.setDeltaMovement(mc.player.getDeltaMovement().x,
                                     mc.player.getDeltaMovement().y < -0.1 ? -0.04 : mc.player.getDeltaMovement().y,
                                     mc.player.getDeltaMovement().z);
            }
        }
    }
}
