package me.p0n41k.artifactworld.power;

import me.p0n41k.artifactworld.Artifactworld;
import me.p0n41k.artifactworld.NoManaPlayer;
import me.p0n41k.artifactworld.artmenu.ArtifactMenu;
import me.p0n41k.artifactworld.power.Mechanics.CooldownManager;
import me.p0n41k.artifactworld.power.Mechanics.Silence;
import me.p0n41k.artifactworld.power.Mechanics.SoundUtil;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
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
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class RandomPortal implements Listener {
    private final JavaPlugin plugin;
    private final ArtifactMenu artifactMenu;
    private static final int randomPortalCooldownTime = 60;
    private static final int manauseg = 75;
    private final int cmduseg = 109;
    private static final CooldownManager randomPortalCooldownManager = new CooldownManager();

    public RandomPortal(JavaPlugin plugin) {
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

    public static void resetAllCooldownsRandomPortal(Player player) {
        randomPortalCooldownManager.resetCooldown(player.getName());
    }

    public static ItemStack createRandomPortalItem() {
        ItemStack s7 = new ItemStack(Material.DIAMOND_HORSE_ARMOR);
        ItemMeta s7meta = s7.getItemMeta();
        s7meta.setDisplayName(ChatColor.YELLOW + "Разлом");
        s7meta.setCustomModelData(109);
        List<String> lores7 = new ArrayList<>();
        lores7.add(ChatColor.BLUE + "   Искривляет пространство, создавая разлом.");
        lores7.add(ChatColor.BLUE + "   Перемещает все попавшее внутрь на большую");
        lores7.add(ChatColor.BLUE + "   высоту.");
        lores7.add("");
        lores7.add(ChatColor.AQUA + "   Мана: " + ChatColor.GRAY + manauseg);
        lores7.add(ChatColor.RED + "   Перезарядка: " + ChatColor.GRAY + randomPortalCooldownTime);
        lores7.add("");
        s7meta.setLore(lores7);
        s7.setItemMeta(s7meta);
        return s7;
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
        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK) || (event.getAction() == Action.LEFT_CLICK_BLOCK)) {
            if (player.getInventory().getHeldItemSlot() == hasArtifact - 9 && !randomPortalCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg &&
                        player.getWorld().getEnvironment() != World.Environment.NETHER &&
                        player.getWorld().getEnvironment() != World.Environment.THE_END) {

                    randomPortalCooldownManager.setCooldown(player.getName(), randomPortalCooldownTime);
                    event.setCancelled(true);
                    handleRandomPortal(player, event.getClickedBlock());
                    manaMechanics.addSecondMana(-manauseg);
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
            List<Integer> getspellslots = artifactMenu.getDiamondHorseArmorSlots(onlinePlayer);

            if (hasArtifact == null) {
                continue;
            }

            int heldItemSlot = onlinePlayer.getInventory().getHeldItemSlot();
            if (heldItemSlot == hasArtifact - 9) {
                if (randomPortalCooldownManager.hasCooldown(onlinePlayer.getName())) {
                    int remainingTime = (int) ((randomPortalCooldownManager.cooldowns.get(onlinePlayer.getName()) - System.currentTimeMillis()) / 1000);
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + "" + manauseg + ChatColor.GRAY + " | " + ChatColor.RED + "Разлом" + ChatColor.GRAY + " | КД: " + remainingTime + " сек.");
                } else {
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + "" + manauseg + ChatColor.GRAY + " | " + ChatColor.YELLOW + "Разлом" + ChatColor.GRAY + " |");
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

    private void spawnParticleAroundArmorStand(ArmorStand armorStand, Vector particleDirection) {
        Location particleLocation = armorStand.getLocation().clone().add(particleDirection);

        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(153, 0, 153), 1);

        armorStand.getWorld().spawnParticle(Particle.REDSTONE, particleLocation, 1, particleDirection.getX() / 10, particleDirection.getY() / 10, particleDirection.getZ() / 10, 0, dustOptions);
    }

    private void spawnNetherParticlesTowardsArmorStand(ArmorStand armorStand, Particle particleType) {
        Random random = new Random();
        World world = armorStand.getWorld();
        Location armorStandLocation = armorStand.getLocation();

        for (int i = 0; i < 4; i++) {
            double offsetX = (random.nextDouble() * 2 - 1);
            double offsetY = (random.nextDouble() * 2 - 1);
            double offsetZ = (random.nextDouble() * 2 - 1);

            Vector particleDirection = new Vector(offsetX, offsetY, offsetZ).normalize();

            Vector armorStandVector = armorStandLocation.toVector();

            Vector directionToArmorStand = armorStandVector.subtract(armorStandVector.clone().add(particleDirection)).normalize();

            directionToArmorStand.multiply(0.4);

            Location particleLocation = armorStandLocation.clone().add(directionToArmorStand);

            world.spawnParticle(particleType, particleLocation, 1);
        }
    }

    private final List<Biome> oceanBiomes = Arrays.asList(
            Biome.OCEAN,
            Biome.DEEP_OCEAN,
            Biome.COLD_OCEAN,
            Biome.DEEP_COLD_OCEAN,
            Biome.WARM_OCEAN,
            Biome.LUKEWARM_OCEAN,
            Biome.DEEP_FROZEN_OCEAN,
            Biome.FROZEN_OCEAN,
            Biome.DEEP_LUKEWARM_OCEAN
    );

    private Location findValidLocation(Location playerLocation) {
        World world = playerLocation.getWorld();
        double playerX = playerLocation.getX();
        double playerY = playerLocation.getY() + 80;
        double playerZ = playerLocation.getZ();

        return new Location(world, playerX, playerY, playerZ);
    }



    private void handleRandomPortal(Player player, Block clickedBlock) {
        if (clickedBlock == null) {
            return;
        }
        if (!clickedBlock.getType().equals(Material.AIR)) {
            Location location = clickedBlock.getLocation().add(0.5, 2.0, 0.5); // Перемещаемся в центр блока и немного вверх, чтобы стойка для брони не зарывалась
            Location locationPortalRandom = findValidLocation(player.getLocation());
            if (location == null) {
                return;
            }
            final BukkitTask[] task = new BukkitTask[1];
            final BukkitTask[] task1 = new BukkitTask[1];
            final BukkitTask[] task2 = new BukkitTask[1];
            final BukkitTask[] task3 = new BukkitTask[1];
            final BukkitTask[] task4 = new BukkitTask[1];
            final BukkitTask[] tasksound = new BukkitTask[1];
            final BukkitTask[] taskportal = new BukkitTask[1];

            ArmorStand armorStand = player.getWorld().spawn(location, ArmorStand.class, entity -> {
                entity.setGravity(false);
                entity.setVisible(false);
                entity.setInvulnerable(true);
                entity.setMarker(true); // Делаем стойку невидимой для игроков
                entity.setAI(false); // Отключаем ИИ, чтобы стойка не могла ходить
                entity.setCanPickupItems(false); // Запрещаем стойке поднимать предметы
                entity.addScoreboardTag("unmovable"); // Добавляем тег, чтобы позже отслеживать и блокировать взаимодействие игроков с этой стойкой
            });

            taskportal[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                spawnNetherParticlesTowardsArmorStand(armorStand, Particle.PORTAL);
            }, 0, 4);

            tasksound[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                SoundUtil.playSoundForNearbyPlayers(armorStand.getLocation(), Sound.BLOCK_BEACON_AMBIENT, 0.4f, 1.0f, 16);
            }, 0, 90);

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                task1[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                    for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 2) {
                        for (double phi = 0; phi <= Math.PI; phi += Math.PI / 2) {
                            double particleX = 0.1 * Math.sin(phi) * Math.cos(theta);
                            double particleY = 0.1 * Math.cos(phi);
                            double particleZ = 0.1 * Math.sin(phi) * Math.sin(theta);

                            Vector particleDirection = new Vector(particleX, particleY, particleZ);
                            spawnParticleAroundArmorStand(armorStand, particleDirection);
                        }
                    }
                }, 0, 2);

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            task3[0].cancel();
                            task4[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                                for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 5) {
                                    for (double phi = 0; phi <= Math.PI; phi += Math.PI / 5) {
                                        double particleX = 0.4 * Math.sin(phi) * Math.cos(theta);
                                        double particleY = 0.4 * Math.cos(phi);
                                        double particleZ = 0.4 * Math.sin(phi) * Math.sin(theta);

                                        Vector particleDirection = new Vector(particleX, particleY, particleZ);
                                        spawnParticleAroundArmorStand(armorStand, particleDirection);
                                    }
                                }
                            }, 0, 2);
                            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                task4[0].cancel();
                            }, 20);
                        }, 20);
                        task2[0].cancel();
                        task3[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                            for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 4) {
                                for (double phi = 0; phi <= Math.PI; phi += Math.PI / 4) {
                                    double particleX = 0.3 * Math.sin(phi) * Math.cos(theta);
                                    double particleY = 0.3 * Math.cos(phi);
                                    double particleZ = 0.3 * Math.sin(phi) * Math.sin(theta);

                                    Vector particleDirection = new Vector(particleX, particleY, particleZ);
                                    spawnParticleAroundArmorStand(armorStand, particleDirection);
                                }
                            }
                        }, 0, 2);
                    }, 20);
                    task1[0].cancel();
                    task2[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                        for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 3) {
                            for (double phi = 0; phi <= Math.PI; phi += Math.PI / 3) {
                                double particleX = 0.2 * Math.sin(phi) * Math.cos(theta);
                                double particleY = 0.2 * Math.cos(phi);
                                double particleZ = 0.2 * Math.sin(phi) * Math.sin(theta);

                                Vector particleDirection = new Vector(particleX, particleY, particleZ);
                                spawnParticleAroundArmorStand(armorStand, particleDirection);
                            }
                        }
                    }, 0, 2);
                }, 20);
            }, 2);

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                task[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                    for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 7) {
                        for (double phi = 0; phi <= Math.PI; phi += Math.PI / 7) {
                            double particleX = 0.5 * Math.sin(phi) * Math.cos(theta);
                            double particleY = 0.5 * Math.cos(phi);
                            double particleZ = 0.5 * Math.sin(phi) * Math.sin(theta);

                            Vector particleDirection = new Vector(particleX, particleY, particleZ);
                            spawnParticleAroundArmorStand(armorStand, particleDirection);
                        }
                    }
                    for (Entity entity : armorStand.getNearbyEntities(0.6, 0.7, 0.6)) {
                        if (entity instanceof LivingEntity) {
                            LivingEntity livingEntity = (LivingEntity) entity;
                            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 250, 0,false,false));
                        }
                        SoundUtil.playSoundForNearbyPlayers(entity.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.5f, 1.0f, 8);
                        entity.teleport(locationPortalRandom);
                    }
                }, 0, 2);

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    task[0].cancel();
                    taskportal[0].cancel();
                    tasksound[0].cancel();
                    armorStand.remove();
                }, 20 * 15);
            }, 85);
        }
    }
}