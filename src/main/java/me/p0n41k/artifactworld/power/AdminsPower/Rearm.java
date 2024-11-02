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
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


public class Rearm implements Listener {
    private final JavaPlugin plugin;
    private final ArtifactMenu artifactMenu;
    private static final int rearmCooldownTime = 15;
    private static final int manauseg = 85;
    private final int cmduseg = 10006;
    private static final CooldownManager rearmCooldownManager = new CooldownManager();

    public Rearm(JavaPlugin plugin) {
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

    public static void resetAllCooldownsRearm(Player player) {
        rearmCooldownManager.resetCooldown(player.getName());
    }

    public static ItemStack createRearmItem() {
        ItemStack s6a = new ItemStack(Material.DIAMOND_HORSE_ARMOR);
        ItemMeta s6ameta = s6a.getItemMeta();
        s6ameta.setDisplayName(ChatColor.DARK_RED + "Реарм");
        s6ameta.setCustomModelData(10006);
        List<String> lores6a = new ArrayList<>();
        lores6a.add(ChatColor.BLUE + "   При использовании заклинания");
        lores6a.add(ChatColor.BLUE + "   Игрок начинает процесс сброса кулдауна способностей");
        lores6a.add(ChatColor.BLUE + "   Этот процесс можно сбить получив достаточный урон ");
        lores6a.add(ChatColor.BLUE + "   По окончанию игрок сбрасывает свои способности ");
        lores6a.add("");
        lores6a.add(ChatColor.AQUA + "   Мана: " + ChatColor.GRAY + manauseg);
        lores6a.add(ChatColor.RED + "   Перезарядка: " + ChatColor.GRAY + rearmCooldownTime);
        lores6a.add("");
        s6ameta.setLore(lores6a);
        s6a.setItemMeta(s6ameta);
        return s6a;
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
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !rearmCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    rearmCooldownManager.setCooldown(player.getName(), rearmCooldownTime);
                    event.setCancelled(true);
                    handleRearm(player);
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
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !rearmCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    rearmCooldownManager.setCooldown(player.getName(), rearmCooldownTime);
                    event.setCancelled(true);
                    handleRearm(player);
                    manaMechanics.addSecondMana(-manauseg);
                }
                else {
                    NoManaPlayer.displayNoManaMessage(player);
                }
            }
        } else if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !rearmCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    rearmCooldownManager.setCooldown(player.getName(), rearmCooldownTime);
                    event.setCancelled(true);
                    handleRearm(player);
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
        if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !rearmCooldownManager.hasCooldown(player.getName())) {
            if (Silence.getSilenceState(player)>= 1) {
                event.setCancelled(true);
                NoManaPlayer.displaySilenceMessage(player);
                return;
            }
            if (secondManaUse >= manauseg) {
                rearmCooldownManager.setCooldown(player.getName(), rearmCooldownTime);
                event.setCancelled(true);
                handleRearm(player);
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
                if (rearmCooldownManager.hasCooldown(onlinePlayer.getName())) {
                    int remainingTime = (int) ((rearmCooldownManager.cooldowns.get(onlinePlayer.getName()) - System.currentTimeMillis()) / 1000);
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + ""  + manauseg + ChatColor.GRAY + " | " + ChatColor.RED + "Реарм" + ChatColor.GRAY + " | КД: " + remainingTime + " сек.");
                } else {
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + "" + manauseg + ChatColor.GRAY + " | " + ChatColor.YELLOW + "Реарм" + ChatColor.GRAY + " |");
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

    private void handleRearm(Player player) {
        double playerhp = player.getHealth();
        stunRearm(player);
        SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 0.2f, 1.0f, 10);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_PISTON_CONTRACT, 0.9f, 1.0f, 10);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_PISTON_EXTEND, 0.9f, 1.0f, 10);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_PISTON_CONTRACT, 0.9f, 1.0f, 10);
                }, 6);
            }, 6);
        }, 3);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            double playerhpnew = player.getHealth();
            double resulthp = playerhp - playerhpnew;
            if (resulthp >= 5) {
                return;
            }
            CooldownCommands.resetAllCooldowns(player);
        }, 40);
    }

    private void stunRearm(Entity entity) {
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

            final float[] currentYaw = {playerLocation.getYaw()};
            taskId[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                if (player.isDead()) {
                    player.setWalkSpeed(0.2f);
                    player.setFlySpeed(0.1f);
                    HandlerList.unregisterAll(moveListener);
                    taskend[0].cancel();
                    taskId[0].cancel();
                    return;
                } else {
                    double radius = 0.4;
                    double angle = Math.random() * 2 * Math.PI;

                    double x = player.getLocation().getX() + radius * Math.cos(angle);
                    double y = player.getLocation().getY() + Math.random() * 2;
                    double z = player.getLocation().getZ() + radius * Math.sin(angle);

                    player.getWorld().spawnParticle(Particle.REDSTONE, new Location(player.getWorld(), x, y, z), 3,
                            new Particle.DustOptions(Color.fromRGB(0, 204, 204), 1));

                    currentYaw[0] += 18;
                    if (currentYaw[0] >= 360) {
                        currentYaw[0] -= 360;
                    }

                    Location newLocation = player.getLocation();
                    newLocation.setYaw(currentYaw[0]);
                    player.teleport(newLocation);

                    Location belowLocation = playerLocation.clone().subtract(0, 0, 0);
                    if (player.getWorld().getBlockAt(belowLocation).getType() == Material.AIR) {
                        playerLocation.subtract(0, 0.25, 0);
                        player.teleport(playerLocation);
                    } else {
                        Location playerlocationnew = player.getLocation();
                        player.teleport(playerlocationnew);
                    }
                }

            }, 0L, 1L);

            taskend[0] = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Location playerlocationnew = player.getLocation();
                player.teleport(playerlocationnew.add(0,0.3,0));
                player.setWalkSpeed(0.2f);
                player.setFlySpeed(0.1f);
                HandlerList.unregisterAll(moveListener);
                taskId[0].cancel();
            }, 40);
        }
    }

}