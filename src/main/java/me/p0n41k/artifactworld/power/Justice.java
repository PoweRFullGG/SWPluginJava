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
import org.bukkit.scheduler.BukkitRunnable;

import me.p0n41k.artifactworld.data.ManaMechanics;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;


public class Justice implements Listener {
    private final JavaPlugin plugin;
    private final ArtifactMenu artifactMenu;
    private static final int justiceCooldownTime = 12;
    private static final int manauseg = 45;
    private final int cmduseg = 107;
    private static final CooldownManager justiceCooldownManager = new CooldownManager();

    private Map<UUID, Boolean> playerAbilitiesJustice = new HashMap<>();

    public Justice(JavaPlugin plugin) {
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

    public static void resetAllCooldownsJustice(Player player) {
        justiceCooldownManager.resetCooldown(player.getName());
    }

    public static ItemStack createJusticeItem() {
        ItemStack ss6 = new ItemStack(Material.DIAMOND_HORSE_ARMOR);
        ItemMeta ss6meta = ss6.getItemMeta();
        ss6meta.setDisplayName(ChatColor.YELLOW + "Телекинез");
        ss6meta.setCustomModelData(107);
        List<String> loress6 = new ArrayList<>();
        loress6.add(ChatColor.BLUE + "   Позволяет сконцентрироваться, чтобы");
        loress6.add(ChatColor.BLUE + "   удерживать существо.");
        loress6.add("");
        loress6.add(ChatColor.DARK_GRAY + "   * Получение урона сбивает концентрацию.");
        loress6.add("");
        loress6.add(ChatColor.AQUA + "   Мана: " + ChatColor.GRAY + manauseg);
        loress6.add(ChatColor.RED + "   Перезарядка: " + ChatColor.GRAY + justiceCooldownTime);
        loress6.add("");
        ss6meta.setLore(loress6);
        ss6.setItemMeta(ss6meta);
        return ss6;
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
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !justiceCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    event.setCancelled(true);
                    handleJustice(player, event.getRightClicked());
                }
                else {
                    NoManaPlayer.displayNoManaMessage(player);
                }
            }
        } else if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !justiceCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    event.setCancelled(true);
                    handleJustice(player, event.getRightClicked());
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
        if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !justiceCooldownManager.hasCooldown(player.getName())) {
            if (Silence.getSilenceState(player)>= 1) {
                event.setCancelled(true);
                NoManaPlayer.displaySilenceMessage(player);
                return;
            }
            if (secondManaUse >= manauseg) {
                event.setCancelled(true);
                handleJustice(player, event.getEntity());
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
                if (justiceCooldownManager.hasCooldown(onlinePlayer.getName())) {
                    int remainingTime = (int) ((justiceCooldownManager.cooldowns.get(onlinePlayer.getName()) - System.currentTimeMillis()) / 1000);
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + ""  + manauseg + ChatColor.GRAY + " | " + ChatColor.RED + "Телекинез" + ChatColor.GRAY + " | КД: " + remainingTime + " сек.");
                } else {
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + "" + manauseg + ChatColor.GRAY + " | " + ChatColor.YELLOW + "Телекинез" + ChatColor.GRAY + " |");
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

    private void handleJustice(Player player, Entity targetEntity) {
        UUID playerId = player.getUniqueId();
        if (playerAbilitiesJustice.getOrDefault(playerId, false)) {
            return;
        }
        if (targetEntity instanceof Player) {
            UUID targetId = targetEntity.getUniqueId();
            if (playerAbilitiesJustice.getOrDefault(targetId, false)) {
                return;
            }
        }
        ManaMechanics manaMechanics = new ManaMechanics(player.getUniqueId());
        manaMechanics.addSecondMana(-manauseg);
        playerAbilitiesJustice.put(playerId, true);
        if (targetEntity instanceof LivingEntity && !(targetEntity instanceof ArmorStand) && !targetEntity.equals(player)) {
            LivingEntity livingEntity = (LivingEntity) targetEntity;
            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 0.7f, 1.0f, 20);
            Location playerLocation = player.getLocation();
            Vector direction = playerLocation.getDirection().normalize();

            Location startLocation = playerLocation.clone().add(direction.multiply(3.3));

            final BukkitTask[] task = new BukkitTask[1];
            final BukkitTask[] taskk = new BukkitTask[1];
            final BukkitTask[] taskshift = new BukkitTask[1];
            final BukkitTask[] delayedTask = new BukkitTask[1];
            final BukkitTask[] partTask = new BukkitTask[1];
            player.setLastDamageCause(null);

            Bukkit.getScheduler().runTask(plugin, () -> {
                ArmorStand armorStand = player.getWorld().spawn(startLocation, ArmorStand.class, entity -> {
                    entity.setGravity(false);
                    entity.setVisible(false);
                    entity.setInvulnerable(true);
                    entity.setMarker(true);
                    entity.setAI(false);
                    entity.setCanPickupItems(false);
                    entity.addScoreboardTag("unmovable");
                });

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    Location finalLocation = playerLocation.clone().add(direction.multiply(3.3));
                    armorStand.teleport(finalLocation);

                    task[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                        Location playerLocationn = player.getLocation();
                        Location targetLocationn = playerLocationn.clone().add(playerLocationn.getDirection().normalize().multiply(3.3));
                        armorStand.teleport(targetLocationn);
                        if (player.getLastDamageCause() != null || livingEntity.isDead() || player.isDead()) {
                            playerAbilitiesJustice.put(playerId, false);
                            armorStand.remove();
                            task[0].cancel();
                            taskk[0].cancel();
                            taskshift[0].cancel();
                            player.setLastDamageCause(null);
                            delayedTask[0].cancel();
                            partTask[0].cancel();
                            justiceCooldownManager.setCooldown(player.getName(), justiceCooldownTime); // Устанавливаем кулдаун после успешного использования способности или при других событиях
                        } else if (targetEntity instanceof LivingEntity && armorStand.getLocation().distance(targetEntity.getLocation()) > 7) {
                            playerAbilitiesJustice.put(playerId, false);
                            armorStand.remove();
                            task[0].cancel();
                            taskk[0].cancel();
                            taskshift[0].cancel();
                            player.setLastDamageCause(null);
                            delayedTask[0].cancel();
                            partTask[0].cancel();
                            justiceCooldownManager.setCooldown(player.getName(), justiceCooldownTime);
                        }
                    }, 0, 1);

                    taskk[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                        Location armorStandOriginalLocation = armorStand.getLocation();
                        Vector directionToArmorStand = armorStandOriginalLocation.toVector().subtract(livingEntity.getLocation().toVector()).normalize();
                        if (Double.isFinite(directionToArmorStand.getX()) &&
                                Double.isFinite(directionToArmorStand.getY()) &&
                                Double.isFinite(directionToArmorStand.getZ())) {
                            livingEntity.setVelocity(directionToArmorStand.multiply(0.6));
                        }
                    }, 0, 2);

                    partTask[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(204, 204, 255), 1);
                        player.getWorld().spawnParticle(Particle.REDSTONE, targetEntity.getLocation().add(0, 1, 0), 10, 0.2, 0.5, 0.2, 0, dustOptions);
                    }, 0, 3);

                    taskshift[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                        if (player.isSneaking()) {
                            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                playerAbilitiesJustice.put(playerId, false);
                                armorStand.remove();
                                task[0].cancel();
                                taskk[0].cancel();
                                taskshift[0].cancel();
                                delayedTask[0].cancel();
                                partTask[0].cancel();
                                justiceCooldownManager.setCooldown(player.getName(), justiceCooldownTime); // Устанавливаем кулдаун после успешного использования способности или при других событиях
                            }, 1);
                        }
                    }, 0, 1);

                    delayedTask[0] = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        playerAbilitiesJustice.put(playerId, false);
                        armorStand.remove();
                        taskshift[0].cancel();
                        task[0].cancel();
                        taskk[0].cancel();
                        partTask[0].cancel();
                        justiceCooldownManager.setCooldown(player.getName(), justiceCooldownTime); // Устанавливаем кулдаун после успешного использования способности или при других событиях
                        delayedTask[0].cancel();
                    }, 200);

                }, 1);

            });
        }
    }
}