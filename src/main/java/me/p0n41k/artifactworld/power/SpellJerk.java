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
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.p0n41k.artifactworld.data.ManaMechanics;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SpellJerk implements Listener {
    private final JavaPlugin plugin;
    private final ArtifactMenu artifactMenu;
    private final int spellJerkCooldownTime = 1;
    private static final int spellJerkBigCooldownTime = 20;
    private static final int manauseg = 25;

    private final int cmduseg = 102;
    private static final CooldownManager spellJerkCooldownManager = new CooldownManager();

    public SpellJerk(JavaPlugin plugin) {
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

    // Метод для сброса количества рывков у определенного игрока.
    public void resetLeapCount(Player player) {
        leapCounts.put(player.getName(), 3);
    }

    // Метод для увеличения количества рывков у определенного игрока.
    public void incrementLeapCount(Player player, Integer volume) {
        int count = leapCounts.getOrDefault(player.getName(), 3);
        leapCounts.put(player.getName(), count + volume);
    }

    // Метод для получения количества рывков у определенного игрока.
    public int getLeapCount(Player player) {
        return leapCounts.getOrDefault(player.getName(), 3);
    }

    public static void resetAllCooldownsSpellJerk(Player player) {
        spellJerkCooldownManager.resetCooldown(player.getName());
    }

    public static ItemStack createSpellJerkItem() {
        ItemStack s1 = new ItemStack(Material.DIAMOND_HORSE_ARMOR);
        ItemMeta s1meta = s1.getItemMeta();
        s1meta.setDisplayName(ChatColor.YELLOW + "Рывок");
        s1meta.setCustomModelData(102);
        List<String> lores1 = new ArrayList<>();
        lores1.add(ChatColor.BLUE + "   Делает мощный рывок вперед.");
        lores1.add("");
        lores1.add(ChatColor.AQUA + "   Мана: " + ChatColor.GRAY + manauseg);
        lores1.add(ChatColor.RED + "   Перезарядка: " + ChatColor.GRAY + spellJerkBigCooldownTime);
        lores1.add("");
        s1meta.setLore(lores1);
        s1.setItemMeta(s1meta);
        return s1;
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
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !spellJerkCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    if (getspellgerk > 1) {
                        event.setCancelled(true);
                        incrementLeapCount(player, -1);
                        handleSpellJerk(player);
                        manaMechanics.addSecondMana(-manauseg);
                        spellJerkCooldownManager.setCooldown(player.getName(), spellJerkCooldownTime);
                    } else if (getspellgerk == 1) {
                        event.setCancelled(true);
                        handleSpellJerk(player);
                        manaMechanics.addSecondMana(-manauseg);
                        spellJerkCooldownManager.setCooldown(player.getName(), spellJerkBigCooldownTime);
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
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !spellJerkCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    if (getspellgerk > 1) {
                        event.setCancelled(true);
                        incrementLeapCount(player, -1);
                        handleSpellJerk(player);
                        manaMechanics.addSecondMana(-manauseg);
                        spellJerkCooldownManager.setCooldown(player.getName(), spellJerkCooldownTime);
                    } else if (getspellgerk == 1) {
                        event.setCancelled(true);
                        handleSpellJerk(player);
                        manaMechanics.addSecondMana(-manauseg);
                        spellJerkCooldownManager.setCooldown(player.getName(), spellJerkBigCooldownTime);
                        resetLeapCount(player);
                    }
                }
                else {
                    NoManaPlayer.displayNoManaMessage(player);
                }
            }
        } else if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !spellJerkCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    if (getspellgerk > 1) {
                        event.setCancelled(true);
                        incrementLeapCount(player, -1);
                        handleSpellJerk(player);
                        manaMechanics.addSecondMana(-manauseg);
                        spellJerkCooldownManager.setCooldown(player.getName(), spellJerkCooldownTime);
                    } else if (getspellgerk == 1) {
                        event.setCancelled(true);
                        handleSpellJerk(player);
                        manaMechanics.addSecondMana(-manauseg);
                        spellJerkCooldownManager.setCooldown(player.getName(), spellJerkBigCooldownTime);
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
        if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !spellJerkCooldownManager.hasCooldown(player.getName())) {
            if (Silence.getSilenceState(player)>= 1) {
                event.setCancelled(true);
                NoManaPlayer.displaySilenceMessage(player);
                return;
            }
            if (secondManaUse >= manauseg) {
                if (getspellgerk > 1) {
                    event.setCancelled(true);
                    incrementLeapCount(player, -1);
                    handleSpellJerk(player);
                    manaMechanics.addSecondMana(-manauseg);
                    spellJerkCooldownManager.setCooldown(player.getName(), spellJerkCooldownTime);
                } else if (getspellgerk == 1) {
                    event.setCancelled(true);
                    handleSpellJerk(player);
                    manaMechanics.addSecondMana(-manauseg);
                    spellJerkCooldownManager.setCooldown(player.getName(), spellJerkBigCooldownTime);
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
                if (spellJerkCooldownManager.hasCooldown(onlinePlayer.getName())) {
                    int remainingTime = (int) ((spellJerkCooldownManager.cooldowns.get(onlinePlayer.getName()) - System.currentTimeMillis()) / 1000);
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + ""  + manauseg + ChatColor.GRAY + " | " + ChatColor.RED + "Рывок " + "[" + getspellgerk + "]" + ChatColor.GRAY + " | КД: " + remainingTime + " сек.");
                } else {
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + "" + manauseg + ChatColor.GRAY + " | " + ChatColor.YELLOW + "Рывок " + "[" + getspellgerk + "]" + ChatColor.GRAY + " |");
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

    private void spawnSpellJerkParticle(Player player, Vector particleDirection) {
        Location playerLocation = player.getLocation();

        double particleSpacing = 0.13;

        double startY = playerLocation.getY() + 1.5;

        double endY = playerLocation.getY() - 0.9;

        Color[] rainbowColors = {
                Color.fromRGB(160,160,160),
                Color.fromRGB(192,192,192),
                Color.fromRGB(192,192,192),
                Color.fromRGB(192,192,192),
                Color.fromRGB(160,160,160)
        };

        double colorStep = (startY - endY) / rainbowColors.length;

        for (int i = 0; i < rainbowColors.length; i++) {
            double currentY = startY - i * colorStep;
            Color currentColor = rainbowColors[i];

            Location particleLocation = new Location(playerLocation.getWorld(), playerLocation.getX(), currentY, playerLocation.getZ());
            Particle.DustOptions dustOptions = new Particle.DustOptions(currentColor, 1);
            playerLocation.getWorld().spawnParticle(Particle.REDSTONE, particleLocation, 1, particleSpacing, particleSpacing, particleSpacing, 0, dustOptions);
        }
    }

    private void handleSpellJerk(Player player) {
        SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_PHANTOM_FLAP, 0.9f, 1.0f, 10);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_PHANTOM_FLAP, 0.9f, 1.0f, 10);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_PHANTOM_FLAP, 0.9f, 1.0f, 10);
            }, 3);
        }, 3);
        Location playerLocation = player.getLocation();
        Vector playerDirection = playerLocation.getDirection();

        playerDirection.normalize();

        Vector leapDirection = playerDirection.multiply(1);

        Vector currentVelocity = player.getVelocity();

        Vector newVelocity = currentVelocity.add(leapDirection);
        player.setVelocity(newVelocity);
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 1) {
                for (double phi = 0; phi <= Math.PI; phi += Math.PI / 1) {
                    double particleX = 0.4 * Math.sin(phi) * Math.cos(theta);
                    double particleY = 0.4 * Math.cos(phi);
                    double particleZ = 0.4 * Math.sin(phi) * Math.sin(theta);

                    Vector particleDirection = new Vector(particleX, particleY, particleZ);
                    spawnSpellJerkParticle(player, particleDirection);
                }
            }
        }, 0, 1);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            task.cancel();
        }, 10);
    }
}