package me.p0n41k.artifactworld.power.AdminsPower;

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


public class Bubble implements Listener {
    private final JavaPlugin plugin;
    private final ArtifactMenu artifactMenu;
    private static final int bubbleCooldownTime = 2;
    private static final int manauseg = 100;
    private final int cmduseg = 10001;
    private static final CooldownManager bubbleCooldownManager = new CooldownManager();

    public Bubble(JavaPlugin plugin) {
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

    public static void resetAllCooldownsBubble(Player player) {
        bubbleCooldownManager.resetCooldown(player.getName());
    }

    public static ItemStack createBubbleItem() {
        ItemStack s1a = new ItemStack(Material.DIAMOND_HORSE_ARMOR);
        ItemMeta s1ameta = s1a.getItemMeta();
        s1ameta.setDisplayName(ChatColor.DARK_RED + "Пузырик");
        s1ameta.setCustomModelData(10001);
        List<String> lores1a = new ArrayList<>();
        lores1a.add(ChatColor.BLUE + "   При использовании игрок выпускает");
        lores1a.add(ChatColor.BLUE + "   Шарик который летит туда - куда смотрит игрок");
        lores1a.add(ChatColor.BLUE + "   При попадании наносит быстрый и колоссальный урон");
        lores1a.add("");
        lores1a.add(ChatColor.AQUA + "   Мана: " + ChatColor.GRAY + manauseg);
        lores1a.add(ChatColor.RED + "   Перезарядка: " + ChatColor.GRAY + bubbleCooldownTime);
        lores1a.add("");
        s1ameta.setLore(lores1a);
        s1a.setItemMeta(s1ameta);
        return s1a;
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
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !bubbleCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    bubbleCooldownManager.setCooldown(player.getName(), bubbleCooldownTime);
                    event.setCancelled(true);
                    handleAttractiveFlow(player);
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
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !bubbleCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    bubbleCooldownManager.setCooldown(player.getName(), bubbleCooldownTime);
                    event.setCancelled(true);
                    handleAttractiveFlow(player);
                    manaMechanics.addSecondMana(-manauseg);
                }
                else {
                    NoManaPlayer.displayNoManaMessage(player);
                }
            }
        } else if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !bubbleCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    bubbleCooldownManager.setCooldown(player.getName(), bubbleCooldownTime);
                    event.setCancelled(true);
                    handleAttractiveFlow(player);
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
        if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !bubbleCooldownManager.hasCooldown(player.getName())) {
            if (Silence.getSilenceState(player)>= 1) {
                event.setCancelled(true);
                NoManaPlayer.displaySilenceMessage(player);
                return;
            }
            if (secondManaUse >= manauseg) {
                bubbleCooldownManager.setCooldown(player.getName(), bubbleCooldownTime);
                event.setCancelled(true);
                handleAttractiveFlow(player);
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
                if (bubbleCooldownManager.hasCooldown(onlinePlayer.getName())) {
                    int remainingTime = (int) ((bubbleCooldownManager.cooldowns.get(onlinePlayer.getName()) - System.currentTimeMillis()) / 1000);
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + ""  + manauseg + ChatColor.GRAY + " | " + ChatColor.RED + "Пузырик" + ChatColor.GRAY + " | КД: " + remainingTime + " сек.");
                } else {
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + "" + manauseg + ChatColor.GRAY + " | " + ChatColor.YELLOW + "Пузырик" + ChatColor.GRAY + " |");
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
        final BukkitTask[] taskTimer = new BukkitTask[1];
        final BukkitTask[] task = new BukkitTask[1];
        final BukkitTask[] taskDamage = new BukkitTask[1];

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

        Player owner = player;



        task[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            float yaw = player.getLocation().getYaw();
            float pitch = player.getLocation().getPitch();

            double yawAngle = Math.toRadians(yaw);
            double pitchAngle = Math.toRadians(pitch);

            double x = -Math.sin(yawAngle) * Math.cos(pitchAngle);
            double y = -Math.sin(pitchAngle);
            double z = Math.cos(yawAngle) * Math.cos(pitchAngle);

            Vector direction = new Vector(x, y, z).normalize().multiply(0.3);
            moveArmorStand(armorStand, direction);


            for (Entity nearbyEntity : armorStand.getNearbyEntities(0.3, 0.3, 0.3)) {
                if (nearbyEntity instanceof LivingEntity && !nearbyEntity.equals(owner)) {
                    LivingEntity livingEntity = (LivingEntity) nearbyEntity;

                    taskDamage[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                        livingEntity.setNoDamageTicks(0);
                        livingEntity.damage(0.2);
                    }, 0, 1);

                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        taskDamage[0].cancel();
                    }, 50);

                    taskTimer[0].cancel();
                    armorStand.remove();
                    task[0].cancel();
                    return;
                }
            }

            // Создаем шар частиц вокруг стойки для брони
            for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 3) {
                for (double phi = 0; phi <= Math.PI; phi += Math.PI / 3) {
                    double particleX = 0.2 * Math.sin(phi) * Math.cos(theta);
                    double particleY = 0.2 * Math.cos(phi);
                    double particleZ = 0.2 * Math.sin(phi) * Math.sin(theta);

                    Vector particleDirection = new Vector(particleX, particleY, particleZ);
                    spawnParticleAroundArmorStand(armorStand, particleDirection);
                }
            }
        }, 0, 1);

        taskTimer[0] = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            task[0].cancel();
            armorStand.remove();
            taskTimer[0].cancel();
        }, 200);
    }

    private void spawnParticleAroundArmorStand(ArmorStand armorStand, Vector particleDirection) {
        Location particleLocation = armorStand.getLocation().clone().add(particleDirection);
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(255, 102, 255), 1);
        armorStand.getWorld().spawnParticle(Particle.REDSTONE, particleLocation, 1, 0, 0, 0, 0, dustOptions);
    }


    private void moveArmorStand(ArmorStand armorStand, Vector direction) {
        Location location = armorStand.getLocation();
        location.add(direction);
        armorStand.teleport(location);
    }
}