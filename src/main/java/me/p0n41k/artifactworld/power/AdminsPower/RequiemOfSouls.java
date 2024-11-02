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


public class RequiemOfSouls implements Listener {
    private final JavaPlugin plugin;
    private final ArtifactMenu artifactMenu;
    private static final int RequiemOfSoulsCooldownTime = 80;
    private static final int manauseg = 250;
    private final int cmduseg = 10002;
    private static final CooldownManager RequiemOfSoulsCooldownManager = new CooldownManager();

    public RequiemOfSouls(JavaPlugin plugin) {
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

    private final Map<String, Integer> soulCounts = new HashMap<>();

    private final Map<ArmorStand, HashSet<UUID>> damagedEntitiesMap = new HashMap<>();


    public void resetSoulCount(Player player) {
        soulCounts.put(player.getName(), 5);
    }

    public void incrementSoulCount(Player player, Integer volume) {
        int count = soulCounts.getOrDefault(player.getName(), 5);
        soulCounts.put(player.getName(), count + volume);
    }

    public int getSoulCount(Player player) {
        return soulCounts.getOrDefault(player.getName(), 5);
    }

    public static void resetAllCooldownsRequiemOfSouls(Player player) {
        RequiemOfSoulsCooldownManager.resetCooldown(player.getName());
    }

    public static ItemStack createRequiemOfSoulsItem() {
        ItemStack s2a = new ItemStack(Material.DIAMOND_HORSE_ARMOR);
        ItemMeta s2ameta = s2a.getItemMeta();
        s2ameta.setDisplayName(ChatColor.DARK_RED + "Requiem Of Souls");
        s2ameta.setCustomModelData(10002);
        List<String> lores2a = new ArrayList<>();
        lores2a.add(ChatColor.BLUE + "   Заклинание даёт возможность игроку");
        lores2a.add(ChatColor.BLUE + "   Накапливать души путём убийства существ");
        lores2a.add(ChatColor.BLUE + "   При использовании с небольшой задержкой ");
        lores2a.add(ChatColor.BLUE + "   Заклинание выпускается все души");
        lores2a.add(ChatColor.BLUE + "   Каждая душа наносит урон и накладывает страх");
        lores2a.add("");
        lores2a.add(ChatColor.AQUA + "   Мана: " + ChatColor.GRAY + manauseg);
        lores2a.add(ChatColor.RED + "   Перезарядка: " + ChatColor.GRAY + RequiemOfSoulsCooldownTime);
        lores2a.add("");
        s2ameta.setLore(lores2a);
        s2a.setItemMeta(s2ameta);
        return s2a;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        resetSoulCount(player);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        Player killer = ((LivingEntity) entity).getKiller();

        if (killer != null) {
            if (getSoulCount(killer) <= 19) {
                incrementSoulCount(killer, 1);
            }
        }
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
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !RequiemOfSoulsCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    RequiemOfSoulsCooldownManager.setCooldown(player.getName(), RequiemOfSoulsCooldownTime);
                    event.setCancelled(true);
                    int souls = getSoulCount(player);
                    handleRequiemOfSouls(player, souls);
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
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !RequiemOfSoulsCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    RequiemOfSoulsCooldownManager.setCooldown(player.getName(), RequiemOfSoulsCooldownTime);
                    event.setCancelled(true);
                    int souls = getSoulCount(player);
                    handleRequiemOfSouls(player, souls);
                    manaMechanics.addSecondMana(-manauseg);
                }
                else {
                    NoManaPlayer.displayNoManaMessage(player);
                }
            }
        } else if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !RequiemOfSoulsCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    RequiemOfSoulsCooldownManager.setCooldown(player.getName(), RequiemOfSoulsCooldownTime);
                    event.setCancelled(true);
                    int souls = getSoulCount(player);
                    handleRequiemOfSouls(player, souls);
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
        if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !RequiemOfSoulsCooldownManager.hasCooldown(player.getName())) {
            if (Silence.getSilenceState(player)>= 1) {
                event.setCancelled(true);
                NoManaPlayer.displaySilenceMessage(player);
                return;
            }
            if (secondManaUse >= manauseg) {
                RequiemOfSoulsCooldownManager.setCooldown(player.getName(), RequiemOfSoulsCooldownTime);
                event.setCancelled(true);
                int souls = getSoulCount(player);
                handleRequiemOfSouls(player, souls);
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
            int souls = getSoulCount(onlinePlayer);
            if (heldItemSlot == hasArtifact-9) {
                if (RequiemOfSoulsCooldownManager.hasCooldown(onlinePlayer.getName())) {
                    int remainingTime = (int) ((RequiemOfSoulsCooldownManager.cooldowns.get(onlinePlayer.getName()) - System.currentTimeMillis()) / 1000);
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + ""  + manauseg + ChatColor.GRAY + " | " + ChatColor.RED + "Реквием " + souls + ChatColor.GRAY + " | КД: " + remainingTime + " сек.");
                } else {
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + "" + manauseg + ChatColor.GRAY + " | " + ChatColor.YELLOW + "Реквием "  + ChatColor.RED + souls + ChatColor.GRAY + " |");
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

    private boolean isBlockInFrontOfEntity(LivingEntity entity, double distance) {
        Location eyeLocation = entity.getEyeLocation();
        Vector direction = eyeLocation.getDirection().normalize();
        Location targetLocation = eyeLocation.clone().add(direction.multiply(distance));

        return targetLocation.getBlock().getType().isSolid();
    }


    private void handleSoul(Player player, double raz, double zar) {
        Location playerLocation = player.getLocation();

        Location armorStandLocation = new Location(playerLocation.getWorld(), playerLocation.getX(), playerLocation.getY() + 1.1, playerLocation.getZ());
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

        damagedEntitiesMap.put(armorStand, new HashSet<>());

        Vector direction = new Vector(raz, 0, zar).normalize().multiply(0.4);

        AtomicReference<BukkitTask> taskReference = new AtomicReference<>();

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            moveArmorStand(armorStand, direction);

            HashSet<UUID> damagedEntities = damagedEntitiesMap.get(armorStand);

            for (Entity nearbyEntity : armorStand.getNearbyEntities(1.0, 1.5, 1.0)) {
                if (nearbyEntity instanceof LivingEntity && !nearbyEntity.equals(player)) {
                    LivingEntity livingEntity = (LivingEntity) nearbyEntity;

                    if (!damagedEntities.contains(livingEntity.getUniqueId())) {
                        damagedEntities.add(livingEntity.getUniqueId());

                        // Логика страха
                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 60, 1, false, false));
                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 60, 0, false, false));

                        if (player != null) {
                            if (livingEntity instanceof ArmorStand) {
                                return;
                            }
                            final BukkitTask[] taskk = new BukkitTask[1];
                            Vector directionToPlayer = livingEntity.getLocation().subtract(player.getLocation()).toVector().normalize();

                            float yaw = (float) Math.toDegrees(Math.atan2(-directionToPlayer.getX(), directionToPlayer.getZ()));
                            float pitch = (float) Math.toDegrees(Math.atan2(directionToPlayer.getY(), Math.sqrt(directionToPlayer.getX() * directionToPlayer.getX() + directionToPlayer.getZ() * directionToPlayer.getZ())));

                            taskk[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                                if (livingEntity.isDead()) {
                                    taskk[0].cancel();
                                }
                                livingEntity.setRotation(yaw, pitch);
                                Location livingEntityLocation = livingEntity.getLocation();
                                Vector moveVector = direction.clone().multiply(0.25); // Скорость перемещения
                                Location newLocation = livingEntityLocation.clone().add(moveVector);

                                if (newLocation.getBlock().getType().isSolid()) {
                                    return;
                                }

                                // Телепортируем сущность в новую локацию
                                livingEntity.teleport(newLocation);
                            }, 0, 1);

                            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                taskk[0].cancel();
                            }, 60);
                        }

                        livingEntity.setNoDamageTicks(0);
                        livingEntity.damage(6);
                        if (nearbyEntity instanceof Player) {
                            Silence.plusSilenceState((Player) nearbyEntity, 1);
                            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                Silence.plusSilenceState((Player) nearbyEntity, -1);
                            }, 60);
                        }
                    }
                }
            }

            for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 3) {
                for (double phi = 0; phi <= Math.PI; phi += Math.PI / 3) {
                    double particleX = 0.4 * Math.sin(phi) * Math.cos(theta);
                    double particleY = 0.4 * Math.cos(phi);
                    double particleZ = 0.4 * Math.sin(phi) * Math.sin(theta);

                    Vector particleDirection = new Vector(particleX, particleY, particleZ);
                    spawnParticleAroundArmorStand(armorStand, particleDirection);
                }
            }
        }, 0, 1);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            armorStand.remove();
            task.cancel();
            damagedEntitiesMap.remove(armorStand);
        }, 30);

        taskReference.set(task);
    }


    private void spawnNetherParticlesTowardsArmorStand(Player player, Particle particleType) {
        Random random = new Random();
        World world = player.getWorld();
        Location armorStandLocation = player.getLocation();

        for (int i = 0; i < 4; i++) {
            double offsetX = (random.nextDouble() * 2 - 1); // Случайное смещение вдоль оси X
            double offsetY = (random.nextDouble() * 2 - 1); // Случайное смещение вдоль оси Y
            double offsetZ = (random.nextDouble() * 2 - 1); // Случайное смещение вдоль оси Z

            Vector particleDirection = new Vector(offsetX, offsetY, offsetZ).normalize();

            Vector armorStandVector = armorStandLocation.toVector();

            Vector directionToArmorStand = armorStandVector.subtract(armorStandVector.clone().add(particleDirection)).normalize();

            directionToArmorStand.multiply(0.4);

            Location particleLocation = armorStandLocation.clone().add(directionToArmorStand);
            world.spawnParticle(particleType, particleLocation, 1);
        }
    }

    private void stunreq(Entity entity) {
        // Оглушаем игрока
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
                            new Particle.DustOptions(Color.fromRGB(153, 0, 0), 1));

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
            }, 35);
        }
    }


    private void handleRequiemOfSouls(Player player, int numberOfStands) {
        final BukkitTask[] taskportal = new BukkitTask[1];
        final BukkitTask[] taskfinal = new BukkitTask[1];
        stunreq(player);
        SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_WARDEN_LISTENING_ANGRY, 1.0f, 1.0f, 10);
        SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1.0f, 1.0f, 10);

        taskportal[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            spawnNetherParticlesTowardsArmorStand(player, Particle.PORTAL);
            if (player.isDead()) {
                taskfinal[0].cancel();
                taskportal[0].cancel();
            }
        }, 0, 4);

        taskfinal[0] = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            taskportal[0].cancel();
            if (player.isDead()) {
                return;
            }
            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_WARDEN_DEATH, 1.0f, 1.2f, 20);
            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_WARDEN_DEATH, 1.0f, 1.2f, 20);
            double angleIncrement = 2 * Math.PI / numberOfStands;
            for (int i = 0; i < numberOfStands; i++) {
                double angle = i * angleIncrement;
                double raz = Math.cos(angle);
                double zar = Math.sin(angle);
                handleSoul(player, raz, zar);
            }
        }, 40);
    }

    private void spawnParticleAroundArmorStand(ArmorStand armorStand, Vector particleDirection) {
        Location particleLocation = armorStand.getLocation().clone().add(particleDirection);
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(153, 0, 0), 1);
        armorStand.getWorld().spawnParticle(Particle.REDSTONE, particleLocation, 1, 0, 0, 0, 0, dustOptions);
    }

    private void moveArmorStand(ArmorStand armorStand, Vector direction) {
        Location location = armorStand.getLocation();
        location.add(direction);
        armorStand.teleport(location);
    }

}