package me.p0n41k.artifactworld.power;

import me.p0n41k.artifactworld.Artifactworld;
import me.p0n41k.artifactworld.NoManaPlayer;
import me.p0n41k.artifactworld.artmenu.ArtifactMenu;
import me.p0n41k.artifactworld.power.Mechanics.CooldownManager;
import me.p0n41k.artifactworld.power.Mechanics.Silence;
import me.p0n41k.artifactworld.power.Mechanics.SoundUtil;
import org.bukkit.*;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import me.p0n41k.artifactworld.data.ManaMechanics;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;


public class Deflection implements Listener {
    private final JavaPlugin plugin;
    private final ArtifactMenu artifactMenu;
    private static final int deflectionCooldownTime = 3;
    private static final int manauseg = 45;
    private final int cmduseg = 116;
    private static final CooldownManager deflectionCooldownManager = new CooldownManager();

    public Deflection(JavaPlugin plugin) {
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

    public static void resetAllCooldownsDeflection(Player player) {
        deflectionCooldownManager.resetCooldown(player.getName());
    }

    public static ItemStack createDeflectionItem() {
        ItemStack s14 = new ItemStack(Material.DIAMOND_HORSE_ARMOR);
        ItemMeta s14meta = s14.getItemMeta();
        s14meta.setDisplayName(ChatColor.YELLOW + "Джамбас");
        s14meta.setCustomModelData(116);
        List<String> lores14 = new ArrayList<>();
        lores14.add(ChatColor.BLUE + "   Сильный прием с техники дзюдо.");
        lores14.add(ChatColor.BLUE + "   Перебрасывает существо через себя.");
        lores14.add("");
        lores14.add(ChatColor.AQUA + "   Мана: " + ChatColor.GRAY + manauseg);
        lores14.add(ChatColor.RED + "   Перезарядка: " + ChatColor.GRAY + deflectionCooldownTime);
        lores14.add("");
        s14meta.setLore(lores14);
        s14.setItemMeta(s14meta);
        return s14;
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
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !deflectionCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    double distance = player.getLocation().distance(event.getRightClicked().getLocation());
                    if (distance < 5.5) {
                        deflectionCooldownManager.setCooldown(player.getName(), deflectionCooldownTime);
                        event.setCancelled(true);
                        handleDeflection(player, event.getRightClicked());
                        manaMechanics.addSecondMana(-manauseg);
                    }
                }
                else {
                    NoManaPlayer.displayNoManaMessage(player);
                }
            }
        } else if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !deflectionCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    double distance = player.getLocation().distance(event.getRightClicked().getLocation());
                    if (distance < 5.5) {
                        deflectionCooldownManager.setCooldown(player.getName(), deflectionCooldownTime);
                        event.setCancelled(true);
                        handleDeflection(player, event.getRightClicked());
                        manaMechanics.addSecondMana(-manauseg);
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

        ManaMechanics manaMechanics = new ManaMechanics(player.getUniqueId());
        int secondManaUse = manaMechanics.getSecondMana();
        if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !deflectionCooldownManager.hasCooldown(player.getName())) {
            if (Silence.getSilenceState(player)>= 1) {
                NoManaPlayer.displaySilenceMessage(player);
                return;
            }
            if (secondManaUse >= manauseg) {
                double distance = player.getLocation().distance(event.getEntity().getLocation());
                if (distance < 5.5) {
                    event.setCancelled(true);
                    deflectionCooldownManager.setCooldown(player.getName(), deflectionCooldownTime);
                    handleDeflection(player, event.getEntity());
                    manaMechanics.addSecondMana(-manauseg);
                }
            }
            else {
                event.setCancelled(true);
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
                if (deflectionCooldownManager.hasCooldown(onlinePlayer.getName())) {
                    int remainingTime = (int) ((deflectionCooldownManager.cooldowns.get(onlinePlayer.getName()) - System.currentTimeMillis()) / 1000);
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + ""  + manauseg + ChatColor.GRAY + " | " + ChatColor.RED + "Джамбас" + ChatColor.GRAY + " | КД: " + remainingTime + " сек.");
                } else {
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + "" + manauseg + ChatColor.GRAY + " | " + ChatColor.YELLOW + "Джамбас" + ChatColor.GRAY + " |");
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

    private void handleDeflection(Player player, Entity targetEntity) {
        if (targetEntity != null) {

            Vector direction = player.getLocation().getDirection().setY(0).multiply(-1).normalize();
            Vector upwardForce = new Vector(0, 0.75, 0);
            Vector backwardForce = direction.multiply(1.0);
            Vector flippingForce = upwardForce.add(backwardForce);

            targetEntity.setVelocity(flippingForce);

            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 0.9f, 1.0f, 10);
            player.getWorld().spawnParticle(Particle.CRIT, targetEntity.getLocation(), 25);
        }
    }


}