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
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
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

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;


public class Coil2 implements Listener {
    private final JavaPlugin plugin;
    private final ArtifactMenu artifactMenu;
    private static final int Coil2CooldownTime = 8;
    private static final int manauseg = 50;
    private final int cmduseg = 10004;
    private static final CooldownManager Coil2CooldownManager = new CooldownManager();

    public Coil2(JavaPlugin plugin) {
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

    public static void resetAllCooldownsCoil2(Player player) {
        Coil2CooldownManager.resetCooldown(player.getName());
    }

    public static ItemStack createCoil2Item() {
        ItemStack s4a = new ItemStack(Material.DIAMOND_HORSE_ARMOR);
        ItemMeta s4ameta = s4a.getItemMeta();
        s4ameta.setDisplayName(ChatColor.DARK_RED + "Coil 2");
        s4ameta.setCustomModelData(10004);
        List<String> lores4a = new ArrayList<>();
        lores4a.add(ChatColor.BLUE + "   При использовании заклинания");
        lores4a.add(ChatColor.BLUE + "   Игрок выпускает мощный всплеск в 4 метрах от себя");
        lores4a.add(ChatColor.BLUE + "   Который наносит урон и накладывает дэббафы ");
        lores4a.add("");
        lores4a.add(ChatColor.AQUA + "   Мана: " + ChatColor.GRAY + manauseg);
        lores4a.add(ChatColor.RED + "   Перезарядка: " + ChatColor.GRAY + Coil2CooldownTime);
        lores4a.add("");
        s4ameta.setLore(lores4a);
        s4a.setItemMeta(s4ameta);
        return s4a;
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
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !Coil2CooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    Coil2CooldownManager.setCooldown(player.getName(), Coil2CooldownTime);
                    event.setCancelled(true);
                    handleCoil2(player);
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
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !Coil2CooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    Coil2CooldownManager.setCooldown(player.getName(), Coil2CooldownTime);
                    event.setCancelled(true);
                    handleCoil2(player);
                    manaMechanics.addSecondMana(-manauseg);
                }
                else {
                    NoManaPlayer.displayNoManaMessage(player);
                }
            }
        } else if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !Coil2CooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    Coil2CooldownManager.setCooldown(player.getName(), Coil2CooldownTime);
                    event.setCancelled(true);
                    handleCoil2(player);
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
        if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !Coil2CooldownManager.hasCooldown(player.getName())) {
            if (Silence.getSilenceState(player)>= 1) {
                event.setCancelled(true);
                NoManaPlayer.displaySilenceMessage(player);
                return;
            }
            if (secondManaUse >= manauseg) {
                Coil2CooldownManager.setCooldown(player.getName(), Coil2CooldownTime);
                event.setCancelled(true);
                handleCoil2(player);
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
                if (Coil2CooldownManager.hasCooldown(onlinePlayer.getName())) {
                    int remainingTime = (int) ((Coil2CooldownManager.cooldowns.get(onlinePlayer.getName()) - System.currentTimeMillis()) / 1000);
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + ""  + manauseg + ChatColor.GRAY + " | " + ChatColor.RED + "Средний коил" + ChatColor.GRAY + " | КД: " + remainingTime + " сек.");
                } else {
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + "" + manauseg + ChatColor.GRAY + " | " + ChatColor.YELLOW + "Средний коил" + ChatColor.GRAY + " |");
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

    private void spawnParticleAroundArmorStand(ArmorStand armorStand, double radius, int numParticles, double heightOffset, int numRings) {
        Location armorStandLocation = armorStand.getLocation();

        double angleStep = 2 * Math.PI / numParticles;

        double ringHeight = heightOffset / numRings;

        for (int ring = 0; ring < numRings; ring++) {
            for (int i = 0; i < numParticles; i++) {
                double theta = i * angleStep;


                double particleX = radius * Math.cos(theta);
                double particleZ = radius * Math.sin(theta);

                double particleY = ring * ringHeight;

                Vector particleDirection = new Vector(particleX, particleY, particleZ);

                Location particleLocation = armorStandLocation.clone().add(particleDirection);

                Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(153, 0, 0), 1);

                armorStand.getWorld().spawnParticle(Particle.REDSTONE, particleLocation, 1, 0, 0, 0, 0, dustOptions);
            }
        }
    }

    private void handleCoil2(Player player) {
        Location playerLocation = player.getLocation();
        Vector direction = playerLocation.getDirection().normalize();

        Location armorStandLocation = playerLocation.add(direction.multiply(5));

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

        spawnParticleAroundArmorStand(armorStand, 1.8, 80, 5.0, 24);



        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Entity nearbyEntity : armorStand.getNearbyEntities(1.8, 2, 1.8)) {
                if (nearbyEntity instanceof LivingEntity && !nearbyEntity.equals(player)) {
                    LivingEntity livingEntity = (LivingEntity) nearbyEntity;
                    livingEntity.damage(10);

                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 2, false,false));
                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 40, 0, false,false));
                }
            }
            armorStand.remove();
        }, 1);

        SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1.0f, 1.0f, 10);
    }

}