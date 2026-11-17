package com.ryha.client.module.player;

import com.ryha.client.RyHaClient;
import com.ryha.client.event.Subscribe;
import com.ryha.client.event.events.TickEvent;
import com.ryha.client.module.Category;
import com.ryha.client.module.Module;
import com.ryha.client.module.setting.SliderSetting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.component.FoodProperties;
import net.minecraft.core.component.DataComponents;

public class AutoEat extends Module {

    private final SliderSetting threshold = registerSetting(new SliderSetting("HungerThreshold", "Eat when hunger below", 15, 1, 20, 1));

    public AutoEat() {
        super("AutoEat", "Automatically eats food when hungry", Category.PLAYER);
    }

    @Override protected void onEnable()  { RyHaClient.getInstance().getEventBus().registerObject(this); }
    @Override protected void onDisable() { RyHaClient.getInstance().getEventBus().unregisterObject(this); }

    @Subscribe
    public void onTick(TickEvent event) {
        if (mc.player == null || mc.gameMode == null) return;
        // getFoodData().getFoodLevel() is the Mojang name for hunger level
        if (mc.player.getFoodData().getFoodLevel() > threshold.getValue()) return;
        if (mc.player.isUsingItem()) return;

        // Check mainhand then offhand for food
        for (InteractionHand hand : InteractionHand.values()) {
            var stack = mc.player.getItemInHand(hand);
            if (stack.has(DataComponents.FOOD)) {
                mc.gameMode.useItem(mc.player, hand);
                return;
            }
        }

        // Find food in hotbar and switch to it
        var inv = mc.player.getInventory();
        for (int i = 0; i < 9; i++) {
            var stack = inv.getItem(i);
            if (stack.has(DataComponents.FOOD)) {
                inv.setSelectedHotbarSlot(i);
                mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND);
                return;
            }
        }
    }
}
