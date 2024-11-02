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


public class LightningSpeed implements Listener {
    private final JavaPlugin plugin;
    private final ArtifactMenu artifactMenu;
    private static final int lightningSpeedCooldownTime = 55;
    private static final int manauseg = 95;
    private final int cmduseg = 103;
    private static final CooldownManager lightningSpeedCooldownManager = new CooldownManager();

    public LightningSpeed(JavaPlugin plugin) {
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

    public static void resetAllCooldownsLightningSpeed(Player player) {
        lightningSpeedCooldownManager.resetCooldown(player.getName());
    }

    public static ItemStack createLightningSpeedItem() {
        ItemStack s2 = new ItemStack(Material.DIAMOND_HORSE_ARMOR);
        ItemMeta s2meta = s2.getItemMeta();
        s2meta.setDisplayName(ChatColor.YELLOW + "Заряд Скорости");
        s2meta.setCustomModelData(103);
        List<String> lores2 = new ArrayList<>();
        lores2.add(ChatColor.BLUE + "   Призывает молнию, дарующую огромную");
        lores2.add(ChatColor.BLUE + "   скорость игроку.");
        lores2.add("");
        lores2.add(ChatColor.AQUA + "   Мана: " + ChatColor.GRAY + manauseg);
        lores2.add(ChatColor.RED + "   Перезарядка: " + ChatColor.GRAY + lightningSpeedCooldownTime);
        lores2.add("");
        s2meta.setLore(lores2);
        s2.setItemMeta(s2meta);
        return s2;
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
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !lightningSpeedCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    event.setCancelled(true);
                    handleLightningSpeed(player);
                    manaMechanics.addSecondMana(-manauseg);
                    lightningSpeedCooldownManager.setCooldown(player.getName(), lightningSpeedCooldownTime);
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
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !lightningSpeedCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    event.setCancelled(true);
                    handleLightningSpeed(player);
                    manaMechanics.addSecondMana(-manauseg);
                    lightningSpeedCooldownManager.setCooldown(player.getName(), lightningSpeedCooldownTime);
                }
                else {
                    NoManaPlayer.displayNoManaMessage(player);
                }
            }
        } else if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !lightningSpeedCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    event.setCancelled(true);
                    handleLightningSpeed(player);
                    manaMechanics.addSecondMana(-manauseg);
                    lightningSpeedCooldownManager.setCooldown(player.getName(), lightningSpeedCooldownTime);
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
        if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !lightningSpeedCooldownManager.hasCooldown(player.getName())) {
            if (Silence.getSilenceState(player)>= 1) {
                event.setCancelled(true);
                NoManaPlayer.displaySilenceMessage(player);
                return;
            }
            if (secondManaUse >= manauseg) {
                event.setCancelled(true);
                handleLightningSpeed(player);
                manaMechanics.addSecondMana(-manauseg);
                lightningSpeedCooldownManager.setCooldown(player.getName(), lightningSpeedCooldownTime);
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
                if (lightningSpeedCooldownManager.hasCooldown(onlinePlayer.getName())) {
                    int remainingTime = (int) ((lightningSpeedCooldownManager.cooldowns.get(onlinePlayer.getName()) - System.currentTimeMillis()) / 1000);
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + ""  + manauseg + ChatColor.GRAY + " | " + ChatColor.RED + "Заряд скорости" + ChatColor.GRAY + " | КД: " + remainingTime + " сек.");
                } else {
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + "" + manauseg + ChatColor.GRAY + " | " + ChatColor.YELLOW + "Заряд скорости" + ChatColor.GRAY + " |");
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


    private void spawnLightningSpeedParticle(Player player, Vector particleDirection) {
        Location playerLocation = player.getLocation();

        double particleSpacing = 0.13;

        double startY = playerLocation.getY() + 1.3;

        double endY = playerLocation.getY() - 0.1;

        Color[] rainbowColors = {
                Color.fromRGB(51,153,255),
                Color.fromRGB(0,76,153),
                Color.fromRGB(51,153,255),
                Color.fromRGB(0,76,153),
                Color.fromRGB(51,153,255)
        };

        double colorStep = (startY - endY) / rainbowColors.length;

        for (int i = 0; i < rainbowColors.length; i++) {
            double currentY = startY - i * colorStep;
            Color currentColor = rainbowColors[i];

            Location particleLocation = new Location(playerLocation.getWorld(), playerLocation.getX(), currentY, playerLocation.getZ());
            Particle.DustOptions dustOptions = new Particle.DustOptions(currentColor, 1);
            playerLocation.getWorld().spawnParticle(Particle.REDSTONE, particleLocation, 1, particleSpacing, particleSpacing, particleSpacing, 0, dustOptions);
        }
    }

    private void stun(Entity entity) {
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

            taskId[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                if (player.isDead()){
                    player.setWalkSpeed(0.2f);
                    player.setFlySpeed(0.1f);
                    HandlerList.unregisterAll(moveListener);
                    taskend[0].cancel();
                    taskId[0].cancel();
                } else {
                    double radius = 0.5;
                    double angle = Math.random() * 2 * Math.PI;

                    double x = player.getLocation().getX() + radius * Math.cos(angle);
                    double y = player.getLocation().getY() + Math.random() * 2;
                    double z = player.getLocation().getZ() + radius * Math.sin(angle);

                    player.getWorld().spawnParticle(Particle.REDSTONE, new Location(player.getWorld(), x, y, z), 3,
                            new Particle.DustOptions(Color.fromRGB(0, 128, 255), 1));

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
            }, 15);
        }
    }

    private void handleLightningSpeed(Player player) {
        SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 0.6f, 1.0f, 7);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 0.6f, 1.0f, 7);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 0.6f, 1.0f, 7);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 0.6f, 1.0f, 7);
                }, 20);
            }, 20);
        }, 20);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            final BukkitTask[] task = new BukkitTask[1];
            final BukkitTask[] delayedTask = new BukkitTask[1];
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 220, 7, false, false));
            Location location = player.getLocation();

            player.getWorld().strikeLightningEffect(location);
            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.1f, 1.0f, 20);

            location.getWorld().createExplosion(location.getX(), location.getY(), location.getZ(), 0.0f, false, false);
            stun(player);
            player.damage(5);

            task[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 0.5) {
                    for (double phi = 0; phi <= Math.PI; phi += Math.PI / 0.5) {
                        double particleX = 0.1 * Math.sin(phi) * Math.cos(theta);
                        double particleY = 0.1 * Math.cos(phi);
                        double particleZ = 0.1 * Math.sin(phi) * Math.sin(theta);

                        Vector particleDirection = new Vector(particleX, particleY, particleZ);
                        spawnLightningSpeedParticle(player, particleDirection);
                    }
                }

                if (player.isDead()){
                    task[0].cancel();
                    delayedTask[0].cancel();
                }
            }, 0, 1);
            delayedTask[0] = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                task[0].cancel();
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 160, 1, false, false));
            }, 220);
        }, 60);
    }
}