package com.ryha.client.module.combat;

import com.ryha.client.RyHaClient;
import com.ryha.client.event.Subscribe;
import com.ryha.client.event.events.TickEvent;
import com.ryha.client.module.Category;
import com.ryha.client.module.Module;
import com.ryha.client.module.setting.BooleanSetting;
import com.ryha.client.module.setting.SliderSetting;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public class AimAssist extends Module {

    private final SliderSetting  range    = registerSetting(new SliderSetting("Range",    "Aim range", 6.0, 1.0, 10.0, 0.5));
    private final SliderSetting  speed    = registerSetting(new SliderSetting("Speed",    "Aim speed (smoothness)", 3.0, 0.5, 10.0, 0.5));
    private final BooleanSetting onlyHeld = registerSetting(new BooleanSetting("OnlyHeld","Only assist when holding weapon", true));

    public AimAssist() {
        super("AimAssist", "Smoothly aims towards the nearest entity", Category.COMBAT);
    }

    @Override protected void onEnable()  { RyHaClient.getInstance().getEventBus().registerObject(this); }

    @Subscribe
    public void onTick(TickEvent e) {
        if (mc.player == null || mc.level == null) return;

        Optional<Entity> opt = mc.level.entitiesForRendering().stream()
            .filter(en -> en != mc.player && en instanceof LivingEntity)
            .filter(en -> en instanceof Player)
            .filter(en -> mc.player.distanceTo(en) <= range.getValue())
            .min((a, b) -> Double.compare(mc.player.distanceTo(a), mc.player.distanceTo(b)));

        if (opt.isEmpty()) return;
        Entity target = opt.get();

        double dx   = target.getX() - mc.player.getX();
        double dy   = target.getEyeY() - mc.player.getEyeY();
        double dz   = target.getZ() - mc.player.getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);

        float targetYaw   = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90f;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(dy, dist));

        float currentYaw   = mc.player.getYRot();
        float currentPitch = mc.player.getXRot();

        float s = (float) (speed.getValue() * 0.05f);
        mc.player.setYRot(currentYaw   + (targetYaw   - currentYaw)   * s);
        mc.player.setXRot(currentPitch + (targetPitch - currentPitch) * s);
    }
}
