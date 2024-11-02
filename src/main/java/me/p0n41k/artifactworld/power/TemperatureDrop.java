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
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
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
import java.util.concurrent.atomic.AtomicReference;


public class TemperatureDrop implements Listener {
    private final JavaPlugin plugin;
    private final ArtifactMenu artifactMenu;
    private static final int temperatureDropCooldownTime = 90;
    private static final int manauseg = 80;
    private final int cmduseg = 123;
    private static final CooldownManager temperatureDropCooldownManager = new CooldownManager();

    public TemperatureDrop(JavaPlugin plugin) {
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

    public static void resetAllCooldownsTemperatureDrop(Player player) {
        temperatureDropCooldownManager.resetCooldown(player.getName());
    }

    public static ItemStack createTemperatureDropItem() {
        ItemStack s21 = new ItemStack(Material.DIAMOND_HORSE_ARMOR);
        ItemMeta s21meta = s21.getItemMeta();
        s21meta.setDisplayName(ChatColor.YELLOW + "Понижение Температуры");
        s21meta.setCustomModelData(123);
        List<String> lores21 = new ArrayList<>();
        lores21.add(ChatColor.BLUE + "   Охлаждает воздух вокруг игрока, замораживая");
        lores21.add(ChatColor.BLUE + "   противников.");
        lores21.add("");
        lores21.add(ChatColor.AQUA + "   Мана: " + ChatColor.GRAY + manauseg);
        lores21.add(ChatColor.RED + "   Перезарядка: " + ChatColor.GRAY + temperatureDropCooldownTime);
        lores21.add("");
        s21meta.setLore(lores21);
        s21.setItemMeta(s21meta);
        return s21;
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
            if (player.getInventory().getHeldItemSlot() == hasArtifact - 9 && !temperatureDropCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    temperatureDropCooldownManager.setCooldown(player.getName(), temperatureDropCooldownTime);
                    event.setCancelled(true);
                    handleTemperatureDrop(player);
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
            if (player.getInventory().getHeldItemSlot() == hasArtifact - 9 && !temperatureDropCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    temperatureDropCooldownManager.setCooldown(player.getName(), temperatureDropCooldownTime);
                    event.setCancelled(true);
                    handleTemperatureDrop(player);
                    manaMechanics.addSecondMana(-manauseg);
                } else {
                    NoManaPlayer.displayNoManaMessage(player);
                }
            }
        } else if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
            if (player.getInventory().getHeldItemSlot() == hasArtifact - 9 && !temperatureDropCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    temperatureDropCooldownManager.setCooldown(player.getName(), temperatureDropCooldownTime);
                    event.setCancelled(true);
                    handleTemperatureDrop(player);
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
        if (player.getInventory().getHeldItemSlot() == hasArtifact - 9 && !temperatureDropCooldownManager.hasCooldown(player.getName())) {
            if (Silence.getSilenceState(player)>= 1) {
                event.setCancelled(true);
                NoManaPlayer.displaySilenceMessage(player);
                return;
            }
            if (secondManaUse >= manauseg) {
                temperatureDropCooldownManager.setCooldown(player.getName(), temperatureDropCooldownTime);
                event.setCancelled(true);
                handleTemperatureDrop(player);
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
                if (temperatureDropCooldownManager.hasCooldown(onlinePlayer.getName())) {
                    int remainingTime = (int) ((temperatureDropCooldownManager.cooldowns.get(onlinePlayer.getName()) - System.currentTimeMillis()) / 1000);
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + "" + manauseg + ChatColor.GRAY + " | " + ChatColor.RED + "Понижение температуры" + ChatColor.GRAY + " | КД: " + remainingTime + " сек.");
                } else {
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + "" + manauseg + ChatColor.GRAY + " | " + ChatColor.YELLOW + "Понижение температуры" + ChatColor.GRAY + " |");
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

    private void handleTemperatureDrop(Player player) {
        SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_WITHER_AMBIENT, 0.2f, 0.2f, 12);
        SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 0.5f, 0.7f, 12);
        final BukkitTask[] task = new BukkitTask[1];
        final BukkitTask[] taskPar = new BukkitTask[1];

        Map<LivingEntity, Integer> freezeMap = new HashMap<>();

        ArmorStand armorStand = player.getWorld().spawn(player.getLocation(), ArmorStand.class, entity -> {
            entity.setGravity(false);
            entity.setVisible(false);
            entity.setInvulnerable(true);
            entity.setMarker(true);
            entity.setAI(false);
            entity.setCanPickupItems(false);
            entity.addScoreboardTag("unmovable");
        });

        Map<Player, Boolean> playersInArea = new HashMap<>();

        Listener damageListener = new Listener() {
            @EventHandler
            public void onEntityDamage(EntityDamageEvent event) {
                if (event.getEntity() instanceof LivingEntity) {
                    LivingEntity damagedEntity = (LivingEntity) event.getEntity();

                    if (!damagedEntity.equals(player)) {
                        double distance = damagedEntity.getLocation().distance(armorStand.getLocation());

                        if (distance <= 5.0) {
                            damagedEntity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1, false, false));
                            damagedEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 1, false, false));
                        }
                    }
                }
            }
        };

        Bukkit.getPluginManager().registerEvents(damageListener, plugin);

        task[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            armorStand.teleport(player.getLocation());
            for (Entity nearbyEntity : armorStand.getNearbyEntities(5.0, 5.0, 5.0)) {
                if (nearbyEntity instanceof LivingEntity && !nearbyEntity.equals(player)) {
                    LivingEntity livingEntity = (LivingEntity) nearbyEntity;

                    if (livingEntity instanceof Player) {
                        Player nearbyPlayer = (Player) livingEntity;
                        if (!playersInArea.getOrDefault(nearbyPlayer, false)) {
                            playersInArea.put(nearbyPlayer, true);
                        }
                    }

                    double radius = 0.5;
                    double angle = Math.random() * 2 * Math.PI;

                    double x = livingEntity.getLocation().getX() + radius * Math.cos(angle);
                    double y = livingEntity.getLocation().getY() + Math.random() * 2;
                    double z = livingEntity.getLocation().getZ() + radius * Math.sin(angle);

                    livingEntity.getWorld().spawnParticle(
                            Particle.SNOWFLAKE,
                            new Location(livingEntity.getWorld(), x, y, z),
                            1,
                            0, 0, 0,
                            0.04
                    );

                    freezeMap.putIfAbsent(livingEntity, 0);
                    int currentFreezeTicksPL = livingEntity.getFreezeTicks();
                    int currentFreezeTicks = freezeMap.get(livingEntity);

                    if (currentFreezeTicksPL < 150) {
                        currentFreezeTicks += 2;
                        freezeMap.put(livingEntity, currentFreezeTicks);
                        livingEntity.setFreezeTicks(currentFreezeTicks);
                    }
                }
            }

            for (Player onlinePlayer : playersInArea.keySet()) {
                if (playersInArea.get(onlinePlayer)) {
                    double distance = armorStand.getLocation().distance(onlinePlayer.getLocation());
                    if (distance > 5.0) {
                        freezeMap.put(onlinePlayer, 0);
                        playersInArea.put(onlinePlayer, false);
                    }
                }
            }
        }, 0L, 1L);

        taskPar[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            particleFunc(armorStand, 3.5);
        }, 0, 6);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            taskPar[0].cancel();
            task[0].cancel();
            armorStand.remove();

            HandlerList.unregisterAll(damageListener);
        }, 20 * 20);
    }


    private void particleFunc(ArmorStand armorStand, double finalRadius) {
        World world = armorStand.getWorld();
        Location center = armorStand.getLocation();

        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(170, 200, 255), 1);

        for (int i = 0; i < 20; i++) {
            double offsetX = (Math.random() * 2 - 1) * finalRadius;
            double offsetY = (Math.random() * 2 - 1) * finalRadius;
            double offsetZ = (Math.random() * 2 - 1) * finalRadius;

            // Положение для каждой частицы
            Location particleLocation = center.clone().add(offsetX, offsetY, offsetZ);

            // Спавним частицу
            world.spawnParticle(Particle.END_ROD, particleLocation,1, 0, 0, 0, 0.08);
        }
    }


}