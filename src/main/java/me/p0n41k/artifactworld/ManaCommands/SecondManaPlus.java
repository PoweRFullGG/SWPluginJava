package me.p0n41k.artifactworld.ManaCommands;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import me.p0n41k.artifactworld.data.ManaMechanics;
import org.bukkit.scheduler.BukkitTask;

public class SecondManaPlus implements Listener {
    private JavaPlugin plugin;
    private Map<UUID, Long> lastMovementTimes;
    private BukkitTask manaTask;

    public SecondManaPlus(JavaPlugin plugin) {
        this.plugin = plugin;
        this.lastMovementTimes = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        new BukkitRunnable() {
            @Override
            public void run() {
                addManafast();
                addMana();
            }
        }.runTaskTimer(plugin, 0L, 20L);

    }

    private void addManafast() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!hasMovedRecently(player)) {
                ManaMechanics manaMechanics = new ManaMechanics(player.getUniqueId());
                int maxmana = manaMechanics.getMaxMana();
                int secondmana = manaMechanics.getSecondMana();
                if (secondmana != maxmana) {
                    if (player.isOnline()) {
                        manaMechanics.addSecondMana(8);
                        double radius = 0.5;
                        double angle = Math.random() * 2 * Math.PI;

                        double x = player.getLocation().getX() + radius * Math.cos(angle);
                        double y = player.getLocation().getY() + Math.random() * 2;
                        double z = player.getLocation().getZ() + radius * Math.sin(angle);

                        player.getWorld().spawnParticle(Particle.REDSTONE, new Location(player.getWorld(), x, y, z), 1,
                                new Particle.DustOptions(Color.fromRGB(0, 128, 255), 2));
                    }
                }
            }
        }
    }

    private void addMana() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            ManaMechanics manaMechanics = new ManaMechanics(player.getUniqueId());
            int maxmana = manaMechanics.getMaxMana();
            int secondmana = manaMechanics.getSecondMana();
            if (secondmana != maxmana) {
                manaMechanics.addSecondMana(3);
            }
        }
    }

    private boolean hasMovedRecently(Player player) {
        UUID playerId = player.getUniqueId();
        if (!lastMovementTimes.containsKey(playerId)) {
            return false;
        }
        long lastMovementTime = lastMovementTimes.get(playerId);
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastMovementTime) < 3000;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        double fromX = event.getFrom().getX();
        double fromY = event.getFrom().getY();
        double fromZ = event.getFrom().getZ();
        double toX = event.getTo().getX();
        double toY = event.getTo().getY();
        double toZ = event.getTo().getZ();

        if (fromX != toX || fromZ != toZ || fromY != toY) {
            lastMovementTimes.put(playerId, System.currentTimeMillis());
        }
    }

}
