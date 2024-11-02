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


public class AstralStep implements Listener {
    private final JavaPlugin plugin;
    private final ArtifactMenu artifactMenu;
    private final int astralStepCooldownTime = 1;
    private static final int astralStepBigCooldownTime = 25;
    private static final int manauseg = 35;

    private final int cmduseg = 118;
    private static final CooldownManager astralStepCooldownManager = new CooldownManager();

    public AstralStep(JavaPlugin plugin) {
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

    private final Map<String, Integer> leapCounts = new HashMap<>();


    public void resetLeapCount(Player player) {
        leapCounts.put(player.getName(), 2);
    }

    // Метод для увеличения количества рывков у определенного игрока.
    public void incrementLeapCount(Player player, Integer volume) {
        int count = leapCounts.getOrDefault(player.getName(), 2);
        leapCounts.put(player.getName(), count + volume);
    }

    // Метод для получения количества рывков у определенного игрока.
    public int getLeapCount(Player player) {
        return leapCounts.getOrDefault(player.getName(), 2);
    }

    public static void resetAllCooldownsAstralStep(Player player) {
        astralStepCooldownManager.resetCooldown(player.getName());
    }

    public static ItemStack createAstralStepItem() {
        ItemStack s16 = new ItemStack(Material.DIAMOND_HORSE_ARMOR);
        ItemMeta s16meta = s16.getItemMeta();
        s16meta.setDisplayName(ChatColor.YELLOW + "Aстральный Шаг");
        s16meta.setCustomModelData(118);
        List<String> lores16 = new ArrayList<>();
        lores16.add(ChatColor.BLUE + "   Делает мощный теневой рывок,");
        lores16.add(ChatColor.BLUE + "   накладывающий негативные эффекты");
        lores16.add(ChatColor.BLUE + "   на противников.");
        lores16.add("");
        lores16.add(ChatColor.AQUA + "   Мана: " + ChatColor.GRAY + manauseg);
        lores16.add(ChatColor.RED + "   Перезарядка: " + ChatColor.GRAY + astralStepBigCooldownTime);
        lores16.add("");
        s16meta.setLore(lores16);
        s16.setItemMeta(s16meta);
        return s16;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        Integer hasArtifact = artifactMenu.checkArtifactMenu(player, cmduseg);
        if (hasArtifact == null) {
            sendActionBarMessage(player, " ");
            return;
        }

        int getspellgerk = getLeapCount(player);
        ManaMechanics manaMechanics = new ManaMechanics(player.getUniqueId());
        int secondManaUse = manaMechanics.getSecondMana();
        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK) || (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !astralStepCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    if (getspellgerk > 1) {
                        event.setCancelled(true);
                        incrementLeapCount(player, -1);
                        handleAstralStep(player);
                        manaMechanics.addSecondMana(-manauseg);
                        astralStepCooldownManager.setCooldown(player.getName(), astralStepCooldownTime);
                    } else if (getspellgerk == 1) {
                        event.setCancelled(true);
                        handleAstralStep(player);
                        manaMechanics.addSecondMana(-manauseg);
                        astralStepCooldownManager.setCooldown(player.getName(), astralStepBigCooldownTime);
                        resetLeapCount(player);
                    }
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

        int getspellgerk = getLeapCount(player);
        ManaMechanics manaMechanics = new ManaMechanics(player.getUniqueId());
        int secondManaUse = manaMechanics.getSecondMana();
        if (event.getHand().equals(EquipmentSlot.HAND)) {
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !astralStepCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    if (getspellgerk > 1) {
                        event.setCancelled(true);
                        incrementLeapCount(player, -1);
                        handleAstralStep(player);
                        manaMechanics.addSecondMana(-manauseg);
                        astralStepCooldownManager.setCooldown(player.getName(), astralStepCooldownTime);
                    } else if (getspellgerk == 1) {
                        event.setCancelled(true);
                        handleAstralStep(player);
                        manaMechanics.addSecondMana(-manauseg);
                        astralStepCooldownManager.setCooldown(player.getName(), astralStepBigCooldownTime);
                        resetLeapCount(player);
                    }
                }
                else {
                    NoManaPlayer.displayNoManaMessage(player);
                }
            }
        } else if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !astralStepCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    if (getspellgerk > 1) {
                        event.setCancelled(true);
                        incrementLeapCount(player, -1);
                        handleAstralStep(player);
                        manaMechanics.addSecondMana(-manauseg);
                        astralStepCooldownManager.setCooldown(player.getName(), astralStepCooldownTime);
                    } else if (getspellgerk == 1) {
                        event.setCancelled(true);
                        handleAstralStep(player);
                        manaMechanics.addSecondMana(-manauseg);
                        astralStepCooldownManager.setCooldown(player.getName(), astralStepBigCooldownTime);
                        resetLeapCount(player);
                    }
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

