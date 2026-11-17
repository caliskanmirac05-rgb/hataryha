package com.ryha.client.module.combat;

import com.ryha.client.RyHaClient;
import com.ryha.client.event.Subscribe;
import com.ryha.client.event.events.TickEvent;
import com.ryha.client.module.Category;
import com.ryha.client.module.Module;
import com.ryha.client.module.setting.SliderSetting;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.InteractionHand;

public class TriggerBot extends Module {

    private final SliderSetting delay = registerSetting(new SliderSetting("Delay", "Delay after trigger (ms)", 50, 0, 300, 10));

    private long lastHit = 0;

    public TriggerBot() {
        super("TriggerBot", "Attacks entity when crosshair is on it", Category.COMBAT);
    }

    @Override protected void onEnable() { RyHaClient.getInstance().getEventBus().registerObject(this); }

    @Subscribe
    public void onTick(TickEvent e) {
        if (mc.player == null || mc.gameMode == null) return;
        if (!(mc.crosshairEntity instanceof LivingEntity target)) return;

        long now = System.currentTimeMillis();
        if (now - lastHit < delay.getValue()) return;

        mc.gameMode.attack(mc.player, target);
        mc.player.swing(InteractionHand.MAIN_HAND);
        lastHit = now;
    }
}
