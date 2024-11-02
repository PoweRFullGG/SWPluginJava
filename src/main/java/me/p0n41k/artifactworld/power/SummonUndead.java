package me.p0n41k.artifactworld.power;

import me.p0n41k.artifactworld.Artifactworld;
import me.p0n41k.artifactworld.NoManaPlayer;
import me.p0n41k.artifactworld.artmenu.ArtifactMenu;
import me.p0n41k.artifactworld.power.Mechanics.CooldownManager;
import me.p0n41k.artifactworld.power.Mechanics.Silence;
import me.p0n41k.artifactworld.power.Mechanics.SoundUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import me.p0n41k.artifactworld.data.ManaMechanics;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;


public class SummonUndead implements Listener {
    private final JavaPlugin plugin;
    private final ArtifactMenu artifactMenu;
    private static final int summonUndeadCooldownTime = 30;
    private static final int manauseg = 75;
    private static final int hpuseg = 5;
    private final int cmduseg = 113;
    private static final CooldownManager summonUndeadCooldownManager = new CooldownManager();

    public SummonUndead(JavaPlugin plugin) {
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

    public static void resetAllCooldownsSummonUndead(Player player) {
        summonUndeadCooldownManager.resetCooldown(player.getName());
    }

    public static ItemStack createSummonUndeadItem() {
        ItemStack s11 = new ItemStack(Material.DIAMOND_HORSE_ARMOR);
        ItemMeta s11meta = s11.getItemMeta();
        s11meta.setDisplayName(ChatColor.YELLOW + "Призыв Нежити");
        s11meta.setCustomModelData(113);
        List<String> lores11 = new ArrayList<>();
        lores11.add(ChatColor.BLUE + "   Ценой части собственного здоровья");
        lores11.add(ChatColor.BLUE + "   позволяет призвать подручных зомби,");
        lores11.add(ChatColor.BLUE + "   атакующих ваших противников.");
        lores11.add("");
        lores11.add(ChatColor.AQUA + "   Мана: " + ChatColor.GRAY + manauseg);
        lores11.add(ChatColor.RED + "   Перезарядка: " + ChatColor.GRAY + summonUndeadCooldownTime);
        lores11.add(ChatColor.DARK_RED + "   Кровь: " + ChatColor.GRAY + hpuseg + " HP");
        lores11.add("");
        s11meta.setLore(lores11);
        s11.setItemMeta(s11meta);
        return s11;
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
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !summonUndeadCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    double health = player.getHealth();
                    if (health > hpuseg) {
                        player.damage(hpuseg);
                        summonUndeadCooldownManager.setCooldown(player.getName(), summonUndeadCooldownTime);
                        event.setCancelled(true);
                        handleSummonUndead(player);
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
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !summonUndeadCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    double health = player.getHealth();
                    if (health > hpuseg) {
                        player.damage(hpuseg);
                        summonUndeadCooldownManager.setCooldown(player.getName(), summonUndeadCooldownTime);
                        event.setCancelled(true);
                        handleSummonUndead(player);
                        manaMechanics.addSecondMana(-manauseg);
                    }
                }
                else {
                    NoManaPlayer.displayNoManaMessage(player);
                }
            }
        } else if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !summonUndeadCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    double health = player.getHealth();
                    if (health > hpuseg) {
                        player.damage(hpuseg);
                        summonUndeadCooldownManager.setCooldown(player.getName(), summonUndeadCooldownTime);
                        event.setCancelled(true);
                        handleSummonUndead(player);
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
        if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !summonUndeadCooldownManager.hasCooldown(player.getName())) {
            if (Silence.getSilenceState(player)>= 1) {
                event.setCancelled(true);
                NoManaPlayer.displaySilenceMessage(player);
                return;
            }
            if (secondManaUse >= manauseg) {
                double health = player.getHealth();
                if (health > hpuseg) {
                    player.damage(hpuseg);
                    summonUndeadCooldownManager.setCooldown(player.getName(), summonUndeadCooldownTime);
                    event.setCancelled(true);
                    handleSummonUndead(player);
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
                if (summonUndeadCooldownManager.hasCooldown(onlinePlayer.getName())) {
                    int remainingTime = (int) ((summonUndeadCooldownManager.cooldowns.get(onlinePlayer.getName()) - System.currentTimeMillis()) / 1000);
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + ""  + manauseg + ChatColor.GRAY + " | " + ChatColor.RED + "Призыв нежити" + ChatColor.GRAY + " | КД: " + remainingTime + " сек.");
                } else {
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + "" + manauseg + ChatColor.GRAY + " | " + ChatColor.YELLOW + "Призыв нежити" + ChatColor.GRAY + " |");
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

    private void handleSummonUndead(Player player) {
        Location playerLocation = player.getLocation();
        World world = player.getWorld();
        Location location = player.getLocation().add(0, 1, 0);

        for (int i = 0; i < 30; i++) {
            double offsetX = Math.random() * 0.6 - 0.3;
            double offsetY = Math.random() * 0.6 - 0.3;
            double offsetZ = Math.random() * 0.6 - 0.3;

            world.spawnParticle(Particle.BLOCK_CRACK, location, 1, offsetX, offsetY, offsetZ, 0, Material.REDSTONE_BLOCK.createBlockData());
        }
        SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1.0f, 1.0f, 8);
        SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_EVOKER_CAST_SPELL, 1.0f, 1.0f, 16);

        for (int i = 0; i < 3; i++) {
            double angle = i * (Math.PI * 2 / 3);
            double radius = 2;
            double x = playerLocation.getX() + radius * Math.cos(angle);
            double z = playerLocation.getZ() + radius * Math.sin(angle);
            double y = playerLocation.getY();

            while (player.getWorld().getBlockAt(new Location(player.getWorld(), x, y, z)).getType() == Material.AIR) {
                y--;
            }
            Block block = player.getWorld().getBlockAt(new Location(player.getWorld(), x, y, z));

            BukkitTask taskBlock = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                block.getWorld().spawnParticle(Particle.BLOCK_CRACK, block.getLocation().add(0.5, 0.5, 0.5), 30, 0.5, 0.5, 0.5, block.getBlockData());
                block.getLocation().getWorld().playSound(block.getLocation(), Sound.BLOCK_GRASS_BREAK, 0.5f, 1.0f);
            }, 0, 3);

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                taskBlock.cancel();
            }, 25);

            y--;

            Location spawnLocation = new Location(player.getWorld(), x, y, z);
            spawnLocation.setY(spawnLocation.getY() - 1.9);

            Zombie zombie = player.getWorld().spawn(spawnLocation, Zombie.class);
            ItemStack HelmetZ = new ItemStack(Material.LEATHER_HELMET);
            LeatherArmorMeta meta = (LeatherArmorMeta) HelmetZ.getItemMeta();
            meta.setColor(Color.fromRGB(0, 51, 102));
            HelmetZ.setItemMeta(meta);
            zombie.getEquipment().setHelmet(HelmetZ);
            zombie.setBaby(false);
            zombie.setCanPickupItems(false);
            zombie.setMaxHealth(35);
            zombie.setHealth(zombie.getMaxHealth());
            zombie.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 15 * 20, 1, false, false));
            zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 15 * 20, 4, false, false));
            zombie.setMetadata("Summoner", new org.bukkit.metadata.FixedMetadataValue(plugin, player.getName()));
            final BukkitTask[] taskPart = new BukkitTask[1];
            final BukkitTask[] taskAgr = new BukkitTask[1];

            taskPart[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                double radiusP = 0.5;
                double angleP = Math.random() * 2 * Math.PI;

                double xP = zombie.getLocation().getX() + radiusP * Math.cos(angleP);
                double yP = zombie.getLocation().getY() + Math.random() * 2;
                double zP = zombie.getLocation().getZ() + radiusP * Math.sin(angleP);

                zombie.getWorld().spawnParticle(Particle.REDSTONE, new Location(zombie.getWorld(), xP, yP, zP), 1,
                        new Particle.DustOptions(Color.fromRGB(0,51,102), 1));
                if (!zombie.isValid() || zombie.isDead()) {
                    taskPart[0].cancel();
                }
            }, 0, 3);

            taskAgr[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                List<Entity> nearbyEntities = zombie.getNearbyEntities(24, 24, 24);
                Entity target = null;
                double closestDistance = Double.MAX_VALUE;

                for (Entity entity : nearbyEntities) {
                    if ((entity instanceof LivingEntity) && !(entity == player) && !(entity instanceof Zombie) && !(entity.hasMetadata("Summoner")) && !(entity instanceof ArmorStand)) {
                        if (entity instanceof Player && ((Player) entity).getGameMode() == GameMode.CREATIVE) {
                            continue;
                        }
                        double distance = zombie.getLocation().distanceSquared(entity.getLocation());
                        if (distance < closestDistance) {
                            closestDistance = distance;
                            target = entity;
                        }
                    }
                }
                if (target != null) {
                    zombie.setTarget((LivingEntity) target);
                }
            }, 0, 1);

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (!zombie.isValid() || zombie.isDead()) {
                    return;
                }
                taskPart[0].cancel();
                taskAgr[0].cancel();
                zombie.getEquipment().clear();
                zombie.remove();
                Location zombieLocations = zombie.getLocation();
                zombieLocations.getWorld().spawnParticle(Particle.WHITE_SMOKE, zombieLocations, 50, 0.5, 0.5, 0.5, 0);
                zombieLocations.getWorld().playSound(zombieLocations, Sound.ITEM_ARMOR_EQUIP_CHAIN, 1.0f, 1.0f);
            }, 15 * 20);

            new BukkitRunnable() {
                int teleportTicks = 0;

                @Override
                public void run() {
                    if (teleportTicks >= 20) {
                        this.cancel();
                        return;
                    }

                    spawnLocation.setY(spawnLocation.getY() + 0.2);
                    zombie.teleport(spawnLocation);

                    teleportTicks++;
                }
            }.runTaskTimer(plugin, 0, 1);
        }
    }


    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player && event.getEntityType() == EntityType.ZOMBIE) {
            Player targetPlayer = (Player) event.getTarget();
            Zombie zombie = (Zombie) event.getEntity();
            if (zombie.hasMetadata("Summoner")) {
                String summonerName = zombie.getMetadata("Summoner").get(0).asString();
                if (summonerName.equals(targetPlayer.getName())) {
                    event.setCancelled(true);
                }
            }
        }
    }

}