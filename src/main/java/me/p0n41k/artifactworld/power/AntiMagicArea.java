package me.p0n41k.artifactworld.power;

import me.p0n41k.artifactworld.Artifactworld;
import me.p0n41k.artifactworld.NoManaPlayer;
import me.p0n41k.artifactworld.artmenu.ArtifactMenu;
import me.p0n41k.artifactworld.power.Mechanics.CooldownManager;
import me.p0n41k.artifactworld.power.Mechanics.Silence;
import me.p0n41k.artifactworld.power.Mechanics.SoundUtil;
import org.bukkit.*;
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

import java.util.*;


public class AntiMagicArea implements Listener {
    private final JavaPlugin plugin;
    private final ArtifactMenu artifactMenu;
    private static final int antiMagicAreaCooldownTime = 50;
    private static final int manauseg = 60;
    private final int cmduseg = 117;
    private static final CooldownManager antiMagicAreaCooldownManager = new CooldownManager();

    public AntiMagicArea(JavaPlugin plugin) {
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

    public static void resetAllCooldownsAntiMagicArea(Player player) {
        antiMagicAreaCooldownManager.resetCooldown(player.getName());
    }

    public static ItemStack createAntiMagicAreaItem() {
        ItemStack s15 = new ItemStack(Material.DIAMOND_HORSE_ARMOR);
        ItemMeta s15meta = s15.getItemMeta();
        s15meta.setDisplayName(ChatColor.YELLOW + "Анти-магическая Область");
        s15meta.setCustomModelData(117);
        List<String> lores15 = new ArrayList<>();
        lores15.add(ChatColor.BLUE + "   Создает область, выжигающую ману");
        lores15.add(ChatColor.BLUE + "   у противников и не позволяющую");
        lores15.add(ChatColor.BLUE + "   использовать способности, находясь");
        lores15.add(ChatColor.BLUE + "   в ней.");
        lores15.add("");
        lores15.add(ChatColor.AQUA + "   Мана: " + ChatColor.GRAY + manauseg);
        lores15.add(ChatColor.RED + "   Перезарядка: " + ChatColor.GRAY + antiMagicAreaCooldownTime);
        lores15.add("");
        s15meta.setLore(lores15);
        s15.setItemMeta(s15meta);
        return s15;
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
            if (player.getInventory().getHeldItemSlot() == hasArtifact - 9 && !antiMagicAreaCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    antiMagicAreaCooldownManager.setCooldown(player.getName(), antiMagicAreaCooldownTime);
                    event.setCancelled(true);
                    handleAntiMagicArea(player);
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
            if (player.getInventory().getHeldItemSlot() == hasArtifact - 9 && !antiMagicAreaCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    antiMagicAreaCooldownManager.setCooldown(player.getName(), antiMagicAreaCooldownTime);
                    event.setCancelled(true);
                    handleAntiMagicArea(player);
                    manaMechanics.addSecondMana(-manauseg);
                } else {
                    NoManaPlayer.displayNoManaMessage(player);
                }
            }
        } else if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
            if (player.getInventory().getHeldItemSlot() == hasArtifact - 9 && !antiMagicAreaCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    antiMagicAreaCooldownManager.setCooldown(player.getName(), antiMagicAreaCooldownTime);
                    event.setCancelled(true);
                    handleAntiMagicArea(player);
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
        if (player.getInventory().getHeldItemSlot() == hasArtifact - 9 && !antiMagicAreaCooldownManager.hasCooldown(player.getName())) {
            if (Silence.getSilenceState(player)>= 1) {
                event.setCancelled(true);
                NoManaPlayer.displaySilenceMessage(player);
                return;
            }
            if (secondManaUse >= manauseg) {
                antiMagicAreaCooldownManager.setCooldown(player.getName(), antiMagicAreaCooldownTime);
                event.setCancelled(true);
                handleAntiMagicArea(player);
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
            List<Integer> getspellslots =  artifactMenu.getDiamondHorseArmorSlots(onlinePlayer);

            if (hasArtifact == null) {
                continue;
            }

            int heldItemSlot = onlinePlayer.getInventory().getHeldItemSlot();
            if (heldItemSlot == hasArtifact-9) {
                if (antiMagicAreaCooldownManager.hasCooldown(onlinePlayer.getName())) {
                    int remainingTime = (int) ((antiMagicAreaCooldownManager.cooldowns.get(onlinePlayer.getName()) - System.currentTimeMillis()) / 1000);
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + ""  + manauseg + ChatColor.GRAY + " | " + ChatColor.RED + "Антимагическая область" + ChatColor.GRAY + " | КД: " + remainingTime + " сек.");
                } else {
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + "" + manauseg + ChatColor.GRAY + " | " + ChatColor.YELLOW + "Антимагическая область" + ChatColor.GRAY + " |");
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

    private void handleAntiMagicArea(Player player) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 0.55f, 0.5f, 18);
        }, 1);
        final BukkitTask[] taskPar = new BukkitTask[1];
        final BukkitTask[] task = new BukkitTask[1];
        Location locationPlayer = player.getLocation();
        //SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.PORT, 1.0f, 1.0f, 10);
        ArmorStand armorStand = player.getWorld().spawn(locationPlayer, ArmorStand.class, entity -> {
            entity.setGravity(false);
            entity.setVisible(false);
            entity.setInvulnerable(true);
            entity.setMarker(true);
            entity.setAI(false);
            entity.setCanPickupItems(false);
            entity.addScoreboardTag("unmovable");
        });
        Map<Player, Boolean> playersInArea = new HashMap<>();

        taskPar[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            particleFunc(armorStand, 9);
        }, 0, 8);

