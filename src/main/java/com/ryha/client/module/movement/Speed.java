package com.ryha.client.module.movement;

import com.ryha.client.RyHaClient;
import com.ryha.client.event.Subscribe;
import com.ryha.client.event.events.TickEvent;
import com.ryha.client.module.Category;
import com.ryha.client.module.Module;
import com.ryha.client.module.setting.ModeSetting;
import com.ryha.client.module.setting.SliderSetting;
import net.minecraft.world.phys.Vec3;

public class Speed extends Module {

    private final ModeSetting   mode  = registerSetting(new ModeSetting("Mode", "Speed mode", "Strafe", "Strafe", "BHop", "Instant"));
    private final SliderSetting speed = registerSetting(new SliderSetting("Speed", "Movement speed multiplier", 1.5, 0.5, 5.0, 0.1));

    public Speed() {
        super("Speed", "Increases movement speed", Category.MOVEMENT);
    }

    @Override protected void onEnable()  { RyHaClient.getInstance().getEventBus().registerObject(this); }

    @Subscribe
    public void onTick(TickEvent event) {
        if (mc.player == null) return;
        boolean moving = mc.player.input.forwardImpulse != 0 || mc.player.input.leftImpulse != 0;
        if (!moving) return;

        switch (mode.getValue()) {
            case "Strafe" -> {
                double yaw  = Math.toRadians(mc.player.getYRot());
                double fwd  = mc.player.input.forwardImpulse;
                double side = mc.player.input.leftImpulse;
                double s    = speed.getValue() * 0.26;
                double vx   = -(Math.sin(yaw) * fwd + Math.cos(yaw) * side) * s;
                double vz   =  (Math.cos(yaw) * fwd - Math.sin(yaw) * side) * s;
                mc.player.setDeltaMovement(vx, mc.player.getDeltaMovement().y, vz);
            }
            case "BHop" -> {
                if (mc.player.onGround()) mc.player.jumpFromGround();
                double factor = speed.getValue() * 0.22;
                Vec3 vel = mc.player.getDeltaMovement();
                mc.player.setDeltaMovement(vel.x * factor, vel.y, vel.z * factor);
            }
            case "Instant" -> {
                double yaw = Math.toRadians(mc.player.getYRot());
                mc.player.setDeltaMovement(
                    -Math.sin(yaw) * speed.getValue() * 0.3,
                    mc.player.getDeltaMovement().y,
                     Math.cos(yaw) * speed.getValue() * 0.3
                );
            }
        }
    }
}
