package me.p0n41k.artifactworld.power.AdminsPower;

import me.p0n41k.artifactworld.Artifactworld;
import me.p0n41k.artifactworld.MainCommands.CooldownCommands;
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
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;


public class Spit implements Listener {
    private final JavaPlugin plugin;
    private final ArtifactMenu artifactMenu;
    private static final int spitCooldownTime = 2;
    private static final int manauseg = 15;
    private final int cmduseg = 10012;
    private static final CooldownManager spitCooldownManager = new CooldownManager();

    public Spit(JavaPlugin plugin) {
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

    public static void resetAllCooldownsSpit(Player player) {
        spitCooldownManager.resetCooldown(player.getName());
    }

    public static ItemStack createSpitItem() {
        ItemStack s12a = new ItemStack(Material.DIAMOND_HORSE_ARMOR);
        ItemMeta s12ameta = s12a.getItemMeta();
        s12ameta.setDisplayName(ChatColor.DARK_RED + "Харча");
        s12ameta.setCustomModelData(10012);
        List<String> lores12a = new ArrayList<>();
        lores12a.add(ChatColor.BLUE + "   При использовании заклинания игрок");
        lores12a.add(ChatColor.BLUE + "   Смокует во рту смесь соплей и слюны");
        lores12a.add(ChatColor.BLUE + "   Образуя харчу");
        lores12a.add(ChatColor.BLUE + "   Которая замедлит жертву на столько сильно ");
        lores12a.add(ChatColor.BLUE + "   На сколько много будет поражена харчёй");
        lores12a.add("");
        lores12a.add(ChatColor.AQUA + "   Мана: " + ChatColor.GRAY + manauseg);
        lores12a.add(ChatColor.RED + "   Перезарядка: " + ChatColor.GRAY + spitCooldownTime);
        lores12a.add("");
        s12ameta.setLore(lores12a);
        s12a.setItemMeta(s12ameta);
        return s12a;
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
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !spitCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    spitCooldownManager.setCooldown(player.getName(), spitCooldownTime);
                    event.setCancelled(true);
                    handleSpit(player);
                    manaMechanics.addSecondMana(-manauseg);
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

        ManaMechanics manaMechanics = new ManaMechanics(player.getUniqueId());
        int secondManaUse = manaMechanics.getSecondMana();
        if (event.getHand().equals(EquipmentSlot.HAND)) {
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !spitCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    spitCooldownManager.setCooldown(player.getName(), spitCooldownTime);
                    event.setCancelled(true);
                    handleSpit(player);
                    manaMechanics.addSecondMana(-manauseg);
                }
                else {
                    NoManaPlayer.displayNoManaMessage(player);
                }
            }
        } else if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !spitCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    spitCooldownManager.setCooldown(player.getName(), spitCooldownTime);
                    event.setCancelled(true);
                    handleSpit(player);
                    manaMechanics.addSecondMana(-manauseg);
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

