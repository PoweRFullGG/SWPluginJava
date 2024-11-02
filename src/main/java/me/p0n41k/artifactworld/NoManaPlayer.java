package me.p0n41k.artifactworld;

import me.p0n41k.artifactworld.power.Mechanics.SoundUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class NoManaPlayer {

    public static void displayNoManaMessage(Player player) {
        //player.playSound(player.getLocation(), Sound.ITEM_SHIELD_BREAK, 0.25f, 1.0f);
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(Artifactworld.getPlugin(Artifactworld.class), () -> {
            player.sendTitle(ChatColor.RED + "Недостаточно маны", "", 0, 20, 10);
        }, 0, 1);
        Bukkit.getScheduler().runTaskLater(Artifactworld.getPlugin(Artifactworld.class), task::cancel, 3);
    }

    public static void displaySilenceMessage(Player player) {
        //player.playSound(player.getLocation(), Sound.ITEM_SHIELD_BREAK, 0.25f, 1.0f);
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(Artifactworld.getPlugin(Artifactworld.class), () -> {
            player.sendTitle(ChatColor.BLUE + "Безмолвие", "", 0, 20, 10);
            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 0.5f, 1.0f, 1);
        }, 0, 1);
        Bukkit.getScheduler().runTaskLater(Artifactworld.getPlugin(Artifactworld.class), task::cancel, 3);
    }
}
