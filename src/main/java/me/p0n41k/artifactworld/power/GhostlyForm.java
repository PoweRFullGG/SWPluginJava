package me.p0n41k.artifactworld.power;

import me.p0n41k.artifactworld.Artifactworld;
import me.p0n41k.artifactworld.NoManaPlayer;
import me.p0n41k.artifactworld.artmenu.ArtifactMenu;
import me.p0n41k.artifactworld.power.Mechanics.CooldownManager;
import me.p0n41k.artifactworld.power.Mechanics.Silence;
import me.p0n41k.artifactworld.power.Mechanics.SoundUtil;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GhostlyForm implements Listener {
    private final JavaPlugin plugin;
    private final ArtifactMenu artifactMenu;
    private static final int attractionCooldownTime = 90;
    private static final int manauseg = 70;
    private final int cmduseg = 114;
    private static final CooldownManager ghostlyFormCooldownManager = new CooldownManager();

    private static final Map<Player, Boolean> GhostPlayer = new HashMap<>();

    public static boolean getGhostPlayer(Player player) {
        return GhostPlayer.getOrDefault(player, false);
    }

    public static void setGhostPlayer(Player player, boolean state) {
        GhostPlayer.put(player, state);
    }

    public GhostlyForm(JavaPlugin plugin) {
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

    public static void resetAllCooldownsGhostlyForm(Player player) {
        ghostlyFormCooldownManager.resetCooldown(player.getName());
    }

    public static ItemStack createGhostlyFormItem() {
        ItemStack s12 = new ItemStack(Material.DIAMOND_HORSE_ARMOR);
        ItemMeta s12meta = s12.getItemMeta();
        s12meta.setDisplayName(ChatColor.YELLOW + "Призрачная Форма");
        s12meta.setCustomModelData(114);
        List<String> lores12 = new ArrayList<>();
        lores12.add(ChatColor.BLUE + "   Дарует игроку призрачную форму,");
        lores12.add(ChatColor.BLUE + "   не позволяющую взаимодействовать");
        lores12.add(ChatColor.BLUE + "   с миром на время действия.");
        lores12.add("");
        lores12.add(ChatColor.AQUA + "   Мана: " + ChatColor.GRAY + manauseg);
        lores12.add(ChatColor.RED + "   Перезарядка: " + ChatColor.GRAY + attractionCooldownTime);
        lores12.add("");
        s12meta.setLore(lores12);
        s12.setItemMeta(s12meta);
        return s12;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        // Check if the player is in ghostly form
        if (getGhostPlayer(player)) {
            event.setCancelled(true);
            return;
        }

        Integer hasArtifact = artifactMenu.checkArtifactMenu(player, cmduseg);
        if (hasArtifact == null) {
            sendActionBarMessage(player, " ");
            return;
        }


        ManaMechanics manaMechanics = new ManaMechanics(player.getUniqueId());
        int secondManaUse = manaMechanics.getSecondMana();
        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK) || (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !ghostlyFormCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    ghostlyFormCooldownManager.setCooldown(player.getName(), attractionCooldownTime);
                    event.setCancelled(true);
                    handleGhostlyForm(player);
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
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !ghostlyFormCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    ghostlyFormCooldownManager.setCooldown(player.getName(), attractionCooldownTime);
                    event.setCancelled(true);
                    handleGhostlyForm(player);
                    manaMechanics.addSecondMana(-manauseg);
                }
                else {
                    NoManaPlayer.displayNoManaMessage(player);
                }
            }
        } else if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !ghostlyFormCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    ghostlyFormCooldownManager.setCooldown(player.getName(), attractionCooldownTime);
                    event.setCancelled(true);
                    handleGhostlyForm(player);
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
        if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !ghostlyFormCooldownManager.hasCooldown(player.getName())) {
            if (Silence.getSilenceState(player)>= 1) {
                event.setCancelled(true);
                NoManaPlayer.displaySilenceMessage(player);
                return;
            }
            if (secondManaUse >= manauseg) {
                ghostlyFormCooldownManager.setCooldown(player.getName(), attractionCooldownTime);
                event.setCancelled(true);
                handleGhostlyForm(player);
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
                if (ghostlyFormCooldownManager.hasCooldown(onlinePlayer.getName())) {
                    int remainingTime = (int) ((ghostlyFormCooldownManager.cooldowns.get(onlinePlayer.getName()) - System.currentTimeMillis()) / 1000);
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + ""  + manauseg + ChatColor.GRAY + " | " + ChatColor.RED + "Призрачная форма" + ChatColor.GRAY + " | КД: " + remainingTime + " сек.");
                } else {
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + "" + manauseg + ChatColor.GRAY + " | " + ChatColor.YELLOW + "Призрачная форма" + ChatColor.GRAY + " |");
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

    private void handleGhostlyForm(Player player) {
        final BukkitTask[] taskPart = new BukkitTask[1];
        final BukkitTask[] taskPart2 = new BukkitTask[1];
        GhostPlayer.put(player, true);
        Silence.plusSilenceState(player, 1);
        taskPart[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            double radiusP = 0.5;
            double angleP = Math.random() * 2 * Math.PI;

            double xP = player.getLocation().getX() + radiusP * Math.cos(angleP);
            double yP = player.getLocation().getY() + Math.random() * 2;
            double zP = player.getLocation().getZ() + radiusP * Math.sin(angleP);

            player.getWorld().spawnParticle(Particle.REDSTONE, new Location(player.getWorld(), xP, yP, zP), 1,
                    new Particle.DustOptions(Color.fromRGB(190,190,190), 1));
            if (player.isDead()) {
                taskPart[0].cancel();
                Silence.plusSilenceState(player, -1);
            }
        }, 0, 1);
        taskPart2[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            double radiusP = 0.5;
            double angleP = Math.random() * 2 * Math.PI;

            double xP = player.getLocation().getX() + radiusP * Math.cos(angleP);
            double yP = player.getLocation().getY() + Math.random() * 2;
            double zP = player.getLocation().getZ() + radiusP * Math.sin(angleP);

            player.getWorld().spawnParticle(Particle.REDSTONE, new Location(player.getWorld(), xP, yP, zP), 1,
                    new Particle.DustOptions(Color.fromRGB(122,122,122), 2));
            if (player.isDead()) {
                taskPart2[0].cancel();
                Silence.plusSilenceState(player, -1);
            }
        }, 0, 2);
        SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.AMBIENT_SOUL_SAND_VALLEY_MOOD, 1.0f, 1.0f, 8);
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 10 * 20, 255, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10 * 20, 100,false,false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 10 * 20, 10, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10 * 20, 1, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 10 * 20, 0, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 10 * 20, 9, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 10 * 20, 9, false, false));

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            taskPart[0].cancel();
            taskPart2[0].cancel();
            Silence.plusSilenceState(player, -1);
            GhostPlayer.put(player, false);
        }, 10 * 20);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (getGhostPlayer(player)) {
                event.setCancelled(true);
            }
        }
    }

}