        int getspellgerk = getLeapCount(player);
        ManaMechanics manaMechanics = new ManaMechanics(player.getUniqueId());
        int secondManaUse = manaMechanics.getSecondMana();
        int heldItemSlot = player.getInventory().getHeldItemSlot();
        if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !astralStepCooldownManager.hasCooldown(player.getName())) {
            if (Silence.getSilenceState(player)>= 1) {
                event.setCancelled(true);
                NoManaPlayer.displaySilenceMessage(player);
                return;
            }
            if (secondManaUse >= manauseg) {
                if (getspellgerk > 1) {
                    event.setCancelled(true);
                    incrementLeapCount(player, -1);
                    handleAstralStep(player);
                    manaMechanics.addSecondMana(-manauseg);
                    astralStepCooldownManager.setCooldown(player.getName(), astralStepCooldownTime);
                } else if (getspellgerk == 1) {
                    event.setCancelled(true);
                    handleAstralStep(player);
                    manaMechanics.addSecondMana(-manauseg);
                    astralStepCooldownManager.setCooldown(player.getName(), astralStepBigCooldownTime);
                    resetLeapCount(player);
                }
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
                int getspellgerk = getLeapCount(onlinePlayer);
                if (astralStepCooldownManager.hasCooldown(onlinePlayer.getName())) {
                    int remainingTime = (int) ((astralStepCooldownManager.cooldowns.get(onlinePlayer.getName()) - System.currentTimeMillis()) / 1000);
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + ""  + manauseg + ChatColor.GRAY + " | " + ChatColor.RED + "Aстральный шаг " + "[" + getspellgerk + "]" + ChatColor.GRAY + " | КД: " + remainingTime + " сек.");
                } else {
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + "" + manauseg + ChatColor.GRAY + " | " + ChatColor.YELLOW + "Aстральный шаг " + "[" + getspellgerk + "]" + ChatColor.GRAY + " |");
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

    private void spawnSpellJerkParticle(Location location, Player player) {
        double particleSpacing = 0.13;
        double startY = location.getY() + 1.5;
        double endY = location.getY() - 0.9;

        Color[] rainbowColors = {
                Color.fromRGB(153, 0, 153),
                Color.fromRGB(127, 0, 255),
                Color.fromRGB(127, 0, 255),
                Color.fromRGB(127, 0, 255),
                Color.fromRGB(153, 0, 153),
        };

        double colorStep = (startY - endY) / rainbowColors.length;

        // Итерируем по каждому цвету радуги
        for (int i = 0; i < rainbowColors.length; i++) {
            double currentY = startY - i * colorStep;
            Color currentColor = rainbowColors[i];

            Location particleLocation = new Location(location.getWorld(), location.getX(), currentY, location.getZ());
            Particle.DustOptions dustOptions = new Particle.DustOptions(currentColor, 2);
            location.getWorld().spawnParticle(Particle.REDSTONE, particleLocation, 1, particleSpacing, particleSpacing, particleSpacing, 0, dustOptions);
            for (Entity nearbyEntity : particleLocation.getWorld().getNearbyEntities(particleLocation, 1.1, 1.1, 1.1)) {
                if (nearbyEntity instanceof LivingEntity && !nearbyEntity.equals(player)) {
                    LivingEntity livingEntity = (LivingEntity) nearbyEntity;
                    livingEntity.damage(4);
                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*4, 2, false, false));
                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20*2, 69, false, false));
                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 50, 69, false, false));
                }
            }
        }

    }


    private void handleAstralStep(Player player) {
        SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_SHULKER_TELEPORT, 0.8f, 0.8f, 10);

        Location startLocation = player.getLocation();
        Vector direction = player.getLocation().getDirection().normalize();
        Location targetLocation = startLocation.clone().add(direction.multiply(4)); // Целевая точка

        // Переменная для хранения последней допустимой позиции перед препятствием
        Location lastValidLocation = startLocation.clone();

        Vector step = direction.clone().multiply(0.5);
        double distance = startLocation.distance(targetLocation);
        boolean blockedBelow = false; // Флаг для проверки блоков под игроком

        for (double i = 0; i < distance; i += 0.5) {
            Location currentLocation = startLocation.clone().add(step.clone().multiply(i));
            Block block = currentLocation.getBlock();

            if (currentLocation.getY() < startLocation.getY() && isSolidBlock(block)) {
                blockedBelow = true;
            }

            if (!isNonObstacleBlock(block)) {
                break;
            }
            lastValidLocation = currentLocation.clone();
        }

        if (blockedBelow) {
            Vector flatDirection = new Vector(direction.getX(), 0, direction.getZ()).normalize();

            for (double i = 0; i < 4; i += 0.5) {
                Location currentLocation = startLocation.clone().add(flatDirection.clone().multiply(i));
                currentLocation.setY(startLocation.getY());
                Block block = currentLocation.getBlock();

                if (!isNonObstacleBlock(block)) {
                    break;
                }
                lastValidLocation = currentLocation.clone();
            }

            player.teleport(lastValidLocation);
        } else {
            player.teleport(lastValidLocation);
        }

        spawnSpellTrail(startLocation, lastValidLocation, player);
    }


    private boolean isSolidBlock(Block block) {
        Material material = block.getType();

        return !isNonObstacleBlock(block);
    }


    private boolean isNonObstacleBlock(Block block) {
        Material material = block.getType();

        return material == Material.LADDER ||
                material == Material.AIR ||
                material == Material.VINE ||
                material == Material.SHORT_GRASS ||
                material == Material.FERN ||
                material == Material.LARGE_FERN ||
                material == Material.DEAD_BUSH ||
                material == Material.SNOW ||
                material == Material.SUGAR_CANE ||
                material == Material.COBWEB ||
                material == Material.WATER ||
                material == Material.LAVA ||
                material == Material.TWISTING_VINES ||
                material == Material.WEEPING_VINES ||
                material == Material.SEAGRASS ||
                material == Material.TALL_SEAGRASS ||
                material == Material.KELP_PLANT ||
                material == Material.BAMBOO_SAPLING ||
                material == Material.BAMBOO ||
                material == Material.SCAFFOLDING ||
                material == Material.CAVE_VINES ||
                material == Material.CAVE_VINES_PLANT ||
                material == Material.GLOW_LICHEN ||
                material == Material.AZALEA ||
                material == Material.FLOWERING_AZALEA ||
                material == Material.TALL_GRASS ||
                material == Material.DRIPSTONE_BLOCK;
    }

    private void spawnSpellTrail(Location start, Location end, Player player) {
        Vector direction = end.toVector().subtract(start.toVector()).normalize();
        double distance = start.distance(end);

        for (double i = 0; i < distance; i += 0.5) {
            Location particleLocation = start.clone().add(direction.clone().multiply(i));
            spawnSpellJerkParticle(particleLocation, player);
        }
    }



}