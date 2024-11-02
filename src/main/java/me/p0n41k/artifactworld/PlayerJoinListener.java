package me.p0n41k.artifactworld;

import me.p0n41k.artifactworld.power.GhostlyForm;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        GhostlyForm.setGhostPlayer(player, false);

        player.setWalkSpeed(0.2f);
        player.setFlySpeed(0.1f);
    }
}
