package me.p0n41k.artifactworld.power;

import me.p0n41k.artifactworld.Artifactworld;
import me.p0n41k.artifactworld.NoManaPlayer;
import me.p0n41k.artifactworld.artmenu.ArtifactMenu;
import me.p0n41k.artifactworld.power.Mechanics.CooldownManager;
import me.p0n41k.artifactworld.power.Mechanics.Silence;
import me.p0n41k.artifactworld.power.Mechanics.SoundUtil;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
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

import java.util.*;


public class Beacon implements Listener {
    private final JavaPlugin plugin;
    private final ArtifactMenu artifactMenu;
    private static final int beaconCooldownTime = 70;
    private static final int manauseg = 85;
    private final int cmduseg = 108;
    private static final CooldownManager beaconCooldownManager = new CooldownManager();

    private Map<Player, Location> beaconLocations = new HashMap<>();

    public Beacon(JavaPlugin plugin) {
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

    public static void resetAllCooldownsBeacon(Player player) {
        beaconCooldownManager.resetCooldown(player.getName());
    }

    public static ItemStack createBeaconItem() {
        ItemStack s6 = new ItemStack(Material.DIAMOND_HORSE_ARMOR);
        ItemMeta s6meta = s6.getItemMeta();
        s6meta.setDisplayName(ChatColor.YELLOW + "Маячок");
        s6meta.setCustomModelData(108);
        List<String> lores6 = new ArrayList<>();
        lores6.add(ChatColor.BLUE + "   Показывает местоположение цели.");
        lores6.add("");
        lores6.add(ChatColor.AQUA + "   Мана: " + ChatColor.GRAY + manauseg);
        lores6.add(ChatColor.RED + "   Перезарядка: " + ChatColor.GRAY + beaconCooldownTime);
        lores6.add("");
        s6meta.setLore(lores6);
        s6.setItemMeta(s6meta);
        return s6;
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
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !beaconCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    event.setCancelled(true);
                    Entity clickedEntity = event.getRightClicked();
                    handleBeacon(player, clickedEntity);
                    manaMechanics.addSecondMana(-manauseg);
                }
                else {
                    NoManaPlayer.displayNoManaMessage(player);
                }
            }
        } else if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !beaconCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    event.setCancelled(true);
                    Entity clickedEntity = event.getRightClicked();
                    handleBeacon(player, clickedEntity);
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
        if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !beaconCooldownManager.hasCooldown(player.getName())) {
            if (Silence.getSilenceState(player)>= 1) {
                event.setCancelled(true);
                NoManaPlayer.displaySilenceMessage(player);
                return;
            }
            if (secondManaUse >= manauseg) {
                event.setCancelled(true);
                Entity clickedEntity = event.getEntity();
                handleBeacon(player, clickedEntity);
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

            Location targetLocation = beaconLocations.get(onlinePlayer);
            int heldItemSlot = onlinePlayer.getInventory().getHeldItemSlot();
            if (heldItemSlot == hasArtifact-9) {
                if (targetLocation != null) {
                    double x = Math.round(targetLocation.getX());
                    double y = Math.round(targetLocation.getY());
                    double z = Math.round(targetLocation.getZ());

                    // Удаление последних двух символов из координат
                    String formattedX = String.valueOf(x).substring(0, String.valueOf(x).length() - 2);
                    String formattedY = String.valueOf(y).substring(0, String.valueOf(y).length() - 2);
                    String formattedZ = String.valueOf(z).substring(0, String.valueOf(z).length() - 2);
                    if (!beaconCooldownManager.hasCooldown(onlinePlayer.getName())) {
                        sendActionBarMessage(onlinePlayer, ChatColor.AQUA + "" + manauseg + ChatColor.GRAY + " | " + ChatColor.YELLOW + "Маячок" + " [ " + "X: " + formattedX + " Y: " + formattedY + " Z: " + formattedZ + " ]" + ChatColor.GRAY + " |");
                    } else {
                        int remainingTime = (int) ((beaconCooldownManager.cooldowns.get(onlinePlayer.getName()) - System.currentTimeMillis()) / 1000);
                        sendActionBarMessage(onlinePlayer, ChatColor.AQUA + ""  + manauseg + ChatColor.GRAY + " | " + ChatColor.RED + "Маячок" + ChatColor.YELLOW + " [ " + "X: " + formattedX + " Y: " + formattedY + " Z: " + formattedZ + " ]" + ChatColor.GRAY + " | КД: " + remainingTime + " сек.");
                    }
                } else if (beaconCooldownManager.hasCooldown(onlinePlayer.getName())) {
                    int remainingTime = (int) ((beaconCooldownManager.cooldowns.get(onlinePlayer.getName()) - System.currentTimeMillis()) / 1000);
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + ""  + manauseg + ChatColor.GRAY + " | " + ChatColor.RED + "Маячок" + ChatColor.GRAY + " | КД: " + remainingTime + " сек.");
                } else {
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + "" + manauseg + ChatColor.GRAY + " | " + ChatColor.YELLOW + "Маячок" + ChatColor.GRAY + " |");
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

    private void handleBeacon(Player player, Entity targetEntity) {
        beaconCooldownManager.setCooldown(player.getName(), beaconCooldownTime);
        final BukkitTask[] taskProv = new BukkitTask[1];
        final BukkitTask[] taskend = new BukkitTask[1];
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            Location targetLocation = targetEntity.getLocation();
            beaconLocations.put(player, targetLocation);

            if (player.getLocation().distance(targetEntity.getLocation()) <= 80) {
                Color particleColor = Color.fromRGB(255,0,127);

                Location playerLocation = player.getLocation().add(0, 0.7, 0);

                Vector direction = targetLocation.toVector().subtract(playerLocation.toVector());
                direction.normalize();

                for (double i = 0; i < playerLocation.distance(targetLocation); i += 0.7) {
                    Location particleLocation = playerLocation.clone().add(direction.clone().multiply(i));
                    player.spawnParticle(Particle.REDSTONE, particleLocation, 2, new Particle.DustOptions(particleColor, 1));
                }
            }

        }, 0, 5);

        taskProv[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if ((player.isDead() || !player.isOnline()) || !((Player) targetEntity).isOnline()) {
                task.cancel();
                taskend[0].cancel();
                beaconLocations.remove(player);
                taskProv[0].cancel();
            }
        }, 0, 2);

        taskend[0] = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            task.cancel();
            taskProv[0].cancel();
            beaconLocations.remove(player);
        }, 20 * 30);
    }
}