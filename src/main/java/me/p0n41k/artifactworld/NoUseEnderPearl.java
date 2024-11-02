package me.p0n41k.artifactworld;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class NoUseEnderPearl implements Listener {

    @EventHandler
    public void onPlayerUseEnderPearl(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getItem() != null && event.getItem().getType() == Material.ENDER_PEARL) {
            event.setCancelled(true);
        }
    }
}
