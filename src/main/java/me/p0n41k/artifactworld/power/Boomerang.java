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
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import me.p0n41k.artifactworld.data.ManaMechanics;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;


public class Boomerang implements Listener {
    private final JavaPlugin plugin;
    private final ArtifactMenu artifactMenu;
    private static final int boomerangCooldownTime = 15;

    private final int boomerangCooldownTimeSmall = 1;
    private static final int manauseg = 25;
    private final int cmduseg = 125;
    private static final CooldownManager boomerangCooldownManager = new CooldownManager();

    public Boomerang(JavaPlugin plugin) {
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

    public static void resetAllCooldownsBoomerang(Player player) {
        boomerangCooldownManager.resetCooldown(player.getName());
    }

    public static ItemStack createBoomerangItem() {
        ItemStack s23 = new ItemStack(Material.DIAMOND_HORSE_ARMOR);
        ItemMeta s23meta = s23.getItemMeta();
        s23meta.setDisplayName(ChatColor.YELLOW + "Сюрикен");
        s23meta.setCustomModelData(125);
        List<String> lores23 = new ArrayList<>();
        lores23.add(ChatColor.BLUE + "   Выпускает острый сюрикен, возвращающийся по");
        lores23.add(ChatColor.BLUE + "   траектории полета и оставляющий кровотечение");
        lores23.add(ChatColor.BLUE + "   всем, кто оказался на его пути.");
        lores23.add("");
        lores23.add(ChatColor.AQUA + "   Мана: " + ChatColor.GRAY + manauseg);
        lores23.add(ChatColor.RED + "   Перезарядка: " + ChatColor.GRAY + boomerangCooldownTime);
        lores23.add("");
        s23meta.setLore(lores23);
        s23.setItemMeta(s23meta);
        return s23;
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
            if (player.getInventory().getHeldItemSlot() == hasArtifact - 9 && !boomerangCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    boomerangCooldownManager.setCooldown(player.getName(), boomerangCooldownTime);
                    event.setCancelled(true);
                    handleBoomerang(player);
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
            if (player.getInventory().getHeldItemSlot() == hasArtifact - 9 && !boomerangCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    boomerangCooldownManager.setCooldown(player.getName(), boomerangCooldownTime);
                    event.setCancelled(true);
                    handleBoomerang(player);
                    manaMechanics.addSecondMana(-manauseg);
                } else {
                    NoManaPlayer.displayNoManaMessage(player);
                }
            }
        } else if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
            if (player.getInventory().getHeldItemSlot() == hasArtifact - 9 && !boomerangCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    boomerangCooldownManager.setCooldown(player.getName(), boomerangCooldownTime);
                    event.setCancelled(true);
                    handleBoomerang(player);
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
        if (player.getInventory().getHeldItemSlot() == hasArtifact - 9 && !boomerangCooldownManager.hasCooldown(player.getName())) {
            if (Silence.getSilenceState(player)>= 1) {
                event.setCancelled(true);
                NoManaPlayer.displaySilenceMessage(player);
                return;
            }
            if (secondManaUse >= manauseg) {
                boomerangCooldownManager.setCooldown(player.getName(), boomerangCooldownTime);
                event.setCancelled(true);
                handleBoomerang(player);
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
                if (boomerangCooldownManager.hasCooldown(onlinePlayer.getName())) {
                    int remainingTime = (int) ((boomerangCooldownManager.cooldowns.get(onlinePlayer.getName()) - System.currentTimeMillis()) / 1000);
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + "" + manauseg + ChatColor.GRAY + " | " + ChatColor.RED + "Сюрикен" + ChatColor.GRAY + " | КД: " + remainingTime + " сек.");
                } else {
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + "" + manauseg + ChatColor.GRAY + " | " + ChatColor.YELLOW + "Сюрикен" + ChatColor.GRAY + " |");
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

    private void handleBoomerang(Player player) {
        World world = player.getWorld();
        final BukkitTask[] task2 = new BukkitTask[1];
        final BukkitTask[] taskTimer = new BukkitTask[1];
        Location playerLocation = player.getLocation();

        Location armorStandLocation = new Location(playerLocation.getWorld(), playerLocation.getX(), playerLocation.getY() + 1.1, playerLocation.getZ());
        ArmorStand armorStand = player.getWorld().spawn(armorStandLocation, ArmorStand.class, entity -> {
            entity.setGravity(false);
            entity.setVisible(false);
            entity.setInvulnerable(true);
            entity.setMarker(true);
            entity.setAI(false);
            entity.setCanPickupItems(false);
            entity.addScoreboardTag("unmovable");
            entity.setSmall(false);
            entity.setArms(true);
        });

        ItemStack diamondItem = new ItemStack(Material.DIAMOND_HORSE_ARMOR);
        ItemMeta itemMeta = diamondItem.getItemMeta();
        itemMeta.setCustomModelData(50);
        diamondItem.setItemMeta(itemMeta);
        armorStand.getEquipment().setItemInMainHand(diamondItem);

        SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ITEM_TRIDENT_THROW, 0.7f, 0.8f, 12);

        Vector direction = player.getLocation().getDirection().normalize().multiply(1.1);

        AtomicBoolean hasHitEntityOrBlock = new AtomicBoolean(false);
        Player owner = player;
        AtomicReference<BukkitTask> taskReference = new AtomicReference<>();

        armorStand.setRightArmPose(new EulerAngle(0, 0, 0));
        armorStand.setLeftArmPose(new EulerAngle(0, 0, 0));

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            moveArmorStand(armorStand, direction);

            EulerAngle currentPose = armorStand.getRightArmPose();
            armorStand.setRightArmPose(currentPose.add(0, Math.toRadians(15), 0));

            armorStand.setRotation(armorStand.getLocation().getYaw(), 0);

            for (Entity nearbyEntity : armorStand.getNearbyEntities(0.3, 0.2, 0.3)) {
                if (nearbyEntity instanceof LivingEntity && !nearbyEntity.equals(owner)) {
                    LivingEntity livingEntity = (LivingEntity) nearbyEntity;
                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 8, 1, false, false));
                    livingEntity.damage(1);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        if (livingEntity instanceof Player) {
                            for (int i = 0; i < 15; i++) {
                                double offsetX = Math.random() * 0.6 - 0.3;
                                double offsetY = Math.random() * 0.6 - 0.3;
                                double offsetZ = Math.random() * 0.6 - 0.3;

                                world.spawnParticle(Particle.BLOCK_CRACK, livingEntity.getLocation().add(0, 1, 0), 1, offsetX, offsetY, offsetZ, 0, Material.REDSTONE_BLOCK.createBlockData());
                            }
                        }
                        livingEntity.damage(0.5);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            if (livingEntity instanceof Player) {
                                for (int i = 0; i < 15; i++) {
                                    double offsetX = Math.random() * 0.6 - 0.3;
                                    double offsetY = Math.random() * 0.6 - 0.3;
                                    double offsetZ = Math.random() * 0.6 - 0.3;

                                    world.spawnParticle(Particle.BLOCK_CRACK, livingEntity.getLocation().add(0, 1, 0), 1, offsetX, offsetY, offsetZ, 0, Material.REDSTONE_BLOCK.createBlockData());
                                }
                            }
                            livingEntity.damage(0.5);
                            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                if (livingEntity instanceof Player) {
                                    for (int i = 0; i < 15; i++) {
                                        double offsetX = Math.random() * 0.6 - 0.3;
                                        double offsetY = Math.random() * 0.6 - 0.3;
                                        double offsetZ = Math.random() * 0.6 - 0.3;

                                        world.spawnParticle(Particle.BLOCK_CRACK, livingEntity.getLocation().add(0, 1, 0), 1, offsetX, offsetY, offsetZ, 0, Material.REDSTONE_BLOCK.createBlockData());
                                    }
                                }
                                livingEntity.damage(0.1);
                                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                    if (livingEntity instanceof Player) {
                                        for (int i = 0; i < 15; i++) {
                                            double offsetX = Math.random() * 0.6 - 0.3;
                                            double offsetY = Math.random() * 0.6 - 0.3;
                                            double offsetZ = Math.random() * 0.6 - 0.3;

                                            world.spawnParticle(Particle.BLOCK_CRACK, livingEntity.getLocation().add(0, 1, 0), 1, offsetX, offsetY, offsetZ, 0, Material.REDSTONE_BLOCK.createBlockData());
                                        }
                                    }
                                    livingEntity.damage(0.1);
                                }, 30);
                            }, 30);
                        }, 30);
                    }, 30);
                    livingEntity.setNoDamageTicks(2);
                }
            }

            Block block = armorStand.getLocation().getBlock();
            if (!isNonObstacleBlock(block)) {
                playParticleEffect(block);
                SoundUtil.playSoundForNearbyPlayers(armorStand.getLocation(), Sound.BLOCK_WOOD_BREAK, 0.7f, 1.2f, 20);
                direction.multiply(-1);
                hasHitEntityOrBlock.set(true);
            }

        }, 0, 1);

        AtomicBoolean hasExecuted = new AtomicBoolean(false);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            task2[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                for (Entity nearbyEntity : armorStand.getNearbyEntities(0.5, 0.5, 0.5)) {
                    if (nearbyEntity instanceof LivingEntity && nearbyEntity.equals(owner) && !hasExecuted.get()) {
                        hasExecuted.set(true);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            task.cancel();
                            taskTimer[0].cancel();
                            armorStand.remove();
                            taskReference.get().cancel();
                            boomerangCooldownManager.setCooldown(player.getName(), boomerangCooldownTimeSmall);
                            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.7f, 0.5f, 10);
                            task2[0].cancel();
                        }, 3);
                    }
                }
            }, 0, 1);
        }, 5);


        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!hasHitEntityOrBlock.get()) {
                direction.multiply(-1);
            }
        }, 17);

        taskTimer[0] = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            task.cancel();
            task2[0].cancel();
            armorStand.remove();
            taskReference.get().cancel();
            taskTimer[0].cancel();
        }, 40);

        taskReference.set(task);
    }

    private void playParticleEffect(Block block) {
        block.getWorld().spawnParticle(Particle.REDSTONE, block.getLocation(), 20, 0.4, 0.4, 0.4, 0.7, new Particle.DustOptions(Color.GRAY, 1));
        block.getWorld().spawnParticle(Particle.BLOCK_DUST, block.getLocation(), 40, 0.6, 0.6, 0.6, 0.7, block.getBlockData());
    }

    private boolean isNonObstacleBlock(Block block) {
        Material material = block.getType();

        return material == Material.LADDER ||
                material == Material.AIR ||
                material == Material.VINE ||
                material == Material.SHORT_GRASS ||
                material == Material.FERN ||
                material == Material.LARGE_FERN ||
                material == Material.DEAD_BUSH ||
                material == Material.SNOW ||
                material == Material.SUGAR_CANE ||
                material == Material.COBWEB ||
                material == Material.WATER ||
                material == Material.LAVA ||
                material == Material.TWISTING_VINES ||
                material == Material.WEEPING_VINES ||
                material == Material.SEAGRASS ||
                material == Material.TALL_SEAGRASS ||
                material == Material.KELP_PLANT ||
                material == Material.BAMBOO_SAPLING ||
                material == Material.BAMBOO ||
                material == Material.SCAFFOLDING ||
                material == Material.CAVE_VINES ||
                material == Material.CAVE_VINES_PLANT ||
                material == Material.GLOW_LICHEN ||
                material == Material.AZALEA ||
                material == Material.FLOWERING_AZALEA ||
                material == Material.TALL_GRASS ||
                material == Material.DRIPSTONE_BLOCK;
    }

    private void moveArmorStand(ArmorStand armorStand, Vector direction) {
        Location location = armorStand.getLocation();
        location.add(direction);

        armorStand.teleport(location);
    }
}