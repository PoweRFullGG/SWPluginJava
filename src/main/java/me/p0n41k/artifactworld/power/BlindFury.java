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


public class BlindFury implements Listener {
    private final JavaPlugin plugin;
    private final ArtifactMenu artifactMenu;
    private static final int blindFuryCooldownTime = 75;
    private static final int manauseg = 85;
    private final int cmduseg = 119;
    private static final CooldownManager blindFuryCooldownManager = new CooldownManager();

    public BlindFury(JavaPlugin plugin) {
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

    public static void resetAllCooldownsBlindFury(Player player) {
        blindFuryCooldownManager.resetCooldown(player.getName());
    }

    public static ItemStack createBlindFuryItem() {
        ItemStack s17 = new ItemStack(Material.DIAMOND_HORSE_ARMOR);
        ItemMeta s17meta = s17.getItemMeta();
        s17meta.setDisplayName(ChatColor.YELLOW + "Слепая Ярость");
        s17meta.setCustomModelData(119);
        List<String> lores17 = new ArrayList<>();
        lores17.add(ChatColor.BLUE + "   Погружает в безумие, дающее огромную");
        lores17.add(ChatColor.BLUE + "   силу, скорость и слепоту...");
        lores17.add("");
        lores17.add(ChatColor.AQUA + "   Мана: " + ChatColor.GRAY + manauseg);
        lores17.add(ChatColor.RED + "   Перезарядка: " + ChatColor.GRAY + blindFuryCooldownTime);
        lores17.add("");
        s17meta.setLore(lores17);
        s17.setItemMeta(s17meta);
        return s17;
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
            if (player.getInventory().getHeldItemSlot() == hasArtifact - 9 && !blindFuryCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    blindFuryCooldownManager.setCooldown(player.getName(), blindFuryCooldownTime);
                    event.setCancelled(true);
                    handleBlindFury(player);
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
            if (player.getInventory().getHeldItemSlot() == hasArtifact - 9 && !blindFuryCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    blindFuryCooldownManager.setCooldown(player.getName(), blindFuryCooldownTime);
                    event.setCancelled(true);
                    handleBlindFury(player);
                    manaMechanics.addSecondMana(-manauseg);
                } else {
                    NoManaPlayer.displayNoManaMessage(player);
                }
            }
        } else if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
            if (player.getInventory().getHeldItemSlot() == hasArtifact - 9 && !blindFuryCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    blindFuryCooldownManager.setCooldown(player.getName(), blindFuryCooldownTime);
                    event.setCancelled(true);
                    handleBlindFury(player);
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
        if (player.getInventory().getHeldItemSlot() == hasArtifact - 9 && !blindFuryCooldownManager.hasCooldown(player.getName())) {
            if (Silence.getSilenceState(player)>= 1) {
                event.setCancelled(true);
                NoManaPlayer.displaySilenceMessage(player);
                return;
            }
            if (secondManaUse >= manauseg) {
                blindFuryCooldownManager.setCooldown(player.getName(), blindFuryCooldownTime);
                event.setCancelled(true);
                handleBlindFury(player);
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
                if (blindFuryCooldownManager.hasCooldown(onlinePlayer.getName())) {
                    int remainingTime = (int) ((blindFuryCooldownManager.cooldowns.get(onlinePlayer.getName()) - System.currentTimeMillis()) / 1000);
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + "" + manauseg + ChatColor.GRAY + " | " + ChatColor.RED + "Слепая ярость" + ChatColor.GRAY + " | КД: " + remainingTime + " сек.");
                } else {
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + "" + manauseg + ChatColor.GRAY + " | " + ChatColor.YELLOW + "Слепая ярость" + ChatColor.GRAY + " |");
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

    private void handleBlindFury(Player player) {
        SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 0.3f, 1.0f, 10);
        SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_WARDEN_HEARTBEAT, 0.9f, 1.0f, 10);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_WARDEN_HEARTBEAT, 0.9f, 1.0f, 10);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_WARDEN_HEARTBEAT, 0.9f, 1.0f, 10);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_WARDEN_HEARTBEAT, 0.9f, 1.0f, 10);
                }, 10);
            }, 40);
        }, 10);

        BukkitTask particleTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (player.isOnline()) {
                for (int i = 0; i < 5; i++) {
                    double offsetX = Math.random() * 2 - 1;
                    double offsetZ = Math.random() * 2 - 1;
                    player.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, player.getLocation().add(offsetX, 1.3, offsetZ), 1, 0, 0, 0, 0);
                }
            }
        }, 0L, 20L);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 13 * 20, 1, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 13 * 20, 0, false, false)); // Сила 1 на 8 секунд
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 13 * 20, 1, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 13* 20, 1, false, false));// Сопротивление 1 на 8 секунд

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            particleTask.cancel();
        }, 13 * 20);
    }
}