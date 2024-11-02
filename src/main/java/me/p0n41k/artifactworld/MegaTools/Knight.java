package me.p0n41k.artifactworld.MegaTools;

import me.p0n41k.artifactworld.Artifactworld;
import me.p0n41k.artifactworld.NoManaPlayer;
import me.p0n41k.artifactworld.artmenu.ArtifactMenu;
import me.p0n41k.artifactworld.data.ManaMechanics;
import me.p0n41k.artifactworld.power.Mechanics.CooldownManager;
import me.p0n41k.artifactworld.power.Mechanics.Silence;
import me.p0n41k.artifactworld.power.Mechanics.SoundUtil;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Knight implements Listener {
    private final JavaPlugin plugin;

    private final int katanaCooldownTime = 4;
    private static final CooldownManager katanaCooldownManager = new CooldownManager();

    public Knight(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player attacker = (Player) event.getDamager();

            if (!(event.getEntity() instanceof LivingEntity)) {
                return; // Если нет, просто выходим из метода
            }

            LivingEntity entity = (LivingEntity) event.getEntity();
            ItemStack weapon = attacker.getInventory().getItemInMainHand();

            if (isKnight(weapon)) {
                attacker.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10 * 20, 3, false, false));
                attacker.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 1 * 20, 3, false, false));
                attacker.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 10 * 20, 1, false, false));
                attacker.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 3 * 20, 2, false, false));
                attacker.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, 15 * 20, 3, false, false));
                SoundUtil.playSoundForNearbyPlayers(attacker.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 0.4f, 1.0f, 10);
                SoundUtil.playSoundForNearbyPlayers(attacker.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 0.4f, 1.0f, 10);
                Location mouthLocation = attacker.getEyeLocation().clone().add(attacker.getEyeLocation().getDirection().multiply(0.5));
                attacker.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, mouthLocation, 0, 0, 0, 0, 0.4);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    attacker.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, mouthLocation, 0, 0, 0, 0, 0.4);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        attacker.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, mouthLocation, 0, 0, 0, 0, 0.4);
                    }, 3);
                }, 3);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack weapon = player.getInventory().getItemInMainHand();

        if (isKnight(weapon)) {
            if ((event.getAction() == Action.RIGHT_CLICK_BLOCK) || (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10 * 20, 3, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 1 * 20, 3, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 10 * 20, 1, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 3 * 20, 2, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, 15 * 20, 3, false, false));
                SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 0.4f, 1.0f, 10);
                SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 0.4f, 1.0f, 10);
                Location mouthLocation = player.getEyeLocation().clone().add(player.getEyeLocation().getDirection().multiply(0.5));
                player.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, mouthLocation, 0, 0, 0, 0, 0.4);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    player.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, mouthLocation, 0, 0, 0, 0, 0.4);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        player.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, mouthLocation, 0, 0, 0, 0, 0.4);
                    }, 3);
                }, 3);
            }
        }
    }

    private boolean isKnight(ItemStack item) {
        if (item.getType() == Material.DIAMOND_HORSE_ARMOR && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            return meta.hasCustomModelData() && meta.getCustomModelData() == 89;
        }
        return false;
    }
}
