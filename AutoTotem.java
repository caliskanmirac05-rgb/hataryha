package com.ryha.client.module.combat;

import com.ryha.client.RyHaClient;
import com.ryha.client.event.Subscribe;
import com.ryha.client.event.events.TickEvent;
import com.ryha.client.module.Category;
import com.ryha.client.module.Module;
import com.ryha.client.module.setting.SliderSetting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Items;

public class AutoTotem extends Module {

    private final SliderSetting  healthThreshold = registerSetting(
        new SliderSetting("Health", "Re-equip when health below", 10, 1, 20, 1));

    public AutoTotem() {
        super("AutoTotem", "Automatically keeps a totem of undying in off-hand", Category.COMBAT);
    }

    @Override protected void onEnable() { RyHaClient.getInstance().getEventBus().registerObject(this); }

    @Subscribe
    public void onTick(TickEvent event) {
        LocalPlayer p = mc.player;
        if (p == null || mc.gameMode == null) return;

        // Off-hand already has a totem and health is fine → skip
        if (p.getOffhandItem().is(Items.TOTEM_OF_UNDYING)
            && p.getHealth() > healthThreshold.getValue()) return;

        // Find totem in inventory (slots 0-35, slot 40 = off-hand in Mojang container)
        var inv = p.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            if (inv.getItem(i).is(Items.TOTEM_OF_UNDYING)) {
                // Swap with off-hand via inventory click packet
                // Raw slot index for off-hand is 45 in container 0
                mc.gameMode.clickSlot(p.containerMenu.containerId,
                    i < 9 ? i + 36 : i,          // hotbar vs inventory mapping
                    0, ClickType.PICKUP, p);
                mc.gameMode.clickSlot(p.containerMenu.containerId,
                    45, 0, ClickType.PICKUP, p);
                mc.gameMode.clickSlot(p.containerMenu.containerId,
                    i < 9 ? i + 36 : i,
                    0, ClickType.PICKUP, p);
                return;
            }
        }
    }
}
