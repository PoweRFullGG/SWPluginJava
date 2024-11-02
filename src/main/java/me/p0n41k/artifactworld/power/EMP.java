package me.p0n41k.artifactworld.power;

import me.p0n41k.artifactworld.Artifactworld;
import me.p0n41k.artifactworld.NoManaPlayer;
import me.p0n41k.artifactworld.artmenu.ArtifactMenu;
import me.p0n41k.artifactworld.power.Mechanics.CooldownManager;
import me.p0n41k.artifactworld.power.Mechanics.Silence;
import me.p0n41k.artifactworld.power.Mechanics.SoundUtil;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import me.p0n41k.artifactworld.data.ManaMechanics;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;


public class EMP implements Listener {
    private final JavaPlugin plugin;
    private final ArtifactMenu artifactMenu;
    private static final int eMPCooldownTime = 35;
    private static final int manauseg = 50;
    private final int cmduseg = 122;
    private static final CooldownManager eMPCooldownManager = new CooldownManager();

    public EMP(JavaPlugin plugin) {
        this.plugin = plugin;
        this.artifactMenu = new ArtifactMenu((Artifactworld) plugin);
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);

        new BukkitRunnable() {
            @Override
            public void run() {
                checkSlot();
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    public static void resetAllCooldownsEMP(Player player) {
        eMPCooldownManager.resetCooldown(player.getName());
    }

    public static ItemStack createEMPItem() {
        ItemStack s20 = new ItemStack(Material.DIAMOND_HORSE_ARMOR);
        ItemMeta s20meta = s20.getItemMeta();
        s20meta.setDisplayName(ChatColor.YELLOW + "Электромагнитный Импульс");
        s20meta.setCustomModelData(122);
        List<String> lores20 = new ArrayList<>();
        lores20.add(ChatColor.BLUE + "   Замедляет оказавшихся недалеко противников,");
        lores20.add(ChatColor.BLUE + "   выжигая ману у тех, кто решил не убегать");
        lores20.add(ChatColor.BLUE + "   от взрыва.");
        lores20.add("");
        lores20.add(ChatColor.AQUA + "   Мана: " + ChatColor.GRAY + manauseg);
        lores20.add(ChatColor.RED + "   Перезарядка: " + ChatColor.GRAY + eMPCooldownTime);
        lores20.add("");
        s20meta.setLore(lores20);
        s20.setItemMeta(s20meta);
        return s20;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        Integer hasArtifact = artifactMenu.checkArtifactMenu(player, cmduseg);
        if (hasArtifact == null) {
            sendActionBarMessage(player, " ");
            return;
        }

        ManaMechanics manaMechanics = new ManaMechanics(player.getUniqueId());
        int secondManaUse = manaMechanics.getSecondMana();
        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK) || (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
            if (player.getInventory().getHeldItemSlot() == hasArtifact - 9 && !eMPCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    eMPCooldownManager.setCooldown(player.getName(), eMPCooldownTime);
                    event.setCancelled(true);
                    handleEMP(player);
                    manaMechanics.addSecondMana(-manauseg);
                } else {
                    NoManaPlayer.displayNoManaMessage(player);
                }
            }
        }

    }


    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();

        Integer hasArtifact = artifactMenu.checkArtifactMenu(player, cmduseg);
        if (hasArtifact == null) {
            sendActionBarMessage(player, " ");
            return;
        }

