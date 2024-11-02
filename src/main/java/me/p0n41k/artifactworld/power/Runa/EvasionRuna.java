package me.p0n41k.artifactworld.power.Runa;

import me.p0n41k.artifactworld.Artifactworld;
import me.p0n41k.artifactworld.artmenu.ArtifactMenu;
import me.p0n41k.artifactworld.artmenu.RunaMenu;
import me.p0n41k.artifactworld.power.Mechanics.SoundUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class EvasionRuna implements Listener {
    private final JavaPlugin plugin;
    private final ArtifactMenu artifactMenu;
    private final int cmduseg = 103;
    private final double evasionChance = 0.25; // 25% шанс уклонения

    public EvasionRuna(JavaPlugin plugin) {
        this.plugin = plugin;
        this.artifactMenu = new ArtifactMenu((Artifactworld) plugin);
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (RunaMenu.checkRunaMenu(player, cmduseg) != null) {
                double randomNumber = Math.random();
                if (randomNumber <= evasionChance) {
                    event.setCancelled(true);
                    Vector randomDirection = getRandomDirection();
                    player.setVelocity(randomDirection.multiply(0.6));
                }
            }
        }
    }

    private Vector getRandomDirection() {
        double angle = Math.random() * Math.PI * 2;
        double x = Math.cos(angle);
        double z = Math.sin(angle);
        return new Vector(x, 0, z).normalize();
    }
}
