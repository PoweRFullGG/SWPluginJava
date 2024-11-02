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
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import me.p0n41k.artifactworld.data.ManaMechanics;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


public class MagicFireOne implements Listener {
    private final JavaPlugin plugin;
    private final ArtifactMenu artifactMenu;
    private final int darkMatterCooldownTime = 3;
    private final int manauseg = 10;
    private static final CooldownManager darkMatterCooldownManager = new CooldownManager();

    public MagicFireOne(JavaPlugin plugin) {
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

    public static void resetAllCooldownsMagicFireOne(Player player) {
        darkMatterCooldownManager.resetCooldown(player.getName());
    }


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        Integer hasArtifact = artifactMenu.checkArtifactMenu(player, 101);
        if (hasArtifact == null) {
            sendActionBarMessage(player, " ");
            return;
        }

        ManaMechanics manaMechanics = new ManaMechanics(player.getUniqueId());
        int secondManaUse = manaMechanics.getSecondMana();
        // Проверяем, является ли клик левым или правым, и происходит ли он по блоку или воздуху
        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK) || (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !darkMatterCooldownManager.hasCooldown(player.getName()) && secondManaUse >= manauseg) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                event.setCancelled(true);
                handleFireBall(player);
                manaMechanics.addSecondMana(-manauseg);
                darkMatterCooldownManager.setCooldown(player.getName(), darkMatterCooldownTime);
            }
        }
    }



    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity clickedEntity = event.getRightClicked(); // Получаем существо, по которому кликнул игрок

        Integer hasArtifact = artifactMenu.checkArtifactMenu(player, 101);
        if (hasArtifact == null) {
            sendActionBarMessage(player, " ");
            return;
        }

        ManaMechanics manaMechanics = new ManaMechanics(player.getUniqueId());
        int secondManaUse = manaMechanics.getSecondMana();
        // Проверяем, является ли клик левым или правым
        if (event.getHand().equals(EquipmentSlot.HAND)) {
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !darkMatterCooldownManager.hasCooldown(player.getName()) && secondManaUse >= manauseg) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                event.setCancelled(true);
                handleFireBall(player);
                manaMechanics.addSecondMana(-manauseg);
                darkMatterCooldownManager.setCooldown(player.getName(), darkMatterCooldownTime);
            }
        } else if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !darkMatterCooldownManager.hasCooldown(player.getName()) && secondManaUse >= manauseg) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                event.setCancelled(true);
                handleFireBall(player);
                manaMechanics.addSecondMana(-manauseg);
                darkMatterCooldownManager.setCooldown(player.getName(), darkMatterCooldownTime);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getDamager();

        Integer hasArtifact = artifactMenu.checkArtifactMenu(player, 101);
        if (hasArtifact == null) {
            sendActionBarMessage(player, " ");
            return;
        }

        ManaMechanics manaMechanics = new ManaMechanics(player.getUniqueId());
        int secondManaUse = manaMechanics.getSecondMana();
        int heldItemSlot = player.getInventory().getHeldItemSlot();
        if (heldItemSlot == hasArtifact-9 && !darkMatterCooldownManager.hasCooldown(player.getName()) && secondManaUse >= manauseg) {
            if (Silence.getSilenceState(player)>= 1) {
                event.setCancelled(true);
                NoManaPlayer.displaySilenceMessage(player);
                return;
            }
            event.setCancelled(true);
            handleFireBall(player);
            manaMechanics.addSecondMana(-manauseg);
            darkMatterCooldownManager.setCooldown(player.getName(), darkMatterCooldownTime);
        }
    }


    public void checkSlot() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {

            String actionBarMessage = "";

            Integer hasArtifact = artifactMenu.checkArtifactMenu(onlinePlayer, 101);
            List<Integer> getspellslots =  artifactMenu.getDiamondHorseArmorSlots(onlinePlayer);
            if (hasArtifact == null) {
                continue;
            }

            int heldItemSlot = onlinePlayer.getInventory().getHeldItemSlot();
            if (heldItemSlot == hasArtifact-9) {
                if (darkMatterCooldownManager.hasCooldown(onlinePlayer.getName())) {
                    int remainingTime = (int) ((darkMatterCooldownManager.cooldowns.get(onlinePlayer.getName()) - System.currentTimeMillis()) / 1000);
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + "10 " + ChatColor.GRAY + "| " + ChatColor.RED + "Огненый шар" + ChatColor.GRAY + " | Кулдаун: " + remainingTime + " сек.");
                } else {
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + "10 " + ChatColor.GRAY + "| " + ChatColor.YELLOW + "Огненый шар" + ChatColor.GRAY + " |");
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

    private void playSoundForNearbyPlayers(Location location, Sound sound, float volume, float pitch, double radius) {
        for (Player nearbyPlayer : location.getWorld().getPlayers()) {
            if (nearbyPlayer.getLocation().distance(location) <= radius) {
                nearbyPlayer.playSound(location, sound, volume, pitch);
            }
        }
    }

    private void handleFireBall(Player player) {
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
        SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_GHAST_SHOOT, 0.7f, 1.0f, 10);

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

            if (!block.getType().isAir()) {
                armorStand.remove();
                taskReference.get().cancel();
                return;
            }

            for (Entity nearbyEntity : armorStand.getNearbyEntities(0.3, 0.3, 0.3)) {
                if (nearbyEntity instanceof LivingEntity && !nearbyEntity.equals(owner)) {
                    LivingEntity livingEntity = (LivingEntity) nearbyEntity;
                    Vector awayFromPlayer = nearbyEntity.getLocation().toVector().subtract(playerLocation.toVector()).normalize();
                    livingEntity.setVelocity(awayFromPlayer.multiply(0.2));

                    double baseDamage = 3;
                    double armor = livingEntity.getAttribute(Attribute.GENERIC_ARMOR).getValue();

                    double damageReduction = armor / (armor + 50);

                    double finalDamage = Math.max(0, baseDamage * (1 - damageReduction));

                    livingEntity.damage(finalDamage, owner);
                    livingEntity.setFireTicks(60);

                    armorStand.remove();
                    taskReference.get().cancel();
                    return;
                }
            }




            for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 5) {
                for (double phi = 0; phi <= Math.PI; phi += Math.PI / 5) {
                    double particleX = 0.3 * Math.sin(phi) * Math.cos(theta);
                    double particleY = 0.3 * Math.cos(phi);
                    double particleZ = 0.3 * Math.sin(phi) * Math.sin(theta);

                    Vector particleDirection = new Vector(particleX, particleY, particleZ);
                    spawnParticleAroundArmorStand(armorStand, particleDirection);
                }
            }
        }, 0, 1);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            armorStand.remove();
            task.cancel();
        }, 30);

        taskReference.set(task);
    }

    private void spawnParticleAroundArmorStand(ArmorStand armorStand, Vector particleDirection) {
        Location particleLocation = armorStand.getLocation().clone().add(particleDirection);

        armorStand.getWorld().spawnParticle(Particle.FLAME, particleLocation, 1, 0, 0, 0, 0);
    }


    private void moveArmorStand(ArmorStand armorStand, Vector direction) {
        Location location = armorStand.getLocation();
        location.add(direction);

        armorStand.teleport(location);
    }
}