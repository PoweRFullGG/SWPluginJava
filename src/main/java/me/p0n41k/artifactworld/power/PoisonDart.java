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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;


public class PoisonDart implements Listener {
    private final JavaPlugin plugin;
    private final ArtifactMenu artifactMenu;
    private final int poisonDartCooldownTime = 4;
    private static final int poisonDartBigCooldownTime = 15;
    private static final int manauseg = 30;

    private final int cmduseg = 120;
    private static final CooldownManager poisonDartCooldownManager = new CooldownManager();

    public PoisonDart(JavaPlugin plugin) {
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

    private final Map<String, Integer> leapCounts = new HashMap<>();

    public void resetLeapCount(Player player) {
        leapCounts.put(player.getName(), 3);
    }

    public void incrementLeapCount(Player player, Integer volume) {
        int count = leapCounts.getOrDefault(player.getName(), 3);
        leapCounts.put(player.getName(), count + volume);
    }

    public int getLeapCount(Player player) {
        return leapCounts.getOrDefault(player.getName(), 3);
    }

    public static void resetAllCooldownsPoisonDart(Player player) {
        poisonDartCooldownManager.resetCooldown(player.getName());
    }

    public static ItemStack createPoisonDartItem() {
        ItemStack s18 = new ItemStack(Material.DIAMOND_HORSE_ARMOR);
        ItemMeta s18meta = s18.getItemMeta();
        s18meta.setDisplayName(ChatColor.YELLOW + "Дротик с Ядом");
        s18meta.setCustomModelData(120);
        List<String> lores18 = new ArrayList<>();
        lores18.add(ChatColor.BLUE + "   Выпускает дротик, отравляющий противника");
        lores18.add(ChatColor.BLUE + "   в зависимости от дальности полета.");
        lores18.add("");
        lores18.add(ChatColor.AQUA + "   Мана: " + ChatColor.GRAY + manauseg);
        lores18.add(ChatColor.RED + "   Перезарядка: " + ChatColor.GRAY + poisonDartBigCooldownTime);
        lores18.add("");
        s18meta.setLore(lores18);
        s18.setItemMeta(s18meta);
        return s18;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        Integer hasArtifact = artifactMenu.checkArtifactMenu(player, cmduseg);
        if (hasArtifact == null) {
            sendActionBarMessage(player, " ");
            return;
        }

        int getspellgerk = getLeapCount(player);
        ManaMechanics manaMechanics = new ManaMechanics(player.getUniqueId());
        int secondManaUse = manaMechanics.getSecondMana();
        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK) || (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !poisonDartCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    if (getspellgerk > 1) {
                        event.setCancelled(true);
                        incrementLeapCount(player, -1);
                        handlePoisonDart(player);
                        manaMechanics.addSecondMana(-manauseg);
                        poisonDartCooldownManager.setCooldown(player.getName(), poisonDartCooldownTime);
                    } else if (getspellgerk == 1) {
                        event.setCancelled(true);
                        handlePoisonDart(player);
                        manaMechanics.addSecondMana(-manauseg);
                        poisonDartCooldownManager.setCooldown(player.getName(), poisonDartBigCooldownTime);
                        resetLeapCount(player);
                    }
                }
                else {
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

        int getspellgerk = getLeapCount(player);
        ManaMechanics manaMechanics = new ManaMechanics(player.getUniqueId());
        int secondManaUse = manaMechanics.getSecondMana();
        if (event.getHand().equals(EquipmentSlot.HAND)) {
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !poisonDartCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    if (getspellgerk > 1) {
                        event.setCancelled(true);
                        incrementLeapCount(player, -1);
                        handlePoisonDart(player);
                        manaMechanics.addSecondMana(-manauseg);
                        poisonDartCooldownManager.setCooldown(player.getName(), poisonDartCooldownTime);
                    } else if (getspellgerk == 1) {
                        event.setCancelled(true);
                        handlePoisonDart(player);
                        manaMechanics.addSecondMana(-manauseg);
                        poisonDartCooldownManager.setCooldown(player.getName(), poisonDartBigCooldownTime);
                        resetLeapCount(player);
                    }
                }
                else {
                    NoManaPlayer.displayNoManaMessage(player);
                }
            }
        } else if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !poisonDartCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    if (getspellgerk > 1) {
                        event.setCancelled(true);
                        incrementLeapCount(player, -1);
                        handlePoisonDart(player);
                        manaMechanics.addSecondMana(-manauseg);
                        poisonDartCooldownManager.setCooldown(player.getName(), poisonDartCooldownTime);
                    } else if (getspellgerk == 1) {
                        event.setCancelled(true);
                        handlePoisonDart(player);
                        manaMechanics.addSecondMana(-manauseg);
                        poisonDartCooldownManager.setCooldown(player.getName(), poisonDartBigCooldownTime);
                        resetLeapCount(player);
                    }
                }
                else {
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

        int getspellgerk = getLeapCount(player);
        ManaMechanics manaMechanics = new ManaMechanics(player.getUniqueId());
        int secondManaUse = manaMechanics.getSecondMana();
        int heldItemSlot = player.getInventory().getHeldItemSlot();
        if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !poisonDartCooldownManager.hasCooldown(player.getName())) {
            if (Silence.getSilenceState(player)>= 1) {
                event.setCancelled(true);
                NoManaPlayer.displaySilenceMessage(player);
                return;
            }
            if (secondManaUse >= manauseg) {
                if (getspellgerk > 1) {
                    event.setCancelled(true);
                    incrementLeapCount(player, -1);
                    handlePoisonDart(player);
                    manaMechanics.addSecondMana(-manauseg);
                    poisonDartCooldownManager.setCooldown(player.getName(), poisonDartCooldownTime);
                } else if (getspellgerk == 1) {
                    event.setCancelled(true);
                    handlePoisonDart(player);
                    manaMechanics.addSecondMana(-manauseg);
                    poisonDartCooldownManager.setCooldown(player.getName(), poisonDartBigCooldownTime);
                    resetLeapCount(player);
                }
            }
            else {
                NoManaPlayer.displayNoManaMessage(player);
            }
        }
    }


    public void checkSlot() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {

            String actionBarMessage = "";

            Integer hasArtifact = artifactMenu.checkArtifactMenu(onlinePlayer, cmduseg);
            List<Integer> getspellslots =  artifactMenu.getDiamondHorseArmorSlots(onlinePlayer);

            if (hasArtifact == null) {
                continue;
            }

            int heldItemSlot = onlinePlayer.getInventory().getHeldItemSlot();
            if (heldItemSlot == hasArtifact-9) {
                int getPoisonDart = getLeapCount(onlinePlayer);
                if (poisonDartCooldownManager.hasCooldown(onlinePlayer.getName())) {
                    int remainingTime = (int) ((poisonDartCooldownManager.cooldowns.get(onlinePlayer.getName()) - System.currentTimeMillis()) / 1000);
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + ""  + manauseg + ChatColor.GRAY + " | " + ChatColor.RED + "Дротик с ядом " + "[" + getPoisonDart + "]" + ChatColor.GRAY + " | КД: " + remainingTime + " сек.");
                } else {
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + "" + manauseg + ChatColor.GRAY + " | " + ChatColor.YELLOW + "Дротик с ядом " + "[" + getPoisonDart + "]" + ChatColor.GRAY + " |");
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


    private void handlePoisonDart(Player player) {
        Location playerLocation = player.getLocation();

        Location armorStandLocation = new Location(playerLocation.getWorld(), playerLocation.getX(), playerLocation.getY() + 1.1, playerLocation.getZ());
        ArmorStand armorStand = player.getWorld().spawn(armorStandLocation, ArmorStand.class, entity -> {
            entity.setGravity(false);
            entity.setVisible(false);
            entity.setInvulnerable(true);
            entity.setMarker(true); // Делаем стойку невидимой для игроков
            entity.setAI(false); // Отключаем ИИ, чтобы стойка не могла ходить
            entity.setCanPickupItems(false); // Запрещаем стойке поднимать предметы
            entity.addScoreboardTag("unmovable"); // Добавляем тег, чтобы позже отслеживать и блокировать взаимодействие игроков с этой стойкой
            entity.setSmall(true);
        });
        SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 0.7f, 1.0f, 5);

        float yaw = player.getLocation().getYaw();
        float pitch = player.getLocation().getPitch();

        double yawAngle = Math.toRadians(yaw);
        double pitchAngle = Math.toRadians(pitch);

        double x = -Math.sin(yawAngle) * Math.cos(pitchAngle);
        double y = -Math.sin(pitchAngle);
        double z = Math.cos(yawAngle) * Math.cos(pitchAngle);

        Vector direction = new Vector(x, y, z).normalize().multiply(1.1);

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

            for (Entity nearbyEntity : armorStand.getNearbyEntities(0.2, 0.2, 0.2)) {
                if (nearbyEntity instanceof LivingEntity && !nearbyEntity.equals(owner)) {
                    LivingEntity livingEntity = (LivingEntity) nearbyEntity;
                    Location livingEntityLocation = livingEntity.getLocation();
                    int distance = (int) playerLocation.distance(livingEntityLocation);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 40, 1, false, false));
                    }, 3*20);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 40, 1, false, false));
                    }, 5*20);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, ((distance + 2) * 20), 2, false, false));
                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, ((distance/2 + 2) * 20), 2, false, false));
                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 5 * 20, 3, false, false));
                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 5*20, 2, false, false));
                    }, 7*20);
                    armorStand.remove();
                    taskReference.get().cancel();
                    return;
                }
            }

            for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 2) {
                for (double phi = 0; phi <= Math.PI; phi += Math.PI / 2) {
                    double particleX = 0.1 * Math.sin(phi) * Math.cos(theta);
                    double particleY = 0.1 * Math.cos(phi);
                    double particleZ = 0.1 * Math.sin(phi) * Math.sin(theta);

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
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(0, 153, 0), 1);
        armorStand.getWorld().spawnParticle(Particle.REDSTONE, particleLocation, 1, 0, 0, 0, 0, dustOptions);
    }


    private void moveArmorStand(ArmorStand armorStand, Vector direction) {
        Location location = armorStand.getLocation();
        location.add(direction);

        armorStand.teleport(location);
    }
}