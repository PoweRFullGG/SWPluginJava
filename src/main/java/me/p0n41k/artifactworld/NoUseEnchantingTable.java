package me.p0n41k.artifactworld;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;

public class NoUseEnchantingTable implements Listener {

    @EventHandler
    public void onEnchantingTableOpen(InventoryOpenEvent event) {
        if (event.getInventory().getType() == InventoryType.ENCHANTING || event.getInventory().getType() == InventoryType.BREWING) {
            event.setCancelled(true);
        }
    }
}
