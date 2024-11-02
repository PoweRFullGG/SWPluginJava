package me.p0n41k.artifactworld.power;

import me.p0n41k.artifactworld.Artifactworld;
import me.p0n41k.artifactworld.NoManaPlayer;
import me.p0n41k.artifactworld.artmenu.ArtifactMenu;
import me.p0n41k.artifactworld.power.Mechanics.CooldownManager;
import me.p0n41k.artifactworld.power.Mechanics.Silence;
import me.p0n41k.artifactworld.power.Mechanics.SoundUtil;
import org.bukkit.*;
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


public class Attraction implements Listener {
    private final JavaPlugin plugin;
    private final ArtifactMenu artifactMenu;
    private static final int attractionCooldownTime = 30;
    private static final int manauseg = 50;
    private final int cmduseg = 105;
    private static final CooldownManager attractionCooldownManager = new CooldownManager();

    public Attraction(JavaPlugin plugin) {
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

    public static void resetAllCooldownsAttraction(Player player) {
        attractionCooldownManager.resetCooldown(player.getName());
    }

    public static ItemStack createAttractionItem() {
        ItemStack s4 = new ItemStack(Material.DIAMOND_HORSE_ARMOR);
        ItemMeta s4meta = s4.getItemMeta();
        s4meta.setDisplayName(ChatColor.YELLOW + "Притяжение");
        s4meta.setCustomModelData(105);
        List<String> lores4 = new ArrayList<>();
        lores4.add(ChatColor.BLUE + "   Притягивает всех вокруг к себе.");
        lores4.add("");
        lores4.add(ChatColor.AQUA + "   Мана: " + ChatColor.GRAY + manauseg);
        lores4.add(ChatColor.RED + "   Перезарядка: " + ChatColor.GRAY + attractionCooldownTime);
        lores4.add("");
        s4meta.setLore(lores4);
        s4.setItemMeta(s4meta);
        return s4;
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
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !attractionCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    attractionCooldownManager.setCooldown(player.getName(), attractionCooldownTime);
                    event.setCancelled(true);
                    handleAttraction(player);
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
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !attractionCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    attractionCooldownManager.setCooldown(player.getName(), attractionCooldownTime);
                    event.setCancelled(true);
                    handleAttraction(player);
                    manaMechanics.addSecondMana(-manauseg);
                }
                else {
                    NoManaPlayer.displayNoManaMessage(player);
                }
            }
        } else if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !attractionCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    attractionCooldownManager.setCooldown(player.getName(), attractionCooldownTime);
                    event.setCancelled(true);
                    handleAttraction(player);
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
        if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !attractionCooldownManager.hasCooldown(player.getName())) {
            if (Silence.getSilenceState(player)>= 1) {
                event.setCancelled(true);
                NoManaPlayer.displaySilenceMessage(player);
                return;
            }
            if (secondManaUse >= manauseg) {
                attractionCooldownManager.setCooldown(player.getName(), attractionCooldownTime);
                event.setCancelled(true);
                handleAttraction(player);
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
                if (attractionCooldownManager.hasCooldown(onlinePlayer.getName())) {
                    int remainingTime = (int) ((attractionCooldownManager.cooldowns.get(onlinePlayer.getName()) - System.currentTimeMillis()) / 1000);
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + ""  + manauseg + ChatColor.GRAY + " | " + ChatColor.RED + "Притяжение" + ChatColor.GRAY + " | КД: " + remainingTime + " сек.");
                } else {
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + "" + manauseg + ChatColor.GRAY + " | " + ChatColor.YELLOW + "Притяжение" + ChatColor.GRAY + " |");
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

    private void handleAttraction(Player player) {
        SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 0.3f, 1.0f, 18);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 0.3f, 1.0f, 18);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 0.3f, 1.0f, 18);
            }, 10);
        }, 10);
        final BukkitTask[] task = new BukkitTask[1];
        final BukkitTask[] taskend = new BukkitTask[1];
        task[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            double radius = 1.3;
            double angle = Math.random() * 2 * Math.PI;

            double x = player.getLocation().getX() + radius * Math.cos(angle);
            double y = player.getLocation().getY() + Math.random() * 2;
            double z = player.getLocation().getZ() + radius * Math.sin(angle);

            player.getWorld().spawnParticle(Particle.REDSTONE, new Location(player.getWorld(), x, y, z), 1,
                    new Particle.DustOptions(Color.fromRGB(153,51,255), 1));
            List<Entity> nearbyEntities = player.getNearbyEntities(12, 12, 12);

            for (Entity entity : nearbyEntities) {
                if (entity instanceof LivingEntity && !entity.equals(player)) {
                    Vector direction = entity.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();

                    double gravity = -0.2;
                    direction.setY(direction.getY() + gravity);

                    direction.normalize();

                    direction.multiply(new Vector(-0.4, -0.4, -0.4));

                    if (Double.isFinite(direction.getX()) && Double.isFinite(direction.getY()) && Double.isFinite(direction.getZ())) {
                        ((LivingEntity) entity).setVelocity(direction);
                    } else {
                        continue;
                    }
                }
            }
            if (player.isSneaking()) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    task[0].cancel();
                    taskend[0].cancel();
                }, 1);
            }
        }, 0, 1);


        taskend[0] = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            task[0].cancel();
        }, 60);
    }
}