        ManaMechanics manaMechanics = new ManaMechanics(player.getUniqueId());
        int secondManaUse = manaMechanics.getSecondMana();
        if (event.getHand().equals(EquipmentSlot.HAND)) {
            if (player.getInventory().getHeldItemSlot() == hasArtifact - 9 && !eMPCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    eMPCooldownManager.setCooldown(player.getName(), eMPCooldownTime);
                    event.setCancelled(true);
                    handleEMP(player);
                    manaMechanics.addSecondMana(-manauseg);
                } else {
                    NoManaPlayer.displayNoManaMessage(player);
                }
            }
        } else if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
            if (player.getInventory().getHeldItemSlot() == hasArtifact - 9 && !eMPCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    eMPCooldownManager.setCooldown(player.getName(), eMPCooldownTime);
                    event.setCancelled(true);
                    handleEMP(player);
                    manaMechanics.addSecondMana(-manauseg);
                } else {
                    NoManaPlayer.displayNoManaMessage(player);
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getDamager();

        Integer hasArtifact = artifactMenu.checkArtifactMenu(player, cmduseg);
        if (hasArtifact == null) {
            sendActionBarMessage(player, " ");
            return;
        }

        ManaMechanics manaMechanics = new ManaMechanics(player.getUniqueId());
        int secondManaUse = manaMechanics.getSecondMana();
        if (player.getInventory().getHeldItemSlot() == hasArtifact - 9 && !eMPCooldownManager.hasCooldown(player.getName())) {
            if (Silence.getSilenceState(player)>= 1) {
                event.setCancelled(true);
                NoManaPlayer.displaySilenceMessage(player);
                return;
            }
            if (secondManaUse >= manauseg) {
                eMPCooldownManager.setCooldown(player.getName(), eMPCooldownTime);
                event.setCancelled(true);
                handleEMP(player);
                manaMechanics.addSecondMana(-manauseg);
            } else {
                NoManaPlayer.displayNoManaMessage(player);
            }
        }
    }


    public void checkSlot() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {

            String actionBarMessage = "";

            Integer hasArtifact = artifactMenu.checkArtifactMenu(onlinePlayer, cmduseg);
            List<Integer> getspellslots = artifactMenu.getDiamondHorseArmorSlots(onlinePlayer);

            if (hasArtifact == null) {
                continue;
            }

            int heldItemSlot = onlinePlayer.getInventory().getHeldItemSlot();
            if (heldItemSlot == hasArtifact - 9) {
                if (eMPCooldownManager.hasCooldown(onlinePlayer.getName())) {
                    int remainingTime = (int) ((eMPCooldownManager.cooldowns.get(onlinePlayer.getName()) - System.currentTimeMillis()) / 1000);
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + "" + manauseg + ChatColor.GRAY + " | " + ChatColor.RED + "Электромагнитный импульс" + ChatColor.GRAY + " | КД: " + remainingTime + " сек.");
                } else {
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + "" + manauseg + ChatColor.GRAY + " | " + ChatColor.YELLOW + "Электромагнитный импульс" + ChatColor.GRAY + " |");
                }
            } else if (!getspellslots.contains(heldItemSlot)) {
                sendActionBarMessage(onlinePlayer, " ");
            }

            sendActionBarMessage(onlinePlayer, actionBarMessage);
        }
    }


    private void sendActionBarMessage(Player player, String message) {
        player.sendActionBar(message);
    }

    private void handleEMP(Player player) {
        SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 0.9f, 1.0f, 14);
        final BukkitTask[] task = new BukkitTask[1];
        ArmorStand armorStand = player.getWorld().spawn(player.getLocation().add(0, 1.5, 0), ArmorStand.class, entity -> {
            entity.setGravity(false);
            entity.setVisible(false);
            entity.setInvulnerable(true);
            entity.setMarker(true);
            entity.setAI(false);
            entity.setCanPickupItems(false);
            entity.addScoreboardTag("unmovable");
        });

        Random random = new Random();

        task[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            double randomX = (random.nextDouble() * 0.09) + 0.01;
            double randomY = (random.nextDouble() * 0.09) + 0.01;
            double randomZ = (random.nextDouble() * 0.09) + 0.01;

            randomX = random.nextBoolean() ? randomX : -randomX;
            randomY = random.nextBoolean() ? randomY : -randomY;
            randomZ = random.nextBoolean() ? randomZ : -randomZ;

            armorStand.teleport(armorStand.getLocation().add(randomX, randomY, randomZ));

            for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 5) {
                for (double phi = 0; phi <= Math.PI; phi += Math.PI / 5) {
                    double particleX = 0.4 * Math.sin(phi) * Math.cos(theta);
                    double particleY = 0.3 * Math.cos(phi);
                    double particleZ = 0.4 * Math.sin(phi) * Math.sin(theta);

                    Vector particleDirection = new Vector(particleX, particleY, particleZ);
                    spawnParticleAroundArmorStand(armorStand, particleDirection);
                }
            }

            // Притягивание и замедление сущностей
            for (Entity nearbyEntity : armorStand.getNearbyEntities(10, 10, 10)) {
                if (nearbyEntity instanceof LivingEntity && !nearbyEntity.equals(player)) {
                    LivingEntity livingEntity = (LivingEntity) nearbyEntity;
                    Vector toArmorStand = armorStand.getLocation().toVector().subtract(livingEntity.getLocation().toVector());
                    toArmorStand.setY(0);
                    double distance = toArmorStand.length();

                    if (distance > 0) {
                        toArmorStand.normalize(); // Нормализуем вектор
                        double strength = (0.05 / 9) * (10 - distance); // Уменьшаем силу притяжения в 3 раза
                        livingEntity.setVelocity(livingEntity.getVelocity().add(toArmorStand.multiply(strength)));
                    }

                    if (distance <= 8 && distance > 5) {
                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 1, false, false));
                    } else if (distance <= 5 && distance > 2) {
                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 2, false, false));
                    } else if (distance <= 2 && distance > 0) {
                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 3, false, false));
                    }
                }
            }
        }, 0, 2);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!task[0].isCancelled()) {
                task[0].cancel();
            }
            SoundUtil.playSoundForNearbyPlayers(armorStand.getLocation(), Sound.ENTITY_EVOKER_CAST_SPELL, 1.0f, 0.8f, 14);
            SoundUtil.playSoundForNearbyPlayers(armorStand.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.8f, 1.2f, 14);
            for (Entity nearbyEntity : armorStand.getNearbyEntities(10, 10, 10)) {
                if (nearbyEntity instanceof Player && !nearbyEntity.equals(player)) {
                    Player livingEntity = (Player) nearbyEntity;
                    Silence.plusSilenceState(livingEntity, 1);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        Silence.plusSilenceState(livingEntity, -1);
                    }, 60);
                    ManaMechanics manaMechanics = new ManaMechanics(livingEntity.getUniqueId());
                    int secondManaLivingEntity = manaMechanics.getSecondMana();
                    int distance = (int) armorStand.getLocation().distance(livingEntity.getLocation());
                    if (distance <= 8 && distance > 5) {
                        livingEntity.damage(3);
                        if (secondManaLivingEntity <= 40) {
                            manaMechanics.setSecondMana(0);
                        } else {
                            manaMechanics.addSecondMana(-40);
                        }
                    } else if (distance <= 5 && distance > 2) {
                        livingEntity.damage(5);
                        if (secondManaLivingEntity <= 80) {
                            manaMechanics.setSecondMana(0);
                        } else {
                            manaMechanics.addSecondMana(-80);
                        }
                    } else if (distance <= 2 && distance > 0) {
                        livingEntity.damage(7);
                        if (secondManaLivingEntity <= 150) {
                            manaMechanics.setSecondMana(0);
                        } else {
                            manaMechanics.addSecondMana(-150);
                        }
                    }
                } else {
                    if (!nearbyEntity.equals(player)) {
                        LivingEntity livingEntity = (LivingEntity) nearbyEntity;
                        livingEntity.damage(8);
                    }
                }
            }
            new BukkitRunnable() {
                double radius = 0.1;
                final double finalRadius = 12.0;
                final double step = 2.0;

                @Override
                public void run() {
                    if (radius <= finalRadius) {
                        for (double theta = 0; theta <= Math.PI; theta += Math.PI / 8) {
                            for (double phi = 0; phi < 2 * Math.PI; phi += Math.PI / 8) {
                                double x = radius * Math.sin(theta) * Math.cos(phi);
                                double y = radius * Math.sin(theta) * Math.sin(phi);
                                double z = radius * Math.cos(theta);
                                Location particleLocation = armorStand.getLocation().clone().add(x, y, z);
                                player.getWorld().spawnParticle(Particle.REDSTONE, particleLocation, 2, 0, 0, 0, 1,
                                        new Particle.DustOptions(Color.fromRGB(0, 102, 204), 1));
                            }
                        }
                        radius += step;
                    } else {
                        armorStand.remove();
                        cancel();
                    }
                }
            }.runTaskTimer(plugin, 0, 2);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (!armorStand.isDead()) {
                    armorStand.remove();
                }
            }, 20);
        }, 20 * 3);
    }

    private void spawnParticleAroundArmorStand(ArmorStand armorStand, Vector particleDirection) {
        Location particleLocation = armorStand.getLocation().clone().add(particleDirection);
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(0, 102, 204), 1);
        armorStand.getWorld().spawnParticle(Particle.REDSTONE, particleLocation, 1, 0, 0, 0, 0, dustOptions);
    }
}