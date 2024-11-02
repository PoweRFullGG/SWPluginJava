package me.p0n41k.artifactworld;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class NoUseHorseArmorWithCustomModelData implements Listener {

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().getType() == EntityType.HORSE) {
            Player player = event.getPlayer();
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item != null && (item.getType() == Material.DIAMOND_HORSE_ARMOR || item.getType() == Material.IRON_HORSE_ARMOR)) {
                if (item.hasItemMeta() && item.getItemMeta().hasCustomModelData()) {
                    event.setCancelled(true);
                }
            }
        }
    }
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getInventory().getHolder() instanceof Horse) {
            event.setCancelled(true);
        }
    }
}
