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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;


public class EchoSlam implements Listener {
    private final JavaPlugin plugin;
    private final ArtifactMenu artifactMenu;
    private final int echoSlamCooldownTime = 120;
    private final int manauseg = 200;
    private final int cmduseg = 10011;
    private static final CooldownManager echoSlamCooldownManager = new CooldownManager();

    public EchoSlam(JavaPlugin plugin) {
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

    public static void resetAllCooldownsEchoSlam(Player player) {
        echoSlamCooldownManager.resetCooldown(player.getName());
    }

    public static ItemStack createEchoSlamItem() {
        ItemStack s11a = new ItemStack(Material.DIAMOND_HORSE_ARMOR);
        ItemMeta s11ameta = s11a.getItemMeta();
        s11ameta.setDisplayName(ChatColor.DARK_RED + "Эхо-Слэм");
        s11ameta.setCustomModelData(10011);
        List<String> lores11a = new ArrayList<>();
        lores11a.add(ChatColor.BLUE + "   При использовании заклинания игрок");
        lores11a.add(ChatColor.BLUE + "   Сотрясает землю вокруг себя");
        lores11a.add(ChatColor.BLUE + "   Нанося урон и подкидывая всех кто оказался рядом ");
        lores11a.add(ChatColor.BLUE + "   Урон зависит от количества существ рядом ");
        lores11a.add(ChatColor.BLUE + "   При смерти сукщества отлетают в разные стороны ");
        lores11a.add("");
        lores11a.add(ChatColor.AQUA + "   Мана: " + ChatColor.GRAY + "200");
        lores11a.add(ChatColor.RED + "   Перезарядка: " + ChatColor.GRAY + "120");
        lores11a.add("");
        s11ameta.setLore(lores11a);
        s11a.setItemMeta(s11ameta);
        return s11a;
    }

    private boolean isPlayerOnGround(Player player) {
        // Проверяем, находится ли игрок на твердом блоке
        return player.getLocation().add(0,-1,0).getBlock().getType() != Material.AIR;
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
            if (player.getInventory().getHeldItemSlot() == hasArtifact - 9 && !echoSlamCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player) >= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    // Проверка, находится ли игрок на земле
                    if (isPlayerOnGround(player)) {
                        echoSlamCooldownManager.setCooldown(player.getName(), echoSlamCooldownTime);
                        event.setCancelled(true);
                        handleEchoSlam(player);
                        manaMechanics.addSecondMana(-manauseg);
                    }
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
            if (player.getInventory().getHeldItemSlot() == hasArtifact - 9 && !echoSlamCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player) >= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    if (isPlayerOnGround(player)) {
                        echoSlamCooldownManager.setCooldown(player.getName(), echoSlamCooldownTime);
                        event.setCancelled(true);
                        handleEchoSlam(player);
                        manaMechanics.addSecondMana(-manauseg);
                    }
                } else {
                    NoManaPlayer.displayNoManaMessage(player);
                }
            }
        } else if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
            if (player.getInventory().getHeldItemSlot() == hasArtifact - 9 && !echoSlamCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player) >= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    if (isPlayerOnGround(player)) {
                        echoSlamCooldownManager.setCooldown(player.getName(), echoSlamCooldownTime);
                        event.setCancelled(true);
                        handleEchoSlam(player);
                        manaMechanics.addSecondMana(-manauseg);
                    }
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
        if (player.getInventory().getHeldItemSlot() == hasArtifact - 9 && !echoSlamCooldownManager.hasCooldown(player.getName())) {
            if (Silence.getSilenceState(player) >= 1) {
                event.setCancelled(true);
                NoManaPlayer.displaySilenceMessage(player);
                return;
            }
            if (secondManaUse >= manauseg) {
                if (isPlayerOnGround(player)) {
                    echoSlamCooldownManager.setCooldown(player.getName(), echoSlamCooldownTime);
                    event.setCancelled(true);
                    handleEchoSlam(player);
                    manaMechanics.addSecondMana(-manauseg);
                }
            } else {
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
                if (echoSlamCooldownManager.hasCooldown(onlinePlayer.getName())) {
                    int remainingTime = (int) ((echoSlamCooldownManager.cooldowns.get(onlinePlayer.getName()) - System.currentTimeMillis()) / 1000);
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + ""  + manauseg + ChatColor.GRAY + " | " + ChatColor.RED + "Эхо-Слэм" + ChatColor.GRAY + " | КД: " + remainingTime + " сек.");
                } else {
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + "" + manauseg + ChatColor.GRAY + " | " + ChatColor.YELLOW + "Эхо-Слэм" + ChatColor.GRAY + " |");
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

    private void handleEchoSlam(Player player) {
        SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.4f, 1.0f, 20);
        double radius = 7.0;
        double damage = 4.0;


        for (int i = 0; i < 20; i++) {
            double radius2 = 0.5;
            double angle = Math.random() * 2 * Math.PI;

            double x = player.getLocation().getX() + radius2 * Math.cos(angle);
            double y = player.getLocation().getY() + Math.random() * 2; // Высота частиц
            double z = player.getLocation().getZ() + radius2 * Math.sin(angle);

            player.getWorld().spawnParticle(Particle.REDSTONE, new Location(player.getWorld(), x, y, z), 3,
                    new Particle.DustOptions(Color.fromRGB(220, 115, 55), 1));
        }

        List<Entity> nearbyEntities = player.getNearbyEntities(radius, radius, radius);
        long entityCount = nearbyEntities.stream()
                .filter(entity -> entity instanceof LivingEntity)
                .count();

        createShockwaveEffect(player, radius, damage, entityCount);
    }

    private void createShockwaveEffect(Player player, double radius, double damage, long entityCount) {
        Location playerLocation = player.getLocation();
        Set<LivingEntity> damagedEntities = new HashSet<>();

        for (int i = 0; i <= radius; i++) {
            double currentRadius = i;

            new BukkitRunnable() {
                @Override
                public void run() {
                    for (double angle = 0; angle < 360; angle += 10) {
                        double radians = Math.toRadians(angle);
                        double x = playerLocation.getX() + currentRadius * Math.cos(radians);
                        double z = playerLocation.getZ() + currentRadius * Math.sin(radians);

                        Location blockLocation = new Location(player.getWorld(), x, playerLocation.getY(), z);
                        Block block = blockLocation.getWorld().getHighestBlockAt(blockLocation); // Получаем самый высокий твердый блок

                        if (!block.isEmpty() && block.getType().isSolid()) {
                            block.getWorld().spawnParticle(Particle.BLOCK_DUST, block.getLocation().add(0, 1, 0), 10, 0.3, 0.5, 0.3, block.getBlockData());
                            Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(220, 115, 55), 1);

                            player.spawnParticle(Particle.REDSTONE, block.getLocation().add(0, 1, 0), 6, 0.3, 0.3, 0.3, dustOptions);

                            Sound blockSound = getBlockSound(block.getType());


                            block.getWorld().playSound(block.getLocation(), blockSound, 0.2f, 0.8f);

                            List<Entity> nearbyEntities = (List<Entity>) block.getWorld().getNearbyEntities(blockLocation, 1.0, radius, 1.0);

                            for (Entity entity : nearbyEntities) {
                                if (entity instanceof LivingEntity && !entity.equals(player) && !damagedEntities.contains(entity)) {
                                    LivingEntity livingEntity = (LivingEntity) entity;

                                    Location entityLocation = livingEntity.getLocation();
                                    Block blockUnderEntity = entityLocation.getWorld().getHighestBlockAt(entityLocation.subtract(0, 1, 0)); // Блок под существом

                                    if (!blockUnderEntity.isEmpty() && blockUnderEntity.getType() != Material.AIR) {
                                        double damagePerEntity = entityCount * damage;
                                        if (livingEntity.getHealth() - damagePerEntity <= 0) {
                                            Vector knockbackDirection = new Vector(Math.random() - 0.5, 0, Math.random() - 0.5).normalize().multiply(2.0);
                                            livingEntity.setVelocity(livingEntity.getVelocity().add(knockbackDirection));
                                        }
                                        livingEntity.damage(damagePerEntity);
                                        damagedEntities.add(livingEntity);

                                        Vector velocity = new Vector(0, 0.5, 0);
                                        livingEntity.setVelocity(livingEntity.getVelocity().add(velocity));
                                    }
                                }
                            }
                        }
                    }
                }
            }.runTaskLater(plugin, i * 2L);
        }
    }

    private Sound getBlockSound(Material blockType) {
        switch (blockType) {
            case GRASS_BLOCK:
            case DIRT:
            case COARSE_DIRT:
            case PODZOL:
            case ACACIA_LEAVES:
            case SPRUCE_LEAVES:
            case AZALEA_LEAVES:
            case BIRCH_LEAVES:
            case CHERRY_LEAVES:
            case DARK_OAK_LEAVES:
            case FLOWERING_AZALEA_LEAVES:
            case JUNGLE_LEAVES:
            case MANGROVE_LEAVES:
            case OAK_LEAVES:
            case LEGACY_LEAVES:
            case LEGACY_LEAVES_2:
                return Sound.BLOCK_GRASS_BREAK;
            case STONE:
            case COBBLESTONE:
            case ANDESITE:
            case GRANITE:
            case DIORITE:
                return Sound.BLOCK_STONE_BREAK;
            case SAND:
            case SANDSTONE:
            case RED_SAND:
            case SOUL_SAND:
            case SOUL_SOIL:
                return Sound.BLOCK_SAND_BREAK;
            case GRAVEL:
                return Sound.BLOCK_GRAVEL_BREAK;
            case OAK_WOOD:
            case SPRUCE_WOOD:
            case BIRCH_WOOD:
            case JUNGLE_WOOD:
            case ACACIA_WOOD:
            case DARK_OAK_WOOD:
            case OAK_PLANKS:
            case SPRUCE_PLANKS:
            case BIRCH_PLANKS:
            case JUNGLE_PLANKS:
            case ACACIA_PLANKS:
            case DARK_OAK_PLANKS:
                return Sound.BLOCK_WOOD_BREAK;
            case SNOW:
            case SNOW_BLOCK:
                return Sound.BLOCK_SNOW_BREAK;
            case GLASS:
            case GLASS_PANE:
                return Sound.BLOCK_GLASS_BREAK;
            default:
                return Sound.BLOCK_STONE_BREAK;
        }
    }

}