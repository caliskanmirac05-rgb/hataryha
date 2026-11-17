package com.ryha.client.module.combat;

import com.ryha.client.RyHaClient;
import com.ryha.client.event.Subscribe;
import com.ryha.client.event.events.TickEvent;
import com.ryha.client.module.Category;
import com.ryha.client.module.Module;
import com.ryha.client.module.setting.BooleanSetting;
import com.ryha.client.module.setting.ModeSetting;
import com.ryha.client.module.setting.SliderSetting;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class KillAura extends Module {

    private final SliderSetting  range          = registerSetting(new SliderSetting("Range", "Attack range", 3.5, 1.0, 6.0, 0.1));
    private final SliderSetting  cps            = registerSetting(new SliderSetting("CPS", "Clicks per second", 12, 1, 20, 1));
    private final ModeSetting    mode           = registerSetting(new ModeSetting("Mode", "Target selection mode", "Single", "Single", "Multi", "Switch"));
    private final ModeSetting    priority       = registerSetting(new ModeSetting("Priority", "Target priority", "Distance", "Distance", "Health", "Angle"));
    private final BooleanSetting attackPlayers  = registerSetting(new BooleanSetting("Players", "Attack players", true));
    private final BooleanSetting attackMobs     = registerSetting(new BooleanSetting("Mobs", "Attack hostile mobs", true));
    private final BooleanSetting attackAnimals  = registerSetting(new BooleanSetting("Animals", "Attack animals", false));
    // 26.2 Chaos Cubed — Sulfur Cube is a new hostile mob from Sulfur Caves
    private final BooleanSetting attackSulfur   = registerSetting(new BooleanSetting("SulfurCubes", "Attack Sulfur Cubes (26.2)", true));
    private final BooleanSetting rotations      = registerSetting(new BooleanSetting("Rotations", "Rotate to target", true));
    private final BooleanSetting throughWalls   = registerSetting(new BooleanSetting("ThroughWalls", "Attack through walls", false));

    private long lastAttack = 0;

    public KillAura() {
        super("KillAura", "Automatically attacks nearby entities", Category.COMBAT);
    }

    @Override
    protected void onEnable() {
        RyHaClient.getInstance().getEventBus().registerObject(this);
    }

    @Override
    protected void onDisable() {
        RyHaClient.getInstance().getEventBus().unregisterObject(this);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (mc.player == null || mc.level == null || mc.gameMode == null) return;

        long now = System.currentTimeMillis();
        long delay = 1000L / cps.getValue().longValue();
        if (now - lastAttack < delay) return;

        List<Entity> targets = getTargets();
        if (targets.isEmpty()) return;

        Entity target = targets.get(0);

        if (rotations.isEnabled()) {
            rotateToEntity(target);
        }

        mc.gameMode.attack(mc.player, target);
        mc.player.swing(InteractionHand.MAIN_HAND);
        lastAttack = now;

        if (mode.is("Multi")) {
            int extraHits = Math.min(targets.size() - 1, 3);
            for (int i = 1; i <= extraHits; i++) {
                mc.gameMode.attack(mc.player, targets.get(i));
            }
        }
    }

    private boolean isSulfurCube(Entity e) {
        // SulfurCube is a new 26.2 mob — match by class simple name in case the
        // exact package path shifts between snapshots
        String name = e.getClass().getSimpleName();
        return name.equals("SulfurCube") || name.equals("SmallSulfurCube");
    }

    private List<Entity> getTargets() {
        double r = range.getValue();
        return mc.level.entitiesForRendering().stream()
            .filter(e -> e != mc.player)
            .filter(e -> e instanceof LivingEntity le && le.getHealth() > 0)
            .filter(e -> mc.player.distanceTo(e) <= r)
            .filter(e -> {
                if (isSulfurCube(e))          return attackSulfur.isEnabled();
                if (e instanceof Player)      return attackPlayers.isEnabled();
                if (e instanceof Monster)     return attackMobs.isEnabled();
                if (e instanceof Animal)      return attackAnimals.isEnabled();
                return false;
            })
            .filter(e -> throughWalls.isEnabled() || mc.player.canSee(e))
            .sorted(getComparator())
            .collect(Collectors.toList());
    }

    private Comparator<Entity> getComparator() {
        return switch (priority.getValue()) {
            case "Health" -> Comparator.comparingDouble(e -> ((LivingEntity) e).getHealth());
            case "Angle"  -> Comparator.comparingDouble(e -> getAngleTo(e));
            default       -> Comparator.comparingDouble(e -> mc.player.distanceTo(e));
        };
    }

    private double getAngleTo(Entity e) {
        double dx = e.getX() - mc.player.getX();
        double dz = e.getZ() - mc.player.getZ();
        double yaw = Math.toDegrees(Math.atan2(dz, dx)) - 90;
        double diff = Math.abs(yaw - mc.player.getYRot());
        return diff > 180 ? 360 - diff : diff;
    }

    private void rotateToEntity(Entity e) {
        double dx   = e.getX() - mc.player.getX();
        double dy   = e.getEyeY() - mc.player.getEyeY();
        double dz   = e.getZ() - mc.player.getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);
        float  yaw  = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90f;
        float  pitch= (float) -Math.toDegrees(Math.atan2(dy, dist));
        mc.player.setYRot(yaw);
        mc.player.setXRot(pitch);
    }
}
