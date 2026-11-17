package com.ryha.client.module.combat;

import com.ryha.client.RyHaClient;
import com.ryha.client.event.Subscribe;
import com.ryha.client.event.events.AttackEvent;
import com.ryha.client.module.Category;
import com.ryha.client.module.Module;
import com.ryha.client.module.setting.ModeSetting;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;

public class Criticals extends Module {

    private final ModeSetting mode = registerSetting(
        new ModeSetting("Mode", "Critical mode", "Packet", "Packet", "Jump", "MiniJump")
    );

    public Criticals() {
        super("Criticals", "Always lands critical hits on enemies", Category.COMBAT);
    }

    @Override protected void onEnable()  { RyHaClient.getInstance().getEventBus().registerObject(this); }

    @Subscribe
    public void onAttack(AttackEvent event) {
        if (mc.player == null || mc.player.onGround()) {
            return;
        }

        switch (mode.getValue()) {
            case "Packet" -> {
                // Send micro-packets to trigger critical state server-side
                var net = mc.getConnection();
                if (net == null) return;
                double x = mc.player.getX(), y = mc.player.getY(), z = mc.player.getZ();
                net.send(new ServerboundMovePlayerPacket.PosAndOnGround(x, y + 0.0625, z, false, mc.player.horizontalCollision));
                net.send(new ServerboundMovePlayerPacket.PosAndOnGround(x, y,          z, false, mc.player.horizontalCollision));
                net.send(new ServerboundMovePlayerPacket.PosAndOnGround(x, y + 1.1e-7, z, false, mc.player.horizontalCollision));
                net.send(new ServerboundMovePlayerPacket.PosAndOnGround(x, y,          z, false, mc.player.horizontalCollision));
            }
            case "Jump"     -> mc.player.jumpFromGround();
            case "MiniJump" -> mc.player.push(0, 0.08, 0);
        }
    }
}
