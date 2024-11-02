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
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import me.p0n41k.artifactworld.data.ManaMechanics;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;


public class KeenConveyance implements Listener {
    private final JavaPlugin plugin;
    private final ArtifactMenu artifactMenu;
    private static final int keenConveyanceCooldownTime = 80;
    private static final int manauseg = 85;
    private final int cmduseg = 10009;
    private static final CooldownManager keenConveyanceCooldownManager = new CooldownManager();

    private final Map<UUID, Location> deathLocations = new HashMap<>();
    private final Map<UUID, List<Object>> teleportOptionsMap = new HashMap<>();
    private final Map<UUID, Integer> teleportIndexMap = new HashMap<>();

    public KeenConveyance(JavaPlugin plugin) {
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

    public static void resetAllCooldownsKeenConveyance(Player player) {
        keenConveyanceCooldownManager.resetCooldown(player.getName());
    }

    public static ItemStack createKeenConveyanceItem() {
        ItemStack s9a = new ItemStack(Material.DIAMOND_HORSE_ARMOR);
        ItemMeta s9ameta = s9a.getItemMeta();
        s9ameta.setDisplayName(ChatColor.DARK_RED + "Транспортировка");
        s9ameta.setCustomModelData(10009);
        List<String> lores9a = new ArrayList<>();
        lores9a.add(ChatColor.BLUE + "   При использовании заклинания");
        lores9a.add(ChatColor.BLUE + "   Игрок с подготовкой телепортируется");
        lores9a.add(ChatColor.BLUE + "   К любому игроку или к месту смерти или месту возраждения ");
        lores9a.add(ChatColor.BLUE + "   Телепортацию можно сбить получив достаточно урона ");
        lores9a.add("");
        lores9a.add(ChatColor.AQUA + "   Мана: " + ChatColor.GRAY + manauseg);
        lores9a.add(ChatColor.RED + "   Перезарядка: " + ChatColor.GRAY + keenConveyanceCooldownTime);
        lores9a.add("");
        s9ameta.setLore(lores9a);
        s9a.setItemMeta(s9ameta);
        return s9a;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Integer hasArtifact = artifactMenu.checkArtifactMenu(player, cmduseg);
        if (hasArtifact == null) {
            sendActionBarMessage(player, " ");
            return;
        }

        Location deathLocation = player.getLocation();

        deathLocations.put(player.getUniqueId(), deathLocation);
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
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !keenConveyanceCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    keenConveyanceCooldownManager.setCooldown(player.getName(), keenConveyanceCooldownTime);
                    event.setCancelled(true);
                    handleKeenConveyance(player);
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
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !keenConveyanceCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    keenConveyanceCooldownManager.setCooldown(player.getName(), keenConveyanceCooldownTime);
                    event.setCancelled(true);
                    handleKeenConveyance(player);
                    manaMechanics.addSecondMana(-manauseg);
                }
                else {
                    NoManaPlayer.displayNoManaMessage(player);
                }
            }
        } else if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !keenConveyanceCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    keenConveyanceCooldownManager.setCooldown(player.getName(), keenConveyanceCooldownTime);
                    event.setCancelled(true);
                    handleKeenConveyance(player);
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
        if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !keenConveyanceCooldownManager.hasCooldown(player.getName())) {
            if (Silence.getSilenceState(player)>= 1) {
                event.setCancelled(true);
                NoManaPlayer.displaySilenceMessage(player);
                return;
            }
            if (secondManaUse >= manauseg) {
                keenConveyanceCooldownManager.setCooldown(player.getName(), keenConveyanceCooldownTime);
                event.setCancelled(true);
                handleKeenConveyance(player);
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
                updateTeleportOptions(onlinePlayer);
                UUID playerUUID = onlinePlayer.getUniqueId();
                List<Object> teleportOptions = teleportOptionsMap.get(playerUUID);

                int currentTeleportIndex = teleportIndexMap.getOrDefault(playerUUID, 0);
                Object selectedOption = teleportOptions.get(currentTeleportIndex);
                if (selectedOption instanceof Player) {
                    selectedOption = ((Player) selectedOption).getName();
                }
                if (keenConveyanceCooldownManager.hasCooldown(onlinePlayer.getName())) {
                    int remainingTime = (int) ((keenConveyanceCooldownManager.cooldowns.get(onlinePlayer.getName()) - System.currentTimeMillis()) / 1000);
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + ""  + manauseg + ChatColor.GRAY + " | " + ChatColor.RED + "Транспортировка " + ChatColor.GRAY + "| " + ChatColor.RED + selectedOption + " КД: " + remainingTime + " сек.");
                } else {
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + "" + manauseg + ChatColor.GRAY + " | " + ChatColor.YELLOW + "Транспортировка " + ChatColor.GRAY + "| " + ChatColor.YELLOW + selectedOption);
                }
            } else if (!getspellslots.contains(heldItemSlot)) {
                sendActionBarMessage(onlinePlayer, " ");
            }

            sendActionBarMessage(onlinePlayer, actionBarMessage);
        }
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        Integer hasArtifact = artifactMenu.checkArtifactMenu(player, cmduseg);
        if (hasArtifact == null) {
            sendActionBarMessage(player, " ");
            return;
        }

        int heldItemSlot = player.getInventory().getHeldItemSlot();
        if (heldItemSlot == hasArtifact - 9) {
            if (event.isSneaking()) {
                UUID playerUUID = player.getUniqueId();
                List<Object> teleportOptions = teleportOptionsMap.get(playerUUID);

                int currentTeleportIndex = teleportIndexMap.getOrDefault(playerUUID, 0);
                currentTeleportIndex = (currentTeleportIndex + 1) % teleportOptions.size();
                teleportIndexMap.put(playerUUID, currentTeleportIndex);

                Object selectedOption = teleportOptions.get(currentTeleportIndex);
            }
        }
    }

    private void updateTeleportOptions(Player player) {
        UUID playerUUID = player.getUniqueId();

        List<Object> teleportOptions = teleportOptionsMap.computeIfAbsent(playerUUID, k -> new ArrayList<>());
        teleportOptions.clear();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer == player) {
                continue;
            }
            teleportOptions.add(onlinePlayer);
        }

        teleportOptions.add("Точка возрождения");

        Location deathLocation = deathLocations.get(playerUUID);
        if (deathLocation != null) {
            teleportOptions.add("Последняя точка смерти");
        }
    }

    private void sendActionBarMessage(Player player, String message) {
        player.sendActionBar(message);
    }

    private void spawnParticleAroundArmorStand(Player player, Entity armorStand, double radius, int numParticles, double heightOffset, int numRings) {
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

                Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(0, 153, 153), 1);

                player.getWorld().spawnParticle(Particle.REDSTONE, particleLocation, 1, 0, 0, 0, 0, dustOptions);
            }
        }
    }

    private void handleKeenConveyance(Player player) {
        UUID playerUUID = player.getUniqueId();
        List<Object> teleportOptions = teleportOptionsMap.get(playerUUID);
        final BukkitTask[] task = new BukkitTask[1];
        final BukkitTask[] tasktp = new BukkitTask[1];

        int currentTeleportIndex = teleportIndexMap.getOrDefault(playerUUID, 0);
        Object selectedOption = teleportOptions.get(currentTeleportIndex);

        if (selectedOption instanceof Player) {
            Player targetPlayer = (Player) selectedOption;
            Location targetPlayerLocation = targetPlayer.getLocation();
            Location targetPlayerLocationNick = targetPlayer.getLocation();
            ArmorStand armorStandinv = player.getWorld().spawn(targetPlayerLocationNick.add(0, 5.0,0), ArmorStand.class, entity -> {
                entity.setGravity(false);
                entity.setVisible(false);
                entity.setInvulnerable(true);
                entity.setMarker(true);
                entity.setAI(false);
                entity.setCanPickupItems(false);
                entity.addScoreboardTag("unmovable");
                entity.setCustomName(player.getName());
                entity.setCustomNameVisible(true);
            });
            ArmorStand armorStand = player.getWorld().spawn(targetPlayerLocation.add(0, 3.0,0), ArmorStand.class, entity -> {
                entity.setHelmet(new ItemStack(Material.PLAYER_HEAD)); // Шлем
                entity.setChestplate(createColoredLeatherArmor(Material.LEATHER_CHESTPLATE, Color.fromRGB(173, 216, 230))); // Голубая кожаная кираса
                entity.setLeggings(createColoredLeatherArmor(Material.LEATHER_LEGGINGS, Color.fromRGB(173, 216, 230))); // Голубые кожаные поножи
                entity.setBoots(createColoredLeatherArmor(Material.LEATHER_BOOTS, Color.fromRGB(173, 216, 230))); // Голубые кожаные ботинки
                entity.setGravity(false);
                entity.setVisible(true);
                entity.setInvulnerable(true);
                entity.setMarker(true);
                entity.setAI(false);
                entity.setCanPickupItems(false);
                entity.addScoreboardTag("unmovable");
                entity.setArms(true); // Добавляем руки
                entity.setBasePlate(false);
            });
            task[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                spawnParticleAroundArmorStand(player, armorStand, 1.5, 50, 0.2, 2);
                spawnParticleAroundArmorStand(player, player, 1.5, 50, 0.2, 4);
            }, 0, 5);
            tasktp[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                Location targetPlayerLocation2 = targetPlayer.getLocation();
                Location targetPlayerLocationNick2 = targetPlayer.getLocation();
                armorStandinv.teleport(targetPlayerLocationNick2.add(0, 5.0,0));
                armorStand.teleport(targetPlayerLocation2.add(0,3,0));
            }, 0, 1);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                armorStandinv.remove();
                armorStand.remove();
                task[0].cancel();
                tasktp[0].cancel();
            }, 65);
            TPtinker2(player, targetPlayer);
        } else if (selectedOption.equals("Точка возрождения")) {
            Location bedLocation = player.getBedSpawnLocation();
            if (bedLocation != null) {
                Location bedLocationNick = player.getBedSpawnLocation();
                ArmorStand armorStandinv = player.getWorld().spawn(bedLocationNick.add(0, 2.0,0), ArmorStand.class, entity -> {
                    entity.setGravity(false);
                    entity.setVisible(false);
                    entity.setInvulnerable(true);
                    entity.setMarker(true);
                    entity.setAI(false);
                    entity.setCanPickupItems(false);
                    entity.addScoreboardTag("unmovable");
                    entity.setCustomName(player.getName());
                    entity.setCustomNameVisible(true);
                });
                ArmorStand armorStand = player.getWorld().spawn(bedLocation, ArmorStand.class, entity -> {
                    entity.setHelmet(new ItemStack(Material.PLAYER_HEAD)); // Шлем
                    entity.setChestplate(createColoredLeatherArmor(Material.LEATHER_CHESTPLATE, Color.fromRGB(173, 216, 230))); // Голубая кожаная кираса
                    entity.setLeggings(createColoredLeatherArmor(Material.LEATHER_LEGGINGS, Color.fromRGB(173, 216, 230))); // Голубые кожаные поножи
                    entity.setBoots(createColoredLeatherArmor(Material.LEATHER_BOOTS, Color.fromRGB(173, 216, 230))); // Голубые кожаные ботинки
                    entity.setGravity(false);
                    entity.setVisible(true);
                    entity.setInvulnerable(true);
                    entity.setMarker(true);
                    entity.setAI(false);
                    entity.setCanPickupItems(false);
                    entity.addScoreboardTag("unmovable");
                    entity.setArms(true); // Добавляем руки
                    entity.setBasePlate(false);
                });
                task[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                    spawnParticleAroundArmorStand(player, armorStand, 1.5, 50, 0.2, 2);
                    spawnParticleAroundArmorStand(player, player, 1.5, 50, 0.2, 4);
                }, 0, 5);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    armorStandinv.remove();
                    armorStand.remove();
                    task[0].cancel();
                }, 65);
                TPtinker(player, bedLocation);
            }
        } else if (selectedOption.equals("Последняя точка смерти")) {
            Location deathLocation = deathLocations.get(playerUUID);
            if (deathLocation != null) {
                Location deathLocationNick = deathLocations.get(playerUUID);
                ArmorStand armorStandinv = player.getWorld().spawn(deathLocationNick.add(0, 2.0,0), ArmorStand.class, entity -> {
                    entity.setGravity(false);
                    entity.setVisible(false);
                    entity.setInvulnerable(true);
                    entity.setMarker(true);
                    entity.setAI(false);
                    entity.setCanPickupItems(false);
                    entity.addScoreboardTag("unmovable");
                    entity.setCustomName(player.getName());
                    entity.setCustomNameVisible(true);
                });
                ArmorStand armorStand = player.getWorld().spawn(deathLocation, ArmorStand.class, entity -> {
                    entity.setHelmet(new ItemStack(Material.PLAYER_HEAD)); // Шлем
                    entity.setChestplate(createColoredLeatherArmor(Material.LEATHER_CHESTPLATE, Color.fromRGB(173, 216, 230))); // Голубая кожаная кираса
                    entity.setLeggings(createColoredLeatherArmor(Material.LEATHER_LEGGINGS, Color.fromRGB(173, 216, 230))); // Голубые кожаные поножи
                    entity.setBoots(createColoredLeatherArmor(Material.LEATHER_BOOTS, Color.fromRGB(173, 216, 230))); // Голубые кожаные ботинки
                    entity.setGravity(false);
                    entity.setVisible(true);
                    entity.setInvulnerable(true);
                    entity.setMarker(true);
                    entity.setAI(false);
                    entity.setCanPickupItems(false);
                    entity.addScoreboardTag("unmovable");
                    entity.setArms(true); // Добавляем руки
                    entity.setCustomName(player.getName());
                    entity.setCustomNameVisible(true);
                    entity.setBasePlate(false);
                });
                task[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                    spawnParticleAroundArmorStand(player, armorStand, 1.5, 50, 0.2, 2);
                    spawnParticleAroundArmorStand(player, player, 1.5, 50, 0.2, 4);
                }, 0, 5);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    armorStandinv.remove();
                    armorStand.remove();
                    task[0].cancel();
                }, 65);
                TPtinker(player, deathLocation);
            }
        }
    }

    private ItemStack createColoredLeatherArmor(Material material, Color color) {
        ItemStack item = new ItemStack(material);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        if (meta != null) {
            meta.setColor(color);
            item.setItemMeta(meta);
        }
        return item;
    }

    private void TPtinker(Player player, Location tpLocation) {
        double playerhp = player.getHealth();
        stunTP(player);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            double playerhpnew = player.getHealth();
            double resulthp = playerhp - playerhpnew;
            if (resulthp >= 2) {
                return;
            }
            player.teleport(tpLocation);
        }, 65);
    }

    private void TPtinker2(Player player, Player targetPlayer) {
        double playerhp = player.getHealth();
        stunTP(player);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            double playerhpnew = player.getHealth();
            double resulthp = playerhp - playerhpnew;
            if (resulthp >= 2) {
                return;
            }
            Location tplocate = targetPlayer.getLocation();
            player.teleport(tplocate.add(0,0.4,0));
        }, 65);
    }

    private void stunTP(Entity entity) {
        if (entity instanceof Player) {
            SoundUtil.playSoundForNearbyPlayers(entity.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 0.4f, 1.0f, 10);
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


                    Location newLocation = player.getLocation();
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
            }, 65);
        }
    }
}