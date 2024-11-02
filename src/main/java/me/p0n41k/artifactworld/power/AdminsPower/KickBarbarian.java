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
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


public class KickBarbarian implements Listener {
    private final JavaPlugin plugin;
    private final ArtifactMenu artifactMenu;
    private static final int kickBarbarianCooldownTime = 15;
    private static final int manauseg = 130;
    private final int cmduseg = 10013;
    private static final CooldownManager kickBarbarianCooldownManager = new CooldownManager();

    public KickBarbarian(JavaPlugin plugin) {
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

    public static void resetAllCooldownsKickBarbarian(Player player) {
        kickBarbarianCooldownManager.resetCooldown(player.getName());
    }

    public static ItemStack createKickBarbarianItem() {
        ItemStack s13a = new ItemStack(Material.DIAMOND_HORSE_ARMOR);
        ItemMeta s13ameta = s13a.getItemMeta();
        s13ameta.setDisplayName(ChatColor.DARK_RED + "Мощный удар");
        s13ameta.setCustomModelData(10013);
        List<String> lores13a = new ArrayList<>();
        lores13a.add(ChatColor.BLUE + "   При использовании заклинания игрок");
        lores13a.add(ChatColor.BLUE + "   Наносит мощнейший удар");
        lores13a.add(ChatColor.BLUE + "   Которая откидывает жертву ломая ей ноги");
        lores13a.add(ChatColor.BLUE + "   Полсе чего жертва какое-то время не может прыгать ");
        lores13a.add("");
        lores13a.add(ChatColor.AQUA + "   Мана: " + ChatColor.GRAY + manauseg);
        lores13a.add(ChatColor.RED + "   Перезарядка: " + ChatColor.GRAY + kickBarbarianCooldownTime);
        lores13a.add("");
        s13ameta.setLore(lores13a);
        s13a.setItemMeta(s13ameta);
        return s13a;
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
        if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !kickBarbarianCooldownManager.hasCooldown(player.getName())) {
            if (Silence.getSilenceState(player)>= 1) {
                event.setCancelled(true);
                NoManaPlayer.displaySilenceMessage(player);
                return;
            }
            if (secondManaUse >= manauseg) {
                kickBarbarianCooldownManager.setCooldown(player.getName(), kickBarbarianCooldownTime);
                event.setCancelled(true);
                handleKickBarbarian(player, event.getEntity());
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
                if (kickBarbarianCooldownManager.hasCooldown(onlinePlayer.getName())) {
                    int remainingTime = (int) ((kickBarbarianCooldownManager.cooldowns.get(onlinePlayer.getName()) - System.currentTimeMillis()) / 1000);
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + ""  + manauseg + ChatColor.GRAY + " | " + ChatColor.RED + "Мощный удар" + ChatColor.GRAY + " | КД: " + remainingTime + " сек.");
                } else {
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + "" + manauseg + ChatColor.GRAY + " | " + ChatColor.YELLOW + "Мощный удар" + ChatColor.GRAY + " |");
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

    private void handleKickBarbarian(Player player, Entity targetEntity) {
        SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 0.9f, 1.0f, 10);
        SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 0.9f, 1.0f, 10);
        SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 0.3f, 1.0f, 10);
        if (targetEntity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) targetEntity;
            livingEntity.damage(4);
            PotionEffect noJumpEffect = new PotionEffect(PotionEffectType.JUMP, 20*12, -5, false, false);
            livingEntity.addPotionEffect(noJumpEffect);
            livingEntity.setVelocity(player.getLocation().getDirection().multiply(2));

            livingEntity.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, livingEntity.getLocation().add(0, 1, 0), 30, 0.5, 0.5, 0.5, 0);

            for (int i = 0; i < 5; i++) {
                double offsetX = Math.random() * 2 - 1;
                double offsetY = Math.random() * 2 - 1;
                double offsetZ = Math.random() * 2 - 1;
                livingEntity.getWorld().spawnParticle(Particle.END_ROD, livingEntity.getLocation().add(offsetX, offsetY, offsetZ), 1, 0, 0, 0, 0);
            }
        }
    }

}