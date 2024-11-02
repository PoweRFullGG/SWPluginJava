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


public class AbsorptionShield implements Listener {
    private final JavaPlugin plugin;
    private final ArtifactMenu artifactMenu;
    private static final int absorptionShieldCooldownTime = 30;
    private static final int manauseg = 45;
    private final int cmduseg = 112;
    private static final CooldownManager absorptionShieldCooldownManager = new CooldownManager();

    public AbsorptionShield(JavaPlugin plugin) {
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

    public static void resetAllCooldownsAbsorptionShield(Player player) {
        absorptionShieldCooldownManager.resetCooldown(player.getName());
    }

    public static ItemStack createAbsorptionShieldItem() {
        ItemStack s10 = new ItemStack(Material.DIAMOND_HORSE_ARMOR);
        ItemMeta s10meta = s10.getItemMeta();
        s10meta.setDisplayName(ChatColor.YELLOW + "Поглощающий Щит");
        s10meta.setCustomModelData(112);
        List<String> lores10 = new ArrayList<>();
        lores10.add(ChatColor.BLUE + "   Создает световой щит, поглощающий урон.");
        lores10.add("");
        lores10.add(ChatColor.AQUA + "   Мана: " + ChatColor.GRAY + manauseg);
        lores10.add(ChatColor.RED + "   Перезарядка: " + ChatColor.GRAY + absorptionShieldCooldownTime);
        lores10.add("");
        s10meta.setLore(lores10);
        s10.setItemMeta(s10meta);
        return s10;
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
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !absorptionShieldCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    absorptionShieldCooldownManager.setCooldown(player.getName(), absorptionShieldCooldownTime);
                    event.setCancelled(true);
                    handleAbsorptionShield(player);
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
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !absorptionShieldCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    absorptionShieldCooldownManager.setCooldown(player.getName(), absorptionShieldCooldownTime);
                    event.setCancelled(true);
                    handleAbsorptionShield(player);
                    manaMechanics.addSecondMana(-manauseg);
                }
                else {
                    NoManaPlayer.displayNoManaMessage(player);
                }
            }
        } else if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !absorptionShieldCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    absorptionShieldCooldownManager.setCooldown(player.getName(), absorptionShieldCooldownTime);
                    event.setCancelled(true);
                    handleAbsorptionShield(player);
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
        if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !absorptionShieldCooldownManager.hasCooldown(player.getName())) {
            if (Silence.getSilenceState(player)>= 1) {
                event.setCancelled(true);
                NoManaPlayer.displaySilenceMessage(player);
                return;
            }
            if (secondManaUse >= manauseg) {
                absorptionShieldCooldownManager.setCooldown(player.getName(), absorptionShieldCooldownTime);
                event.setCancelled(true);
                handleAbsorptionShield(player);
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
                if (absorptionShieldCooldownManager.hasCooldown(onlinePlayer.getName())) {
                    int remainingTime = (int) ((absorptionShieldCooldownManager.cooldowns.get(onlinePlayer.getName()) - System.currentTimeMillis()) / 1000);
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + ""  + manauseg + ChatColor.GRAY + " | " + ChatColor.RED + "Поглощающий щит" + ChatColor.GRAY + " | КД: " + remainingTime + " сек.");
                } else {
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + "" + manauseg + ChatColor.GRAY + " | " + ChatColor.YELLOW + "Поглощающий щит" + ChatColor.GRAY + " |");
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

    private void handleAbsorptionShield(Player player) {
        final BukkitTask[] task = new BukkitTask[1];
        SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_GLOW_SQUID_SQUIRT, 0.9f, 1.0f, 10);
        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 5 * 20, 4, false, false));
        task[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (player.isDead()) {
                task[0].cancel();
                return;
            }
            for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 6) {
                for (double phi = 0; phi <= Math.PI; phi += Math.PI / 6) {
                    double particleX = 1.3 * Math.sin(phi) * Math.cos(theta);
                    double particleY = 1.3 * Math.cos(phi) + 1;
                    double particleZ = 1.3 * Math.sin(phi) * Math.sin(theta);

                    Vector particleDirection = new Vector(particleX, particleY, particleZ);
                    spawnParticlePlayer(player, particleDirection);
                }
            }
        }, 0, 3);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            task[0].cancel();
        }, 20 * 5);
    }

    private void spawnParticlePlayer(Player player, Vector particleDirection) {
        Location particleLocation = player.getLocation().clone().add(particleDirection);
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(255, 255, 90), 1);
        player.getWorld().spawnParticle(Particle.REDSTONE, particleLocation, 1, 0, 0, 0, 0, dustOptions);
    }

}