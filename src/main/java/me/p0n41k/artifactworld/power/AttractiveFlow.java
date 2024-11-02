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
import java.util.concurrent.atomic.AtomicReference;


public class AttractiveFlow implements Listener {
    private final JavaPlugin plugin;
    private final ArtifactMenu artifactMenu;
    private static final int attractionCooldownTime = 16;
    private static final int manauseg = 50;
    private final int cmduseg = 110;
    private static final CooldownManager attractionCooldownManager = new CooldownManager();

    public AttractiveFlow(JavaPlugin plugin) {
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

    public static void resetAllCooldownsAttractiveFlow(Player player) {
        attractionCooldownManager.resetCooldown(player.getName());
    }

    public static ItemStack createAttractiveFlowItem() {
        ItemStack s8 = new ItemStack(Material.DIAMOND_HORSE_ARMOR);
        ItemMeta s8meta = s8.getItemMeta();
        s8meta.setDisplayName(ChatColor.YELLOW + "Притягивающий Поток");
        s8meta.setCustomModelData(110);
        List<String> lores8 = new ArrayList<>();
        lores8.add(ChatColor.BLUE + "   Выпускает поток ветра, притягивающий первое");
        lores8.add(ChatColor.BLUE + "   существо, попавшееся на пути.");
        lores8.add("");
        lores8.add(ChatColor.AQUA + "   Мана: " + ChatColor.GRAY + manauseg);
        lores8.add(ChatColor.RED + "   Перезарядка: " + ChatColor.GRAY + attractionCooldownTime);
        lores8.add("");
        s8meta.setLore(lores8);
        s8.setItemMeta(s8meta);
        return s8;
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
            if (player.getInventory().getHeldItemSlot() == hasArtifact - 9 && !attractionCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    attractionCooldownManager.setCooldown(player.getName(), attractionCooldownTime);
                    event.setCancelled(true);
                    handleAttractiveFlow(player);
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
            if (player.getInventory().getHeldItemSlot() == hasArtifact - 9 && !attractionCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    attractionCooldownManager.setCooldown(player.getName(), attractionCooldownTime);
                    event.setCancelled(true);
                    handleAttractiveFlow(player);
                    manaMechanics.addSecondMana(-manauseg);
                } else {
                    NoManaPlayer.displayNoManaMessage(player);
                }
            }
        } else if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
            if (player.getInventory().getHeldItemSlot() == hasArtifact - 9 && !attractionCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    attractionCooldownManager.setCooldown(player.getName(), attractionCooldownTime);
                    event.setCancelled(true);
                    handleAttractiveFlow(player);
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
        if (player.getInventory().getHeldItemSlot() == hasArtifact - 9 && !attractionCooldownManager.hasCooldown(player.getName())) {
            if (Silence.getSilenceState(player)>= 1) {
                event.setCancelled(true);
                NoManaPlayer.displaySilenceMessage(player);
                return;
            }
            if (secondManaUse >= manauseg) {
                attractionCooldownManager.setCooldown(player.getName(), attractionCooldownTime);
                event.setCancelled(true);
                handleAttractiveFlow(player);
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
                if (attractionCooldownManager.hasCooldown(onlinePlayer.getName())) {
                    int remainingTime = (int) ((attractionCooldownManager.cooldowns.get(onlinePlayer.getName()) - System.currentTimeMillis()) / 1000);
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + "" + manauseg + ChatColor.GRAY + " | " + ChatColor.RED + "Притягивающий Поток" + ChatColor.GRAY + " | КД: " + remainingTime + " сек.");
                } else {
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + "" + manauseg + ChatColor.GRAY + " | " + ChatColor.YELLOW + "Притягивающий Поток" + ChatColor.GRAY + " |");
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

    private void handleAttractiveFlow(Player player) {
        Location playerLocation = player.getLocation();
        final BukkitTask[] task = new BukkitTask[1];
        final BukkitTask[] task2 = new BukkitTask[1];
        final BukkitTask[] task3 = new BukkitTask[1];

        Location armorStandLocation = new Location(playerLocation.getWorld(), playerLocation.getX(), playerLocation.getY() + 1.1, playerLocation.getZ());
        ArmorStand armorStand = player.getWorld().spawn(armorStandLocation, ArmorStand.class, entity -> {
            entity.setGravity(false);
            entity.setVisible(false);
            entity.setInvulnerable(true);
            entity.setMarker(true);
            entity.setAI(false);
            entity.setCanPickupItems(false);
            entity.addScoreboardTag("unmovable");
            entity.setSmall(true);
        });
        SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_SHULKER_SHOOT, 0.7f, 1.0f, 10);
        SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 0.2f, 1.0f, 16);

        float yaw = player.getLocation().getYaw();
        float pitch = player.getLocation().getPitch();

        double yawAngle = Math.toRadians(yaw);
        double pitchAngle = Math.toRadians(pitch);

        double x = -Math.sin(yawAngle) * Math.cos(pitchAngle);
        double y = -Math.sin(pitchAngle);
        double z = Math.cos(yawAngle) * Math.cos(pitchAngle);

        Vector direction = new Vector(x, y, z).normalize().multiply(1.4);

        Player owner = player;


        task[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            moveArmorStand(armorStand, direction);

            for (Entity nearbyEntity : armorStand.getNearbyEntities(0.4, 0.4, 0.4)) {
                if (nearbyEntity instanceof LivingEntity && !nearbyEntity.equals(owner) && !(nearbyEntity instanceof ArmorStand)) {
                    LivingEntity livingEntity = (LivingEntity) nearbyEntity;
                    task3[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                        Location teleportLocation = armorStand.getLocation().clone().subtract(0, 0.3, 0);

                        if (player != null) {
                            Vector directionToPlayer = player.getLocation().subtract(livingEntity.getLocation()).toVector().normalize();

                            livingEntity.teleport(teleportLocation);

                            if (directionToPlayer.getX() != 0 || directionToPlayer.getZ() != 0) {
                                float yaww = (float) Math.toDegrees(Math.atan2(-directionToPlayer.getX(), directionToPlayer.getZ()));
                                float pitchh = (float) Math.toDegrees(Math.atan2(directionToPlayer.getY(), Math.sqrt(directionToPlayer.getX() * directionToPlayer.getX() + directionToPlayer.getZ() * directionToPlayer.getZ())));

                                livingEntity.setRotation(yaww, -pitchh);
                            }
                        }
                    }, 0, 1);
                    task2[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                        if (livingEntity.isDead() || player.isDead()) {
                            task2[0].cancel();
                            task3[0].cancel();
                            armorStand.remove();
                            return;
                        }
                        Vector directionToPlayer = player.getLocation().toVector().subtract(armorStand.getLocation().toVector()).normalize().add(new Vector(0, 0.2, 0)).multiply(0.6);
                        moveArmorStand(armorStand, directionToPlayer);

                        for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 4) {
                            for (double phi = 0; phi <= Math.PI; phi += Math.PI / 4) {
                                double particleX = 0.3 * Math.sin(phi) * Math.cos(theta);
                                double particleY = 0.3 * Math.cos(phi);
                                double particleZ = 0.3 * Math.sin(phi) * Math.sin(theta);

                                Vector particleDirection = new Vector(particleX, particleY, particleZ);
                                spawnParticleAroundArmorStand(armorStand, particleDirection);
                            }
                        }
                        if (armorStand.getLocation().distanceSquared(player.getLocation()) <= 2 || !(player.getLocation().distanceSquared(armorStand.getLocation()) <= 50 * 50)) { // Проверка, если расстояние меньше или равно квадрату 1 блока
                            task2[0].cancel();
                            task3[0].cancel();
                            armorStand.remove();
                            return;
                        }
                    }, 0, 1);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        task3[0].cancel();
                        task2[0].cancel();
                        armorStand.remove();
                    }, 60);
                    task[0].cancel();
                    return;
                }
            }

            for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 4) {
                for (double phi = 0; phi <= Math.PI; phi += Math.PI / 4) {
                    double particleX = 0.3 * Math.sin(phi) * Math.cos(theta);
                    double particleY = 0.3 * Math.cos(phi);
                    double particleZ = 0.3 * Math.sin(phi) * Math.sin(theta);

                    Vector particleDirection = new Vector(particleX, particleY, particleZ);
                    spawnParticleAroundArmorStand(armorStand, particleDirection);
                }
            }
        }, 0, 1);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            task[0].cancel();
            if (task3[0] == null) {
                armorStand.remove();
            }
        }, 30);
    }

    private void spawnParticleAroundArmorStand(ArmorStand armorStand, Vector particleDirection) {
        Location particleLocation = armorStand.getLocation().clone().add(particleDirection);
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(0, 51, 102), 1);
        armorStand.getWorld().spawnParticle(Particle.REDSTONE, particleLocation, 1, 0, 0, 0, 0, dustOptions);
    }


    private void moveArmorStand(ArmorStand armorStand, Vector direction) {
        Location location = armorStand.getLocation();
        location.add(direction);
        armorStand.teleport(location);
    }
}