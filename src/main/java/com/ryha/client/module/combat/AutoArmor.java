package com.ryha.client.module.combat;

import com.ryha.client.RyHaClient;
import com.ryha.client.event.Subscribe;
import com.ryha.client.event.events.TickEvent;
import com.ryha.client.module.Category;
import com.ryha.client.module.Module;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

public class AutoArmor extends Module {

    private long lastEquip = 0;

    public AutoArmor() {
        super("AutoArmor", "Automatically equips the best available armor", Category.COMBAT);
    }

    @Override protected void onEnable() { RyHaClient.getInstance().getEventBus().registerObject(this); }

    @Subscribe
    public void onTick(TickEvent event) {
        if (mc.player == null || mc.gameMode == null) return;
        if (System.currentTimeMillis() - lastEquip < 500) return;

        var inv = mc.player.getInventory();
        // Armor slots in PlayerInventory: 36=boots, 37=leggings, 38=chestplate, 39=helmet

        for (int invSlot = 0; invSlot < 36; invSlot++) {
            var stack = inv.getItem(invSlot);
            if (!(stack.getItem() instanceof ArmorItem armor)) continue;

            int armorSlot = switch (armor.getEquipmentSlot()) {
                case FEET  -> 36;
                case LEGS  -> 37;
                case CHEST -> 38;
                case HEAD  -> 39;
                default    -> -1;
            };
            if (armorSlot < 0) continue;

            // Check if that armor slot is already occupied
            var current = mc.player.getInventory().getItem(inv.getContainerSize() - 1 - (armorSlot - 36));

            if (current.isEmpty()) {
                // Equip via shift-click
                int rawSlot = invSlot < 9 ? invSlot + 36 : invSlot;
                mc.gameMode.clickSlot(mc.player.containerMenu.containerId,
                    rawSlot, 0, ClickType.QUICK_MOVE, mc.player);
                lastEquip = System.currentTimeMillis();
                return;
            }
        }
    }
}
