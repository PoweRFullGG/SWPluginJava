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
import org.bukkit.entity.*;
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
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;


public class Gender implements Listener {
    private final JavaPlugin plugin;
    private final ArtifactMenu artifactMenu;
    private static final int sexCooldownTime = 30;
    private static final int manauseg = 40;
    private final int cmduseg = 10010;
    private static final CooldownManager sexCooldownManager = new CooldownManager();

    public Gender(JavaPlugin plugin) {
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

    public static void resetAllCooldownsSex(Player player) {
        sexCooldownManager.resetCooldown(player.getName());
    }

    public static ItemStack createGenderItem() {
        ItemStack s10a = new ItemStack(Material.DIAMOND_HORSE_ARMOR);
        ItemMeta s10ameta = s10a.getItemMeta();
        s10ameta.setDisplayName(ChatColor.DARK_RED + "Древний ритуал");
        s10ameta.setCustomModelData(10010);
        List<String> lores10a = new ArrayList<>();
        lores10a.add(ChatColor.BLUE + "   При использовании заклинания на существо");
        lores10a.add(ChatColor.BLUE + "   Игрок начинает бедровыми движениями");
        lores10a.add(ChatColor.BLUE + "   Ритуальный танец который позволяет содать новую жизнь на планете ");
        lores10a.add(ChatColor.BLUE + "   Игрок получает временные баффы а жертва дэбаффы ");
        lores10a.add(ChatColor.BLUE + "   Способность может сработать несколько раз за 1 применение ");
        lores10a.add("");
        lores10a.add(ChatColor.AQUA + "   Мана: " + ChatColor.GRAY + manauseg);
        lores10a.add(ChatColor.RED + "   Перезарядка: " + ChatColor.GRAY + sexCooldownTime);
        lores10a.add("");
        s10ameta.setLore(lores10a);
        s10a.setItemMeta(s10ameta);
        return s10a;
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
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !sexCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    double distance = player.getLocation().distance(event.getRightClicked().getLocation());
                    if (distance < 3) {
                        sexCooldownManager.setCooldown(player.getName(), sexCooldownTime);
                        event.setCancelled(true);
                        handleSex(player, event.getRightClicked());
                        manaMechanics.addSecondMana(-manauseg);
                    }
                }
                else {
                    NoManaPlayer.displayNoManaMessage(player);
                }
            }
        } else if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !sexCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    double distance = player.getLocation().distance(event.getRightClicked().getLocation());
                    if (distance < 3) {
                        sexCooldownManager.setCooldown(player.getName(), sexCooldownTime);
                        event.setCancelled(true);
                        handleSex(player, event.getRightClicked());
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
        if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !sexCooldownManager.hasCooldown(player.getName())) {
            if (Silence.getSilenceState(player)>= 1) {
                event.setCancelled(true);
                NoManaPlayer.displaySilenceMessage(player);
                return;
            }
            if (secondManaUse >= manauseg) {
                double distance = player.getLocation().distance(event.getEntity().getLocation());
                if (distance < 3) {
                    sexCooldownManager.setCooldown(player.getName(), sexCooldownTime);
                    event.setCancelled(true);
                    handleSex(player, event.getEntity());
                    manaMechanics.addSecondMana(-manauseg);
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
                if (sexCooldownManager.hasCooldown(onlinePlayer.getName())) {
                    int remainingTime = (int) ((sexCooldownManager.cooldowns.get(onlinePlayer.getName()) - System.currentTimeMillis()) / 1000);
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + ""  + manauseg + ChatColor.GRAY + " | " + ChatColor.RED + "Древний ритуал" + ChatColor.GRAY + " | КД: " + remainingTime + " сек.");
                } else {
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + "" + manauseg + ChatColor.GRAY + " | " + ChatColor.YELLOW + "Древний ритуал" + ChatColor.GRAY + " |");
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

    private void stunSex(Entity entity) {
        // Оглушаем игрока
        if (entity instanceof Player) {
            final BukkitTask[] taskend = new BukkitTask[1];
            final BukkitTask[] taskId = new BukkitTask[1];
            Player player = (Player) entity;
            Location playerLocation = player.getLocation();
            player.setWalkSpeed(0);
            player.setFlySpeed(0);
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 1,false,false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 1, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 1, false, false));

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
                if (player.isDead()){
                    player.setWalkSpeed(0.2f);
                    player.setFlySpeed(0.1f);
                    HandlerList.unregisterAll(moveListener);
                    taskend[0].cancel();
                    taskId[0].cancel();
                } else {
                    double radius = 0.5;
                    double angle = Math.random() * 2 * Math.PI;

                    double x = player.getLocation().getX() + radius * Math.cos(angle);
                    double y = player.getLocation().getY() + Math.random() * 2;
                    double z = player.getLocation().getZ() + radius * Math.sin(angle);

                    player.getWorld().spawnParticle(Particle.HEART, new Location(player.getWorld(), x, y, z), 5);
                }

            }, 0L, 1L);

            taskend[0] = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Location playerlocationnew = player.getLocation();
                player.teleport(playerlocationnew.add(0,0.3,0));
                player.setWalkSpeed(0.2f);
                player.setFlySpeed(0.1f);
                HandlerList.unregisterAll(moveListener);
                taskId[0].cancel();
            }, 80);

        } else if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            // Оглушаем существо
            livingEntity.setAI(false);
            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 1, false, false));
            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 1, false, false));

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                livingEntity.setAI(true);
            }, 80);
        }
    }

    private final Random random = new Random();

    private void handleSex(Player player, Entity targetEntity) {
        int casts = random.nextInt(3) + 1;

        // Первое срабатывание
        handleSexOne(player, targetEntity);

        if (casts > 1) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (player.isDead() || targetEntity.isDead()) {
                    return;
                }
                SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ITEM_TOTEM_USE, 0.7f, 1.3f, 10);
                SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.7f, 1.1f, 10);
                handleSexOne(player, targetEntity);
            }, 85);
        }

        if (casts > 2) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (player.isDead() || targetEntity.isDead()) {
                    return;
                }
                SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ITEM_TOTEM_USE, 0.7f, 1.3f, 10);
                SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.7f, 1.1f, 10);
                handleSexOne(player, targetEntity);
            }, 170);
        }
    }

    private void handleSexOne(Player player, Entity targetEntity) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20*7, 1,false,false));
        if (targetEntity instanceof LivingEntity) {
            ((LivingEntity) targetEntity).addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20*10, 1, false, false));
        }
        targetEntity.teleport(player);
        stunSex(player);
        stunSex(targetEntity);
        Location spawnLocation = targetEntity.getLocation();
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_VILLAGER_TRADE, 0.9f, 1.0f, 10);
            Location playerFeetLocation = player.getLocation().clone().subtract(0, -1, 0);
            player.getWorld().spawnParticle(Particle.SNOWBALL, playerFeetLocation, 100, 0.5, 0, 1.0, 1.0);
            if (targetEntity instanceof Player) {
                Villager villager = (Villager) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.VILLAGER);
                villager.setBaby();
            } else if (targetEntity instanceof Ageable) {
                Ageable spawnedEntity = (Ageable) spawnLocation.getWorld().spawnEntity(spawnLocation, targetEntity.getType());

                if (spawnedEntity.isAdult()) {
                    spawnedEntity.setBaby();
                }
            } else {
                spawnLocation.getWorld().spawnEntity(spawnLocation, targetEntity.getType());
            }
        }, 80);

    }

}