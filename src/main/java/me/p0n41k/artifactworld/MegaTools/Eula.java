package me.p0n41k.artifactworld.MegaTools;

import me.p0n41k.artifactworld.NoManaPlayer;
import me.p0n41k.artifactworld.data.ManaMechanics;
import me.p0n41k.artifactworld.power.Mechanics.CooldownManager;
import me.p0n41k.artifactworld.power.Mechanics.Silence;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class Eula implements Listener {

    private final JavaPlugin plugin;
    private final int eulaCooldownTime = 15;
    private static final CooldownManager eulaCooldownManager = new CooldownManager();
    private static final Map<LivingEntity, Boolean> eulaUsedEntities = new HashMap<>();

    public Eula(JavaPlugin plugin) {
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

            if (isEula(weapon) && !eulaUsedEntities.getOrDefault(entity, false)) {
                event.setCancelled(true);
                if (eulaCooldownManager.hasCooldown(attacker.getName())) {
                    int remainingTime = (int) ((eulaCooldownManager.cooldowns.get(attacker.getName()) - System.currentTimeMillis()) / 1000);
                    attacker.sendMessage(ChatColor.RED + "Артефакт ещё не готов | " + (remainingTime + 1) + " сек.");
                } else {
                    eulaCooldownManager.setCooldown(attacker.getName(), eulaCooldownTime);
                    eulaUsedEntities.put(entity, true);

                    // Основная логика вашего кода
                    final BukkitTask[] taskk = new BukkitTask[1];
                    Location startLocation = entity.getLocation().add(0, 8, 0);
                    ArmorStand armorStand = entity.getWorld().spawn(startLocation, ArmorStand.class, entityy -> {
                        entityy.setGravity(false);
                        entityy.setVisible(false);
                        entityy.setInvulnerable(true);
                        entityy.setMarker(true);
                        entityy.setAI(false);
                        entityy.setCanPickupItems(false);
                        entityy.addScoreboardTag("unmovable");
                    });

                    entity.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 70, 100, false, false));
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 75, 10, false, false));
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 75, 5, false, false));
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 75, 9, false, false));

                    if (entity instanceof Player) {
                        Silence.plusSilenceState(((Player) entity).getPlayer(), 1);
                        if (entity.isDead()) {
                            Silence.plusSilenceState(((Player) entity).getPlayer(), -1);
                            return;
                        }

                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            Silence.plusSilenceState(((Player) entity).getPlayer(), -1);
                        }, 85);
                    }

                    taskk[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                        if (entity.isDead()) {
                            taskk[0].cancel();
                            armorStand.remove();
                            return;
                        }

                        Location armorStandOriginalLocation = armorStand.getLocation();
                        Vector directionToArmorStand = armorStandOriginalLocation.toVector().subtract(entity.getLocation().toVector()).normalize();

                        if (Double.isFinite(directionToArmorStand.getX()) &&
                                Double.isFinite(directionToArmorStand.getY()) &&
                                Double.isFinite(directionToArmorStand.getZ())) {
                            entity.setVelocity(directionToArmorStand.multiply(0.4));
                        }

                        int height = 10;
                        int stepsPerRevolution = 25;
                        double minRadius = 0.3;
                        double maxRadius = 1.5;
                        double yOffsetStart = -8;

                        for (int y = 0; y < height * stepsPerRevolution; y++) {
                            double angle = 2 * Math.PI * y / stepsPerRevolution;

                            double normalizedHeight = (double) y / (height * stepsPerRevolution);
                            double radius = minRadius + (maxRadius - minRadius) * normalizedHeight + 0.3 * Math.random();

                            double x = Math.cos(angle) * radius;
                            double z = Math.sin(angle) * radius;
                            double yOffset = yOffsetStart + (double) y / stepsPerRevolution;

                            Location particleLocation = armorStandOriginalLocation.clone().add(x, yOffset, z);
                            armorStand.getWorld().spawnParticle(Particle.REDSTONE, particleLocation, 1, new Particle.DustOptions(Color.GRAY, 1));
                        }

                    }, 0, 2);

                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        taskk[0].cancel();
                        armorStand.remove();
                        eulaUsedEntities.put(entity, false);
                    }, 70);
                }
            } else if (isEula(weapon) && eulaUsedEntities.getOrDefault(entity, false)) {
                attacker.sendMessage(ChatColor.RED + "Эта сущность уже была использована.");
            }
        }
    }

    private boolean isEula(ItemStack item) {
        if (item.getType() == Material.IRON_SHOVEL && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();

            return meta.hasCustomModelData() && meta.getCustomModelData() == 1;
        }
        return false;
    }
}
