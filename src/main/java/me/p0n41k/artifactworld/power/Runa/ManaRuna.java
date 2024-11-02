package me.p0n41k.artifactworld.power.Runa;

import me.p0n41k.artifactworld.Artifactworld;
import me.p0n41k.artifactworld.artmenu.ArtifactMenu;
import me.p0n41k.artifactworld.artmenu.RunaMenu;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.p0n41k.artifactworld.data.ManaMechanics;



public class ManaRuna implements Listener {
    private final JavaPlugin plugin;
    private final ArtifactMenu artifactMenu;
    private final int cmduseg = 101;

    public ManaRuna(JavaPlugin plugin) {
        this.plugin = plugin;
        this.artifactMenu = new ArtifactMenu((Artifactworld) plugin);
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if (RunaMenu.checkRunaMenu(onlinePlayer, cmduseg) != null){
                        ManaMechanics manaMechanics = new ManaMechanics(onlinePlayer.getUniqueId());
                        manaMechanics.addSecondMana(2);
                    }
                }
            }
        }.runTaskTimer(plugin, 40L, 40L);
    }
}