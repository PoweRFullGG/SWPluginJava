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
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


public class Laser implements Listener {
    private final JavaPlugin plugin;
    private final ArtifactMenu artifactMenu;
    private static final int laserCooldownTime = 18;
    private static final int manauseg = 40;
    private final int cmduseg = 10007;
    private static final CooldownManager laserCooldownManager = new CooldownManager();

    public Laser(JavaPlugin plugin) {
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

    public static void resetAllCooldownsLaser(Player player) {
        laserCooldownManager.resetCooldown(player.getName());
    }

    public static ItemStack createLaserItem() {
        ItemStack s7a = new ItemStack(Material.DIAMOND_HORSE_ARMOR);
        ItemMeta s7ameta = s7a.getItemMeta();
        s7ameta.setDisplayName(ChatColor.DARK_RED + "Лазер");
        s7ameta.setCustomModelData(10007);
        List<String> lores7a = new ArrayList<>();
        lores7a.add(ChatColor.BLUE + "   При использовании заклинания");
        lores7a.add(ChatColor.BLUE + "   Игрок выпускает лазер который");
        lores7a.add(ChatColor.BLUE + "   При попадании наносит урон и накладывает слабость ");
        lores7a.add("");
        lores7a.add(ChatColor.AQUA + "   Мана: " + ChatColor.GRAY + manauseg);
        lores7a.add(ChatColor.RED + "   Перезарядка: " + ChatColor.GRAY + laserCooldownTime);
        lores7a.add("");
        s7ameta.setLore(lores7a);
        s7a.setItemMeta(s7ameta);
        return s7a;
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
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !laserCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    laserCooldownManager.setCooldown(player.getName(), laserCooldownTime);
                    event.setCancelled(true);
                    handleLaser(player);
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
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !laserCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    laserCooldownManager.setCooldown(player.getName(), laserCooldownTime);
                    event.setCancelled(true);
                    handleLaser(player);
                    manaMechanics.addSecondMana(-manauseg);
                }
                else {
                    NoManaPlayer.displayNoManaMessage(player);
                }
            }
        } else if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !laserCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    laserCooldownManager.setCooldown(player.getName(), laserCooldownTime);
                    event.setCancelled(true);
                    handleLaser(player);
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
        if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !laserCooldownManager.hasCooldown(player.getName())) {
            if (Silence.getSilenceState(player)>= 1) {
                event.setCancelled(true);
                NoManaPlayer.displaySilenceMessage(player);
                return;
            }
            if (secondManaUse >= manauseg) {
                laserCooldownManager.setCooldown(player.getName(), laserCooldownTime);
                event.setCancelled(true);
                handleLaser(player);
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
                if (laserCooldownManager.hasCooldown(onlinePlayer.getName())) {
                    int remainingTime = (int) ((laserCooldownManager.cooldowns.get(onlinePlayer.getName()) - System.currentTimeMillis()) / 1000);
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + ""  + manauseg + ChatColor.GRAY + " | " + ChatColor.RED + "Лазер" + ChatColor.GRAY + " | КД: " + remainingTime + " сек.");
                } else {
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + "" + manauseg + ChatColor.GRAY + " | " + ChatColor.YELLOW + "Лазер" + ChatColor.GRAY + " |");
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

    private void handleLaser(Player player) {
        Location playerLocation = player.getLocation();
        Location armorStandLocation = new Location(playerLocation.getWorld(), playerLocation.getX(), playerLocation.getY() + 1.5, playerLocation.getZ());

        ArmorStand armorStand = player.getWorld().spawn(armorStandLocation, ArmorStand.class, entity -> {
            entity.setVisible(false);
            entity.setGravity(false);
            entity.setSmall(true);
            entity.setInvulnerable(true);
        });

        SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_GHAST_SHOOT, 0.9f, 1.0f, 10);

        Vector direction = player.getLocation().getDirection().multiply(20);

        Location endLocation = armorStandLocation.clone().add(direction);
        endLocation.setYaw(playerLocation.getYaw());
        endLocation.setPitch(playerLocation.getPitch());

        RayTraceResult result = player.getWorld().rayTraceBlocks(armorStandLocation, direction.normalize(), 20);
        if (result != null && result.getHitBlock() != null) {
            endLocation = result.getHitPosition().toLocation(player.getWorld());
        }

        spawnParticleLine(armorStandLocation, endLocation, player);

        armorStand.remove();
    }

    private void spawnParticleLine(Location startLocation, Location endLocation, Player player) {
        double particleCount = 250;
        Vector direction = endLocation.toVector().subtract(startLocation.toVector()).normalize().multiply(1.0 / particleCount);

        for (int i = 0; i < particleCount; i++) {
            Location particleLocation = interpolateLocation(startLocation, endLocation, (double) i / particleCount);
            spawnParticle(particleLocation);

            for (Entity nearbyEntity : particleLocation.getWorld().getNearbyEntities(particleLocation, 0.2, 0.2, 0.2)) {
                if (nearbyEntity instanceof LivingEntity && !nearbyEntity.equals(player)) {
                    LivingEntity livingEntity = (LivingEntity) nearbyEntity;
                    livingEntity.damage(6);
                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 80, 69, false, false));
                }
            }
        }
    }


    private Location interpolateLocation(Location start, Location end, double ratio) {
        double x = start.getX() + (end.getX() - start.getX()) * ratio;
        double y = start.getY() + (end.getY() - start.getY()) * ratio;
        double z = start.getZ() + (end.getZ() - start.getZ()) * ratio;
        return new Location(start.getWorld(), x, y, z);
    }

    private void spawnParticle(Location location) {
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(240, 0, 0), 1);
        location.getWorld().spawnParticle(Particle.REDSTONE, location, 1, 0, 0, 0, 0, dustOptions);
    }
}