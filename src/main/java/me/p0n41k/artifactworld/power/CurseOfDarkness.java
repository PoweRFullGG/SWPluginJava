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
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;


public class CurseOfDarkness implements Listener {
    private final JavaPlugin plugin;
    private final ArtifactMenu artifactMenu;
    private static final int curseOfDarknessCooldownTime = 30;
    private static final int manauseg = 60;
    private final int cmduseg = 124;
    private static final CooldownManager curseOfDarknessCooldownManager = new CooldownManager();

    public CurseOfDarkness(JavaPlugin plugin) {
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

    public static void resetAllCooldownsCurseOfDarkness(Player player) {
        curseOfDarknessCooldownManager.resetCooldown(player.getName());
    }

    public static ItemStack createCurseOfDarknessItem() {
        ItemStack s22 = new ItemStack(Material.DIAMOND_HORSE_ARMOR);
        ItemMeta s22meta = s22.getItemMeta();
        s22meta.setDisplayName(ChatColor.YELLOW + "Проклятие Тьмы");
        s22meta.setCustomModelData(124);
        List<String> lores22 = new ArrayList<>();
        lores22.add(ChatColor.BLUE + "   Ставит метку на противника, периодически");
        lores22.add(ChatColor.BLUE + "   оглушая его на короткое время.");
        lores22.add("");
        lores22.add(ChatColor.AQUA + "   Мана: " + ChatColor.GRAY + manauseg);
        lores22.add(ChatColor.RED + "   Перезарядка: " + ChatColor.GRAY + curseOfDarknessCooldownTime);
        lores22.add("");
        s22meta.setLore(lores22);
        s22.setItemMeta(s22meta);
        return s22;
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
            if (player.getInventory().getHeldItemSlot() == hasArtifact - 9 && !curseOfDarknessCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    curseOfDarknessCooldownManager.setCooldown(player.getName(), curseOfDarknessCooldownTime);
                    event.setCancelled(true);
                    handleCurseOfDarkness(player);
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
            if (player.getInventory().getHeldItemSlot() == hasArtifact - 9 && !curseOfDarknessCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    curseOfDarknessCooldownManager.setCooldown(player.getName(), curseOfDarknessCooldownTime);
                    event.setCancelled(true);
                    handleCurseOfDarkness(player);
                    manaMechanics.addSecondMana(-manauseg);
                } else {
                    NoManaPlayer.displayNoManaMessage(player);
                }
            }
        } else if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
            if (player.getInventory().getHeldItemSlot() == hasArtifact - 9 && !curseOfDarknessCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    curseOfDarknessCooldownManager.setCooldown(player.getName(), curseOfDarknessCooldownTime);
                    event.setCancelled(true);
                    handleCurseOfDarkness(player);
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
        if (player.getInventory().getHeldItemSlot() == hasArtifact - 9 && !curseOfDarknessCooldownManager.hasCooldown(player.getName())) {
            if (Silence.getSilenceState(player)>= 1) {
                event.setCancelled(true);
                NoManaPlayer.displaySilenceMessage(player);
                return;
            }
            if (secondManaUse >= manauseg) {
                curseOfDarknessCooldownManager.setCooldown(player.getName(), curseOfDarknessCooldownTime);
                event.setCancelled(true);
                handleCurseOfDarkness(player);
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
                if (curseOfDarknessCooldownManager.hasCooldown(onlinePlayer.getName())) {
                    int remainingTime = (int) ((curseOfDarknessCooldownManager.cooldowns.get(onlinePlayer.getName()) - System.currentTimeMillis()) / 1000);
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + "" + manauseg + ChatColor.GRAY + " | " + ChatColor.RED + "Проклятие тьмы" + ChatColor.GRAY + " | КД: " + remainingTime + " сек.");
                } else {
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + "" + manauseg + ChatColor.GRAY + " | " + ChatColor.YELLOW + "Проклятие тьмы" + ChatColor.GRAY + " |");
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

    private void handleCurseOfDarkness(Player player) {
        final BukkitTask[] taskHD = new BukkitTask[1];
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
            entity.setSmall(true);
        });
        SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_SHULKER_SHOOT, 0.7f, 0.8f, 10);

        float yaw = player.getLocation().getYaw();
        float pitch = player.getLocation().getPitch();

        double yawAngle = Math.toRadians(yaw);
        double pitchAngle = Math.toRadians(pitch);

        double x = -Math.sin(yawAngle) * Math.cos(pitchAngle);
        double y = -Math.sin(pitchAngle);
        double z = Math.cos(yawAngle) * Math.cos(pitchAngle);

        Vector direction = new Vector(x, y, z).normalize().multiply(1.4);

        Player owner = player;

        AtomicReference<BukkitTask> taskReference = new AtomicReference<>();

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            moveArmorStand(armorStand, direction);

            Block block = armorStand.getLocation().getBlock();

            if (!isNonObstacleBlock(block)) {
                armorStand.remove();
                taskReference.get().cancel();
                return;
            }

            for (Entity nearbyEntity : armorStand.getNearbyEntities(0.3, 0.2, 0.3)) {
                if (nearbyEntity instanceof LivingEntity && !nearbyEntity.equals(owner)) {
                    LivingEntity livingEntity = (LivingEntity) nearbyEntity;
                    SoundUtil.playSoundForNearbyPlayers(livingEntity.getLocation(), Sound.BLOCK_BELL_USE, 0.5f, 1.4f, 10);
                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 1, false, false));
                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 2*20, 1, false, false));
                    livingEntity.damage(0.5);
                    stunCOD(livingEntity);
                    taskHD[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                        for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 2) {
                            for (double phi = 0; phi <= Math.PI; phi += Math.PI / 2) {
                                double particleX = 0.4 * Math.sin(phi) * Math.cos(theta);
                                double particleY = 0.1 * Math.cos(phi);
                                double particleZ = 0.4 * Math.sin(phi) * Math.sin(theta);

                                Vector particleDirection = new Vector(particleX, particleY, particleZ);
                                spawnParticleAroundLivingEntity(livingEntity, particleDirection);
                            }
                        }
                    }, 0, 2);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        livingEntity.damage(0.5);
                        SoundUtil.playSoundForNearbyPlayers(livingEntity.getLocation(), Sound.BLOCK_BELL_USE, 0.5f, 1.4f, 10);
                        stunCOD(livingEntity);
                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 2*20, 1, false, false));
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            livingEntity.damage(0.5);
                            SoundUtil.playSoundForNearbyPlayers(livingEntity.getLocation(), Sound.BLOCK_BELL_USE, 0.5f, 1.4f, 10);
                            stunCOD(livingEntity);
                            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 2*20, 1, false, false));
                            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                livingEntity.damage(0.5);
                                SoundUtil.playSoundForNearbyPlayers(livingEntity.getLocation(), Sound.BLOCK_BELL_USE, 0.5f, 1.4f, 10);
                                stunCOD(livingEntity);
                                livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 2*20, 1, false, false));
                                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                    livingEntity.damage(0.5);
                                    SoundUtil.playSoundForNearbyPlayers(livingEntity.getLocation(), Sound.BLOCK_BELL_USE, 0.5f, 1.4f, 10);
                                    stunCOD(livingEntity);
                                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 2*20, 1, false, false));
                                }, 20*4);
                            }, 20*4);
                        }, 20*4);
                    }, 20*4);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        taskHD[0].cancel();
                    }, 20*17);
                    armorStand.remove();
                    taskReference.get().cancel();
                    return;
                }
            }

            // Создаем шар частиц вокруг стойки для брони
            for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 2) {
                for (double phi = 0; phi <= Math.PI; phi += Math.PI / 2) {
                    double particleX = 0.4 * Math.sin(phi) * Math.cos(theta);
                    double particleY = 0.1 * Math.cos(phi);
                    double particleZ = 0.4 * Math.sin(phi) * Math.sin(theta);

                    Vector particleDirection = new Vector(particleX, particleY, particleZ);
                    spawnParticleAroundArmorStand(armorStand, particleDirection);
                }
            }
        }, 0, 1);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            armorStand.remove();
            task.cancel();
        }, 25);

        taskReference.set(task);
    }

    private void stunCOD(Entity entity) {
        if (entity instanceof Player) {
            final BukkitTask[] taskend = new BukkitTask[1];
            final BukkitTask[] taskId = new BukkitTask[1];
            Player player = (Player) entity;
            Location playerLocation = player.getLocation();
            player.setWalkSpeed(0);
            player.setFlySpeed(0);

            Listener moveListener = new Listener() {
                @EventHandler
                public void onPlayerMove(PlayerMoveEvent moveEvent) {
                    if (moveEvent.getPlayer().equals(player)) {
                        moveEvent.setCancelled(true);
                    }
                }
            };

            Bukkit.getPluginManager().registerEvents(moveListener, plugin);

            taskId[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                if (player.isDead()){
                    player.setWalkSpeed(0.2f);
                    player.setFlySpeed(0.1f);
                    HandlerList.unregisterAll(moveListener);
                    taskend[0].cancel();
                    taskId[0].cancel();
                } else {
                    double radius = 0.5;
                    double angle = Math.random() * 2 * Math.PI;

                    double x = player.getLocation().getX() + radius * Math.cos(angle);
                    double y = player.getLocation().getY() + Math.random() * 2;
                    double z = player.getLocation().getZ() + radius * Math.sin(angle);

                    player.getWorld().spawnParticle(Particle.REDSTONE, new Location(player.getWorld(), x, y, z), 3,
                            new Particle.DustOptions(Color.fromRGB(51, 0, 102), 1));

                    Location belowLocation = playerLocation.clone().subtract(0, 0, 0);
                    Block block = (Block) playerLocation.clone().subtract(0, 0, 0);
                    if (player.getWorld().getBlockAt(belowLocation).getType() == Material.AIR) {
                        playerLocation.subtract(0, 0.25, 0);
                        player.teleport(playerLocation);
                    } else {
                        Location playerlocationnew = player.getLocation();
                        player.teleport(playerlocationnew);
                    }
                }

            }, 0L, 1L); // 1 тик

            taskend[0] = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Location playerlocationnew = player.getLocation();
                player.teleport(playerlocationnew.add(0,0.3,0));
                player.setWalkSpeed(0.2f);
                player.setFlySpeed(0.1f);
                HandlerList.unregisterAll(moveListener);
                taskId[0].cancel();
            }, 20);

        } else if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            livingEntity.setAI(false);

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                livingEntity.setAI(true);
            }, 20);
        }
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

    private void spawnParticleAroundArmorStand(ArmorStand armorStand, Vector particleDirection) {
        Location particleLocation = armorStand.getLocation().clone().add(particleDirection);
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(51, 0, 102), 1);
        armorStand.getWorld().spawnParticle(Particle.REDSTONE, particleLocation, 1, 0, 0, 0, 0, dustOptions);
    }

    private void spawnParticleAroundLivingEntity(LivingEntity livingEntity, Vector particleDirection) {
        Location particleLocation = livingEntity.getLocation().add(0,2.2,0).clone().add(particleDirection);
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(51, 0, 102), 1);
        livingEntity.getWorld().spawnParticle(Particle.REDSTONE, particleLocation, 1, 0, 0, 0, 0, dustOptions);
    }


    private void moveArmorStand(ArmorStand armorStand, Vector direction) {
        Location location = armorStand.getLocation();
        location.add(direction);

        armorStand.teleport(location);
    }
}