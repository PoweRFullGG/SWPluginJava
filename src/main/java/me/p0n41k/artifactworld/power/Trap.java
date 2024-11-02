package me.p0n41k.artifactworld.power;

import me.p0n41k.artifactworld.Artifactworld;
import me.p0n41k.artifactworld.NoManaPlayer;
import me.p0n41k.artifactworld.artmenu.ArtifactMenu;
import me.p0n41k.artifactworld.power.Mechanics.CooldownManager;
import me.p0n41k.artifactworld.power.Mechanics.Silence;
import me.p0n41k.artifactworld.power.Mechanics.SoundUtil;
import org.bukkit.*;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Trap implements Listener {
    private final JavaPlugin plugin;
    private final ArtifactMenu artifactMenu;
    private static final int trapCooldownTime = 4;
    private static final int manauseg = 45;
    private final int cmduseg = 106;
    private static final CooldownManager trapCooldownManager = new CooldownManager();

    private final Map<Player, Boolean> trapActivated = new HashMap<>();

    private Map<Player, BukkitTask> trapTasks = new HashMap<>();
    private final Map<Player, ArmorStand> trapArmorStands = new HashMap<>();

    public boolean getTrapActivatedState(Player player) {
        return trapActivated.getOrDefault(player, false);
    }

    public Trap(JavaPlugin plugin) {
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

    public static void resetAllCooldownsTrap(Player player) {
        trapCooldownManager.resetCooldown(player.getName());
    }

    public static ItemStack createTrapItem() {
        ItemStack s5 = new ItemStack(Material.DIAMOND_HORSE_ARMOR);
        ItemMeta s5meta = s5.getItemMeta();
        s5meta.setDisplayName(ChatColor.YELLOW + "Ловушка");
        s5meta.setCustomModelData(106);
        List<String> lores5 = new ArrayList<>();
        lores5.add(ChatColor.BLUE + "   Устанавливает оглушающую противника");
        lores5.add(ChatColor.BLUE + "   ловушку.");
        lores5.add("");
        lores5.add(ChatColor.AQUA + "   Мана: " + ChatColor.GRAY + manauseg);
        lores5.add(ChatColor.RED + "   Перезарядка: " + ChatColor.GRAY + trapCooldownTime);
        lores5.add("");
        s5meta.setLore(lores5);
        s5.setItemMeta(s5meta);
        return s5;
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
        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) || (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !trapCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg && !getTrapActivatedState(player)) {
                    event.setCancelled(true);
                    handleTrap(player, event.getClickedBlock());
                } else if (getTrapActivatedState(player)) {
                    event.setCancelled(true);
                    handleTrap(player, event.getClickedBlock());
                }
                else {
                    NoManaPlayer.displayNoManaMessage(player);
                }
            }
        }

    }

    public void checkSlot() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {

            String actionBarMessage = "";

            Integer hasArtifact = artifactMenu.checkArtifactMenu(onlinePlayer, cmduseg);
            List<Integer> getspellslots =  artifactMenu.getDiamondHorseArmorSlots(onlinePlayer);

            if (hasArtifact == null) {
                ArmorStand armorStand = trapArmorStands.get(onlinePlayer);
                BukkitTask trapTask = trapTasks.get(onlinePlayer);
                if (armorStand != null) {
                    armorStand.remove();
                    trapArmorStands.remove(onlinePlayer);
                }
                if (trapTask != null) {
                    trapTask.cancel();
                    trapTasks.remove(onlinePlayer);
                }
                trapActivated.remove(onlinePlayer);
                continue;
            }

            int heldItemSlot = onlinePlayer.getInventory().getHeldItemSlot();
            if (heldItemSlot == hasArtifact-9) {
                if (trapCooldownManager.hasCooldown(onlinePlayer.getName())) {
                    int remainingTime = (int) ((trapCooldownManager.cooldowns.get(onlinePlayer.getName()) - System.currentTimeMillis()) / 1000);
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + ""  + manauseg + ChatColor.GRAY + " | " + ChatColor.RED + "Ловушка" + ChatColor.GRAY + " | КД: " + remainingTime + " сек.");
                } else {
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + "" + manauseg + ChatColor.GRAY + " | " + ChatColor.YELLOW + "Ловушка" + ChatColor.GRAY + " |");
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

    private void spawnParticleAroundArmorStand(Player player, ArmorStand armorStand, double radius, int numParticles, double heightOffset, int numRings) {
        Location armorStandLocation = armorStand.getLocation();
        World world = armorStand.getWorld();

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

                Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(153, 76, 0), 1);

                player.spawnParticle(Particle.REDSTONE, particleLocation, 1, 0, 0, 0, 0, dustOptions);
            }
        }
    }
    private void spawnParticleAroundArmorStandAll(Player player, ArmorStand armorStand, double radius, int numParticles, double heightOffset, int numRings) {
        Location armorStandLocation = armorStand.getLocation();
        World world = armorStand.getWorld();

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

                Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(153, 76, 0), 1);

                player.getWorld().spawnParticle(Particle.REDSTONE, particleLocation, 1, 0, 0, 0, 0, dustOptions);
            }
        }
    }

    private void handleTrap(Player player, Block clickedBlock) {
        final BukkitTask[] task = new BukkitTask[1];
        if (!getTrapActivatedState(player)) {
            if (clickedBlock == null) {
                return;
            }
            if (!clickedBlock.getType().equals(Material.AIR)) {
                Location location = clickedBlock.getLocation().add(0.5, 1, 0.5);

                ArmorStand armorStand = player.getWorld().spawn(location, ArmorStand.class, entity -> {
                    entity.setGravity(false);
                    entity.setVisible(false);
                    entity.setInvulnerable(true);
                    entity.setMarker(true);
                    entity.setAI(false);
                    entity.setCanPickupItems(false);
                    entity.addScoreboardTag("unmovable");
                });

                task[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                    spawnParticleAroundArmorStand(player, armorStand, 2.4, 70, 0.2, 2);
                }, 0, 6);

                trapActivated.put(player, true);
                trapArmorStands.put(player, armorStand);
                trapTasks.put(player, task[0]);
                SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 0.9f, 1.0f, 1);
                ManaMechanics manaMechanics = new ManaMechanics(player.getUniqueId());
                manaMechanics.addSecondMana(-manauseg);
                trapCooldownManager.setCooldown(player.getName(), trapCooldownTime);
            }
        } else if (getTrapActivatedState(player)) {
            ArmorStand armorStand = trapArmorStands.get(player);
            BukkitTask trapTask = trapTasks.get(player);
            for (Entity nearbyEntity : armorStand.getNearbyEntities(2.5, 1.8, 2.5)) {
                if (nearbyEntity instanceof LivingEntity && !nearbyEntity.equals(player)) {
                    LivingEntity livingEntity = (LivingEntity) nearbyEntity;
                    livingEntity.damage(7);
                    stuntrap(livingEntity);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 160, 1,false,false));
                    }, 45);

                }
            }

            double radiusstart = 2.4;
            spawnParticleAroundArmorStandAll(player, armorStand, radiusstart, 70, 0.2, 2);
            spawnParticleAroundArmorStandAll(player, armorStand, 2.0, 70, 0.2, 2);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                spawnParticleAroundArmorStandAll(player, armorStand, 1.5, 70, 0.2, 2);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    spawnParticleAroundArmorStandAll(player, armorStand, 1.0, 70, 0.2, 2);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        spawnParticleAroundArmorStandAll(player, armorStand, 0.5, 70, 0.2, 2);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            SoundUtil.playSoundForNearbyPlayers(armorStand.getLocation(), Sound.BLOCK_PISTON_EXTEND, 0.9f, 1.0f, 10);
                            spawnParticleAroundArmorStandAll(player, armorStand, 0.1, 70, 0.2, 2);
                        }, 1);
                    }, 1);
                }, 1);
            }, 1);

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (armorStand != null) {
                    armorStand.remove();
                    trapArmorStands.remove(player);
                }
            }, 5);
            if (trapTask != null) {
                trapTask.cancel();
                trapTasks.remove(player);
            }
            trapActivated.remove(player);
            trapCooldownManager.setCooldown(player.getName(), trapCooldownTime);
        }
    }

    private void stuntrap(Entity entity) {
        if (entity instanceof Player) {
            final BukkitTask[] taskend = new BukkitTask[1];
            final BukkitTask[] taskId = new BukkitTask[1];
            Player player = (Player) entity;
            Location playerLocation = player.getLocation();
            player.setWalkSpeed(0);
            player.setFlySpeed(0);
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 55, 1,false,false));

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
                           new Particle.DustOptions(Color.fromRGB(204, 102, 0), 1));

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
            }, 60);

        } else if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            livingEntity.setAI(false);
            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 55, 1, false, false));
            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 55, 1, false, false));

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                livingEntity.setAI(true);
            }, 60);
        }
    }

}