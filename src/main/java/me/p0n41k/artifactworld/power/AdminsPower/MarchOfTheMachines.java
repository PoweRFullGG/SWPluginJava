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

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;


public class MarchOfTheMachines implements Listener {
    private final JavaPlugin plugin;
    private final ArtifactMenu artifactMenu;
    private static final int marchCooldownTime = 35;
    private static final int manauseg = 70;
    private final int cmduseg = 10008;
    private static final CooldownManager marchCooldownManager = new CooldownManager();

    private final Map<ArmorStand, HashSet<UUID>> damagedEntitiesMap = new HashMap<>();

    public MarchOfTheMachines(JavaPlugin plugin) {
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

    public static void resetAllCooldownsMarch(Player player) {
        marchCooldownManager.resetCooldown(player.getName());
    }

    public static ItemStack createMarchOfTheMachinesItem() {
        ItemStack s8a = new ItemStack(Material.DIAMOND_HORSE_ARMOR);
        ItemMeta s8ameta = s8a.getItemMeta();
        s8ameta.setDisplayName(ChatColor.DARK_RED + "Марши");
        s8ameta.setCustomModelData(10008);
        List<String> lores8a = new ArrayList<>();
        lores8a.add(ChatColor.BLUE + "   При использовании заклинания");
        lores8a.add(ChatColor.BLUE + "   Игрок выпускает армию хаотично ползающих");
        lores8a.add(ChatColor.BLUE + "   Роботов которые наносят урон ");
        lores8a.add("");
        lores8a.add(ChatColor.AQUA + "   Мана: " + ChatColor.GRAY + manauseg);
        lores8a.add(ChatColor.RED + "   Перезарядка: " + ChatColor.GRAY + marchCooldownTime);
        lores8a.add("");
        s8ameta.setLore(lores8a);
        s8a.setItemMeta(s8ameta);
        return s8a;
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
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !marchCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    marchCooldownManager.setCooldown(player.getName(), marchCooldownTime);
                    event.setCancelled(true);
                    handleMarch(player);
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
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !marchCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    marchCooldownManager.setCooldown(player.getName(), marchCooldownTime);
                    event.setCancelled(true);
                    handleMarch(player);
                    manaMechanics.addSecondMana(-manauseg);
                }
                else {
                    NoManaPlayer.displayNoManaMessage(player);
                }
            }
        } else if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !marchCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    marchCooldownManager.setCooldown(player.getName(), marchCooldownTime);
                    event.setCancelled(true);
                    handleMarch(player);
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
        if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !marchCooldownManager.hasCooldown(player.getName())) {
            if (Silence.getSilenceState(player)>= 1) {
                event.setCancelled(true);
                NoManaPlayer.displaySilenceMessage(player);
                return;
            }
            if (secondManaUse >= manauseg) {
                marchCooldownManager.setCooldown(player.getName(), marchCooldownTime);
                event.setCancelled(true);
                handleMarch(player);
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
                if (marchCooldownManager.hasCooldown(onlinePlayer.getName())) {
                    int remainingTime = (int) ((marchCooldownManager.cooldowns.get(onlinePlayer.getName()) - System.currentTimeMillis()) / 1000);
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + ""  + manauseg + ChatColor.GRAY + " | " + ChatColor.RED + "Марши" + ChatColor.GRAY + " | КД: " + remainingTime + " сек.");
                } else {
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + "" + manauseg + ChatColor.GRAY + " | " + ChatColor.YELLOW + "Марши" + ChatColor.GRAY + " |");
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

    private void handleMarch(Player player) {
        SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 0.3f, 1.0f, 10);
        Location playerLocation = player.getLocation();
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            spawnRandomMarch(player, playerLocation);
            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_IRON_GOLEM_STEP, 0.8f, 1.0f, 20);
        }, 0, 3);

        Bukkit.getScheduler().runTaskLater(plugin, task::cancel, 300);
    }

    private void spawnRandomMarch(Player player, Location playerLocation) {

        double randomXOffset = (Math.random() * 14) - 7;
        double randomZOffset = (Math.random() * 14) - 7;

        Location armorStandLocation = playerLocation.clone().add(randomXOffset, 0, randomZOffset);
        armorStandLocation.subtract(0, 0.7, 0);

        ArmorStand armorStand = player.getWorld().spawn(armorStandLocation, ArmorStand.class, entity -> {
            entity.setHelmet(new ItemStack(Material.PIGLIN_HEAD));
            entity.setGravity(false);
            entity.setVisible(false);
            entity.setInvulnerable(true);
            entity.setMarker(true);
            entity.setSmall(true);
            entity.setRotation(playerLocation.getYaw(), 0);
        });

        damagedEntitiesMap.put(armorStand, new HashSet<>());

        float yaw = playerLocation.getYaw();
        double yawAngle = Math.toRadians(yaw);
        double x = -Math.sin(yawAngle);
        double z = Math.cos(yawAngle);
        Vector direction = new Vector(x, 0, z).normalize().multiply(0.2);

        Player owner = player;

        BukkitTask moveTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            moveArmorStand(armorStand, direction);

            HashSet<UUID> damagedEntities = damagedEntitiesMap.get(armorStand);

            for (Entity nearbyEntity : armorStand.getNearbyEntities(0.5, 2.3, 0.5)) {
                if (nearbyEntity instanceof LivingEntity && !nearbyEntity.equals(owner)) {
                    LivingEntity livingEntity = (LivingEntity) nearbyEntity;
                    if (!damagedEntities.contains(livingEntity.getUniqueId())) {
                        damagedEntities.add(livingEntity.getUniqueId());
                        livingEntity.setNoDamageTicks(0);
                        livingEntity.damage(1.0);
                    }
                }
            }
        }, 0, 1);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            armorStand.remove();
            moveTask.cancel();
            damagedEntitiesMap.remove(armorStand);
        }, 75);
    }

    private void moveArmorStand(ArmorStand armorStand, Vector direction) {
        armorStand.teleport(armorStand.getLocation().add(direction));
    }




}