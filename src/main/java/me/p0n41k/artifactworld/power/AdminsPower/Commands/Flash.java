package me.p0n41k.artifactworld.power.AdminsPower.Commands;

import me.p0n41k.artifactworld.Artifactworld;
import me.p0n41k.artifactworld.artmenu.ArtifactMenu;
import me.p0n41k.artifactworld.power.*;
import me.p0n41k.artifactworld.power.AdminsPower.*;
import me.p0n41k.artifactworld.power.Mechanics.SoundUtil;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class Flash implements CommandExecutor {
    private final JavaPlugin plugin;
    private final Map<Player, BukkitTask> activeTasks = new HashMap<>(); // Хранение активных задач для каждого игрока

    public Flash(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Используйте: /flash <Игрок>");
            return true;
        }

        Player targetPlayer = sender.getServer().getPlayer(args[0]);

        if (targetPlayer == null) {
            sender.sendMessage(ChatColor.GREEN + "Игрок не в сети");
            return true;
        }

        if (activeTasks.containsKey(targetPlayer)) {
            activeTasks.get(targetPlayer).cancel();
            activeTasks.remove(targetPlayer);
            sender.sendMessage(ChatColor.GREEN + "Игрок " + targetPlayer.getName() + " больше не псих :(");
            targetPlayer.removePotionEffect(PotionEffectType.SPEED);
        } else {
            targetPlayer.getWorld().strikeLightning(targetPlayer.getLocation());
            SoundUtil.playSoundForNearbyPlayers(targetPlayer.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f, 16);
            targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 35, false, false));
            FlashPlayer(targetPlayer);
            sender.sendMessage(ChatColor.GREEN + "Игрок " + targetPlayer.getName() + " теперь в режиме психа :)");
        }

        return true;
    }

    private void spawnLightningSpeedParticleRed(Player player, Vector particleDirection) {
        Location playerLocation = player.getLocation();

        double particleSpacing = 0.13;
        double startY = playerLocation.getY() + 1.3;
        double endY = playerLocation.getY() - 0.1;

        Color[] rainbowColors = {
                Color.fromRGB(204, 0, 0),
                Color.fromRGB(255, 255, 0),
                Color.fromRGB(204, 0, 0),
                Color.fromRGB(255, 255, 0),
                Color.fromRGB(204, 0, 0),
        };

        double colorStep = (startY - endY) / rainbowColors.length;

        for (int i = 0; i < rainbowColors.length; i++) {
            double currentY = startY - i * colorStep;
            Color currentColor = rainbowColors[i];

            Location particleLocation = new Location(playerLocation.getWorld(), playerLocation.getX(), currentY, playerLocation.getZ());
            Particle.DustOptions dustOptions = new Particle.DustOptions(currentColor, 1);
            playerLocation.getWorld().spawnParticle(Particle.REDSTONE, particleLocation, 1, particleSpacing, particleSpacing, particleSpacing, 0, dustOptions);
        }
    }

    private void FlashPlayer(Player player) {
        final BukkitTask[] task = new BukkitTask[1];
        task[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 0.5) {
                for (double phi = 0; phi <= Math.PI; phi += Math.PI / 0.5) {
                    double particleX = 0.1 * Math.sin(phi) * Math.cos(theta);
                    double particleY = 0.1 * Math.cos(phi);
                    double particleZ = 0.1 * Math.sin(phi) * Math.sin(theta);

                    Vector particleDirection = new Vector(particleX, particleY, particleZ);
                    spawnLightningSpeedParticleRed(player, particleDirection);
                }
            }
            if (!player.isOnline()) {
                task[0].cancel();
            }
        }, 0, 1);

        activeTasks.put(player, task[0]);
    }
}
