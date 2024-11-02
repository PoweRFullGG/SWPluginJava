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


public class Rollback implements Listener {
    private final JavaPlugin plugin;
    private final ArtifactMenu artifactMenu;
    private Location playerStartPosition;
    private static final int rollbackCooldownTime = 30;
    private static final int manauseg = 50;
    private final int cmduseg = 121;
    private static final CooldownManager rollbackCooldownManager = new CooldownManager();

    public Rollback(JavaPlugin plugin) {
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

    public static void resetAllCooldownsRollback(Player player) {
        rollbackCooldownManager.resetCooldown(player.getName());
    }

    public static ItemStack createRollbackItem() {
        ItemStack s19 = new ItemStack(Material.DIAMOND_HORSE_ARMOR);
        ItemMeta s19meta = s19.getItemMeta();
        s19meta.setDisplayName(ChatColor.YELLOW + "Rollback");
        s19meta.setCustomModelData(121);
        List<String> lores19 = new ArrayList<>();
        lores19.add(ChatColor.BLUE + "   Отменяет перемещение игрока, телепортируя");
        lores19.add(ChatColor.BLUE + "   его в место активации заклинания через");
        lores19.add(ChatColor.BLUE + "   5 секунд.");
        lores19.add("");
        lores19.add(ChatColor.AQUA + "   Мана: " + ChatColor.GRAY + manauseg);
        lores19.add(ChatColor.RED + "   Перезарядка: " + ChatColor.GRAY + rollbackCooldownTime);
        lores19.add("");
        s19meta.setLore(lores19);
        s19.setItemMeta(s19meta);
        return s19;
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
            if (player.getInventory().getHeldItemSlot() == hasArtifact - 9 && !rollbackCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    rollbackCooldownManager.setCooldown(player.getName(), rollbackCooldownTime);
                    event.setCancelled(true);
                    handleRollback(player);
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
            if (player.getInventory().getHeldItemSlot() == hasArtifact - 9 && !rollbackCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    rollbackCooldownManager.setCooldown(player.getName(), rollbackCooldownTime);
                    event.setCancelled(true);
                    handleRollback(player);
                    manaMechanics.addSecondMana(-manauseg);
                } else {
                    NoManaPlayer.displayNoManaMessage(player);
                }
            }
        } else if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
            if (player.getInventory().getHeldItemSlot() == hasArtifact - 9 && !rollbackCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    rollbackCooldownManager.setCooldown(player.getName(), rollbackCooldownTime);
                    event.setCancelled(true);
                    handleRollback(player);
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
        if (player.getInventory().getHeldItemSlot() == hasArtifact - 9 && !rollbackCooldownManager.hasCooldown(player.getName())) {
            if (Silence.getSilenceState(player)>= 1) {
                event.setCancelled(true);
                NoManaPlayer.displaySilenceMessage(player);
                return;
            }
            if (secondManaUse >= manauseg) {
                rollbackCooldownManager.setCooldown(player.getName(), rollbackCooldownTime);
                event.setCancelled(true);
                handleRollback(player);
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
                if (rollbackCooldownManager.hasCooldown(onlinePlayer.getName())) {
                    int remainingTime = (int) ((rollbackCooldownManager.cooldowns.get(onlinePlayer.getName()) - System.currentTimeMillis()) / 1000);
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + "" + manauseg + ChatColor.GRAY + " | " + ChatColor.RED + "Откат" + ChatColor.GRAY + " | КД: " + remainingTime + " сек.");
                } else {
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + "" + manauseg + ChatColor.GRAY + " | " + ChatColor.YELLOW + "Откат" + ChatColor.GRAY + " |");
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

    private void handleRollback(Player player) {
        final BukkitTask[] task = new BukkitTask[1];
        playerStartPosition = player.getLocation();
        SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1.0f, 1.0f, 10);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1.0f, 1.0f, 10);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1.0f, 1.0f, 10);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1.0f, 1.0f, 10);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1.0f, 1.0f, 10);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1.0f, 1.0f, 10);
                            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1.0f, 1.0f, 10);
                                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                    SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1.0f, 1.0f, 10);
                                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                        SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1.0f, 1.0f, 10);
                                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1.0f, 1.0f, 10);
                                            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                                SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1.0f, 1.0f, 10);
                                                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                                    SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1.0f, 1.0f, 10);
                                                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                                        SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1.0f, 1.0f, 10);
                                                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                                            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1.0f, 1.0f, 10);
                                                            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                                                SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1.0f, 1.0f, 10);
                                                                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                                                    SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1.0f, 1.0f, 10);
                                                                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                                                        SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1.0f, 1.0f, 10);
                                                                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                                                            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1.0f, 1.0f, 10);
                                                                            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                                                                SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1.0f, 1.0f, 10);
                                                                                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                                                                    SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1.0f, 1.0f, 10);
                                                                                }, 2);
                                                                            }, 2);
                                                                        }, 2);
                                                                    }, 2);
                                                                }, 2);
                                                            }, 2);
                                                        }, 4);
                                                    }, 4);
                                                }, 4);
                                            }, 4);
                                        }, 4);
                                    }, 7);
                                }, 7);
                            }, 7);
                        }, 7);
                    }, 7);
                }, 10);
            }, 10);
        }, 10);
        task[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            double radius2 = 0.5; // Радиус круга частиц
            double angle = Math.random() * 2 * Math.PI;

            double x = playerStartPosition.getX() + radius2 * Math.cos(angle);
            double y = playerStartPosition.getY() + Math.random() * 2; // Высота частиц
            double z = playerStartPosition.getZ() + radius2 * Math.sin(angle);

            player.getWorld().spawnParticle(Particle.REDSTONE, new Location(player.getWorld(), x, y, z), 3,
                    new Particle.DustOptions(Color.fromRGB(76, 0, 153), 1));

            double radius3 = 0.5;
            double angle2 = Math.random() * 2 * Math.PI;

            double x2 = player.getLocation().getX() + radius3 * Math.cos(angle2);
            double y2 = player.getLocation().getY() + Math.random() * 2;
            double z2 = player.getLocation().getZ() + radius3 * Math.sin(angle2);

            player.getWorld().spawnParticle(Particle.REDSTONE, new Location(player.getWorld(), x2, y2, z2), 3,
                    new Particle.DustOptions(Color.fromRGB(76, 0, 153), 1));
        }, 0, 1);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!player.isOnline()) {
                task[0].cancel();
                return;
            }
            player.teleport(playerStartPosition);
            task[0].cancel();
            for (int i = 0; i < 20; i++) {
                double radius2 = 0.5;
                double angle = Math.random() * 2 * Math.PI;

                double x = player.getLocation().getX() + radius2 * Math.cos(angle);
                double y = player.getLocation().getY() + Math.random() * 2;
                double z = player.getLocation().getZ() + radius2 * Math.sin(angle);

                player.getWorld().spawnParticle(Particle.REDSTONE, new Location(player.getWorld(), x, y, z), 3,
                        new Particle.DustOptions(Color.fromRGB(76, 0, 153), 1));
            }
            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 0.7f, 1.0f, 10);
        }, 5 * 20);
    }
}