package me.p0n41k.artifactworld;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;

public class NoBreak implements Listener {

    @EventHandler
    public void onItemDamage(PlayerItemDamageEvent event) {
        ItemStack item = event.getItem();
        if (item != null) {
            Material itemType = item.getType();
            if (isArmor(itemType) || isTool(itemType)) {
                event.setCancelled(true);
            }
        }
    }

    private boolean isArmor(Material material) {
        return material.name().endsWith("_HELMET")
                || material.name().endsWith("_CHESTPLATE")
                || material.name().endsWith("_LEGGINGS")
                || material.name().endsWith("_BOOTS");
    }

    private boolean isTool(Material material) {
        return material.name().endsWith("_PICKAXE")
                || material.name().endsWith("_AXE")
                || material.name().endsWith("_SHOVEL")
                || material.name().endsWith("_HOE")
                || material.name().endsWith("_SWORD");
    }
}
