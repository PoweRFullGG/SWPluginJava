package me.p0n41k.artifactworld.MegaTools;

import me.p0n41k.artifactworld.Artifactworld;
import me.p0n41k.artifactworld.artmenu.ArtifactMenu;
import me.p0n41k.artifactworld.power.Mechanics.CooldownManager;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Katana implements Listener {
    private final JavaPlugin plugin;

    private final int katanaCooldownTime = 4;
    private static final CooldownManager katanaCooldownManager = new CooldownManager();

    public Katana(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player attacker = (Player) event.getDamager();

            if (!(event.getEntity() instanceof LivingEntity)) {
                return;
            }

            LivingEntity entity = (LivingEntity) event.getEntity();
            ItemStack weapon = attacker.getInventory().getItemInMainHand();

            if (isKatana(weapon)) {
                if (katanaCooldownManager.hasCooldown(attacker.getName())) {
                    int remainingTime = (int) ((katanaCooldownManager.cooldowns.get(attacker.getName()) - System.currentTimeMillis()) / 1000);
                    attacker.sendMessage(ChatColor.RED + "Оружие слишком тяжёлое для быстрых атак | " + (remainingTime + 1) + " сек.");
                    event.setCancelled(true);
                    entity.damage(0.2);
                } else if (!katanaCooldownManager.hasCooldown(attacker.getName())) {
                    katanaCooldownManager.setCooldown(attacker.getName(), katanaCooldownTime);
                    event.setDamage(event.getDamage());
                    BloodEffect(entity);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        if (entity.isDead()) {
                            return;
                        }
                        BloodEffect(entity);
                        entity.damage(1);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            if (entity.isDead()) {
                                return;
                            }
                            BloodEffect(entity);
                            entity.damage(1);
                            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                if (entity.isDead()) {
                                    return;
                                }
                                BloodEffect(entity);
                                entity.damage(1);
                                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                    if (entity.isDead()) {
                                        return;
                                    }
                                    BloodEffect(entity);
                                    entity.damage(1);
                                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                        if (entity.isDead()) {
                                            return;
                                        }
                                        BloodEffect(entity);
                                        entity.damage(1);
                                    }, 20);
                                }, 20);
                            }, 20);
                        }, 20);
                    }, 20);
                }
            }
        }
    }

    private boolean isKatana(ItemStack item) {
        if (item.getType() == Material.DIAMOND_SWORD && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            return meta.hasCustomModelData() && meta.getCustomModelData() == 3;
        }
        return false;
    }

    private void BloodEffect(Entity entity) {
        World world = entity.getWorld();
        Location location = entity.getLocation().add(0, 1, 0);

        for (int i = 0; i < 30; i++) {
            double offsetX = Math.random() * 0.6 - 0.3;
            double offsetY = Math.random() * 0.6 - 0.3;
            double offsetZ = Math.random() * 0.6 - 0.3;

            world.spawnParticle(Particle.BLOCK_CRACK, location, 1, offsetX, offsetY, offsetZ, 0, Material.REDSTONE_BLOCK.createBlockData());
        }
    }
}