        task[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Entity nearbyEntity : armorStand.getNearbyEntities(9.0, 9.0, 9.0)) {
                if (nearbyEntity instanceof Player) {
                    Player livingEntity = (Player) nearbyEntity;

                    if (!playersInArea.getOrDefault(livingEntity, false)) {
                        Silence.plusSilenceState(livingEntity, 1);
                        playersInArea.put(livingEntity, true);
                    }

                    if (!livingEntity.equals(player)) {
                        ManaMechanics manaMechanics = new ManaMechanics(livingEntity.getUniqueId());
                        int secondManaLivingEntity = manaMechanics.getSecondMana();
                        if (secondManaLivingEntity > 0) {
                            manaMechanics.addSecondMana(-1);
                            double radius = 0.5;
                            double angle = Math.random() * 2 * Math.PI;

                            double x = livingEntity.getLocation().getX() + radius * Math.cos(angle);
                            double y = livingEntity.getLocation().getY() + Math.random() * 2;
                            double z = livingEntity.getLocation().getZ() + radius * Math.sin(angle);

                            livingEntity.getWorld().spawnParticle(Particle.REDSTONE, new Location(livingEntity.getWorld(), x, y, z), 1,
                                    new Particle.DustOptions(Color.fromRGB(200, 128, 0), 2));
                        }
                    }
                }
            }

            for (Player onlinePlayer : playersInArea.keySet()) {
                if (playersInArea.get(onlinePlayer)) {
                    double distance = armorStand.getLocation().distance(onlinePlayer.getLocation());
                    if (distance > 9.0) {
                        Silence.plusSilenceState(onlinePlayer, -1);
                        playersInArea.put(onlinePlayer, false);
                    }
                }
            }
        }, 0, 1);


        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Player onlinePlayer : playersInArea.keySet()) {
                if (playersInArea.get(onlinePlayer)) {
                    if (playersInArea.getOrDefault(onlinePlayer, true)) {
                        Silence.plusSilenceState(onlinePlayer, -1);
                        playersInArea.put(onlinePlayer, false);
                    }
                }
            }
            task[0].cancel();
            taskPar[0].cancel();
            armorStand.remove();
        }, 20 * 10);
    }

    private void particleFunc(ArmorStand armorStand, double finalRadius) {
        for (double theta = 0; theta <= Math.PI; theta += Math.PI / 14) {
            for (double phi = 0; phi < 2 * Math.PI; phi += Math.PI / 14) {
                double x = finalRadius * Math.sin(theta) * Math.cos(phi);
                double y = finalRadius * Math.sin(theta) * Math.sin(phi);
                double z = finalRadius * Math.cos(theta);
                Location particleLocation = armorStand.getLocation().clone().add(x, y, z);
                armorStand.getLocation().getWorld().spawnParticle(Particle.REDSTONE, particleLocation, 2, 0, 0, 0, 1,
                        new Particle.DustOptions(Color.fromRGB(200, 128, 0), 1));
            }
        }
    }

}