package com.ryha.client.module.player;

import com.ryha.client.RyHaClient;
import com.ryha.client.event.Subscribe;
import com.ryha.client.event.events.TickEvent;
import com.ryha.client.module.Category;
import com.ryha.client.module.Module;
import com.ryha.client.module.setting.SliderSetting;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ClickType;

public class ChestStealer extends Module {

    private final SliderSetting  delay = registerSetting(new SliderSetting("Delay", "Steal delay per slot (ms)", 50, 10, 200, 10));
    private long lastSteal  = 0;
    private int  currentSlot = 0;

    public ChestStealer() {
        super("ChestStealer", "Automatically steals all items from open chests", Category.PLAYER);
    }

    @Override protected void onEnable()  {
        currentSlot = 0;
        RyHaClient.getInstance().getEventBus().registerObject(this);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (mc.player == null) return;
        if (!(mc.player.containerMenu instanceof ChestMenu handler)) return;

        long now = System.currentTimeMillis();
        if (now - lastSteal < delay.getValue()) return;

        // Slots 0 to (rows*9-1) are chest slots
        int chestSlots = handler.getRowCount() * 9;
        while (currentSlot < chestSlots) {
            var slot = handler.getSlot(currentSlot);
            if (!slot.getItem().isEmpty()) {
                mc.gameMode.clickSlot(handler.containerId, currentSlot, 0,
                    ClickType.QUICK_MOVE, mc.player);
                lastSteal = now;
                currentSlot++;
                return;
            }
            currentSlot++;
        }
        currentSlot = 0; // reset for next open
    }
}
