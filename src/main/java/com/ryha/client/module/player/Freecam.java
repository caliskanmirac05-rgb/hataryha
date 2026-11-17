package com.ryha.client.module.player;

import com.ryha.client.RyHaClient;
import com.ryha.client.event.Subscribe;
import com.ryha.client.event.events.TickEvent;
import com.ryha.client.module.Category;
import com.ryha.client.module.Module;
import com.ryha.client.module.setting.SliderSetting;
import net.minecraft.world.entity.player.Player;

public class Freecam extends Module {

    private final SliderSetting speed = registerSetting(new SliderSetting("Speed", "Freecam move speed", 1.0, 0.1, 5.0, 0.1));

    // Store original position to restore on disable
    private double origX, origY, origZ;
    private float  origYaw, origPitch;

    public Freecam() {
        super("Freecam", "Detach camera from player body", Category.PLAYER);
    }

    @Override
    protected void onEnable() {
        if (mc.player == null) return;
        origX     = mc.player.getX();
        origY     = mc.player.getY();
        origZ     = mc.player.getZ();
        origYaw   = mc.player.getYRot();
        origPitch = mc.player.getXRot();
        mc.player.noPhysics = true;
        mc.player.setDeltaMovement(0, 0, 0);
        RyHaClient.getInstance().getEventBus().registerObject(this);
    }

    @Override
    protected void onDisable() {
        if (mc.player == null) return;
        mc.player.noPhysics = false;
        mc.player.moveTo(origX, origY, origZ, origYaw, origPitch);
        mc.player.setDeltaMovement(0, 0, 0);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (mc.player == null) return;
        mc.player.noPhysics = true;
        mc.player.setDeltaMovement(0, 0, 0); // prevent gravity

        double s   = speed.getValue() * 0.1;
        double yaw   = Math.toRadians(mc.player.getYRot());
        double pitch = Math.toRadians(mc.player.getXRot());

        double dx = 0, dy = 0, dz = 0;
        if (mc.player.input.forwardImpulse > 0) {
            dx -= Math.sin(yaw) * Math.cos(pitch) * s;
            dy -= Math.sin(pitch) * s;
            dz += Math.cos(yaw) * Math.cos(pitch) * s;
        }
        if (mc.player.input.forwardImpulse < 0) {
            dx += Math.sin(yaw) * Math.cos(pitch) * s;
            dy += Math.sin(pitch) * s;
            dz -= Math.cos(yaw) * Math.cos(pitch) * s;
        }
        if (mc.options.keyJump.isDown()) dy += s;
        if (mc.options.keyShift.isDown()) dy -= s;

        mc.player.setPosition(mc.player.getX() + dx, mc.player.getY() + dy, mc.player.getZ() + dz);
    }
}