        ManaMechanics manaMechanics = new ManaMechanics(player.getUniqueId());
        int secondManaUse = manaMechanics.getSecondMana();
        if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !spitCooldownManager.hasCooldown(player.getName())) {
            if (Silence.getSilenceState(player)>= 1) {
                event.setCancelled(true);
                NoManaPlayer.displaySilenceMessage(player);
                return;
            }
            if (secondManaUse >= manauseg) {
                spitCooldownManager.setCooldown(player.getName(), spitCooldownTime);
                event.setCancelled(true);
                handleSpit(player);
                manaMechanics.addSecondMana(-manauseg);
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
                if (spitCooldownManager.hasCooldown(onlinePlayer.getName())) {
                    int remainingTime = (int) ((spitCooldownManager.cooldowns.get(onlinePlayer.getName()) - System.currentTimeMillis()) / 1000);
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + ""  + manauseg + ChatColor.GRAY + " | " + ChatColor.RED + "Харча" + ChatColor.GRAY + " | КД: " + remainingTime + " сек.");
                } else {
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + "" + manauseg + ChatColor.GRAY + " | " + ChatColor.YELLOW + "Харча" + ChatColor.GRAY + " |");
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

    private void handleSpit(Player player) {
        final BukkitTask[] taskPar = new BukkitTask[1];
        SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_LLAMA_SPIT, 0.7f, 0.8f, 10);
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

        float yaw = player.getLocation().getYaw();
        float pitch = player.getLocation().getPitch();

        double yawAngle = Math.toRadians(yaw);
        double pitchAngle = Math.toRadians(pitch);

        double x = -Math.sin(yawAngle) * Math.cos(pitchAngle);
        double y = -Math.sin(pitchAngle);
        double z = Math.cos(yawAngle) * Math.cos(pitchAngle);

        Vector direction = new Vector(x, y, z).normalize().multiply(1.2);

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

            Map<Player, BukkitTask> particleTasks = new HashMap<>();

            for (Entity nearbyEntity : armorStand.getNearbyEntities(0.3, 0.3, 0.3)) {
                if (nearbyEntity instanceof LivingEntity && !nearbyEntity.equals(owner)) {
                    LivingEntity livingEntity = (LivingEntity) nearbyEntity;
                    SoundUtil.playSoundForNearbyPlayers(livingEntity.getLocation(), Sound.ENTITY_SLIME_JUMP, 0.7f, 1.2f, 10);

                    PotionEffect currentSlow = livingEntity.getPotionEffect(PotionEffectType.SLOW);
                    if (currentSlow != null) {
                        int newDuration = currentSlow.getDuration() + 20 * 8;
                        int newAmplifier = Math.min(currentSlow.getAmplifier() + 1, 4);
                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, newDuration, newAmplifier, false, false));
                    } else {
                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 8, 1, false, false));
                    }

                    livingEntity.damage(0.1);

                    if (livingEntity instanceof Player) {
                        Player slowedPlayer = (Player) livingEntity;

                        if (particleTasks.containsKey(slowedPlayer)) {
                            particleTasks.get(slowedPlayer).cancel();
                        }

                        BukkitTask particleTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                            if (slowedPlayer.hasPotionEffect(PotionEffectType.SLOW)) {
                                double radius = 0.5;
                                double angle = Math.random() * 2 * Math.PI;

                                double x1 = slowedPlayer.getLocation().getX() + radius * Math.cos(angle);
                                double y1 = slowedPlayer.getLocation().getY() + Math.random() * 2;
                                double z1 = slowedPlayer.getLocation().getZ() + radius * Math.sin(angle);

                                slowedPlayer.getWorld().spawnParticle(Particle.REDSTONE, new Location(slowedPlayer.getWorld(), x1, y1, z1), 1,
                                        new Particle.DustOptions(Color.fromRGB(0, 190, 0), 1));
                            } else {
                                particleTasks.get(slowedPlayer).cancel();
                                particleTasks.remove(slowedPlayer);
                            }
                        }, 0, 1);

                        particleTasks.put(slowedPlayer, particleTask);
                    }

                    armorStand.remove();
                    taskReference.get().cancel();
                    return;
                }
            }



            // Создаем шар частиц вокруг стойки для брони
            for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 2) {
                for (double phi = 0; phi <= Math.PI; phi += Math.PI / 2) {
                    double particleX = 0.2 * Math.sin(phi) * Math.cos(theta);
                    double particleY = 0.2 * Math.cos(phi);
                    double particleZ = 0.2 * Math.sin(phi) * Math.sin(theta);

                    Vector particleDirection = new Vector(particleX, particleY, particleZ);
                    spawnParticleAroundArmorStand(armorStand, particleDirection);
                }
            }
        }, 0, 1);

        // Планируем удаление стойки через 3 секунды (60 тиков в секунде, поэтому 3 секунды = 60 * 3 тиков)
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
        armorStand.getWorld().spawnParticle(Particle.SLIME, particleLocation, 1, 0, 0, 0, 0);
    }


    private void moveArmorStand(ArmorStand armorStand, Vector direction) {
        Location location = armorStand.getLocation();
        location.add(direction);

        armorStand.teleport(location);
    }


}