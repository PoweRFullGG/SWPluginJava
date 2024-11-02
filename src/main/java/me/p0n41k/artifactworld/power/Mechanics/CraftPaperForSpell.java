package me.p0n41k.artifactworld.power.Mechanics;

import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.bukkit.entity.Entity;

import java.util.*;

public class CraftPaperForSpell implements Listener {

    private JavaPlugin plugin;

    public CraftPaperForSpell(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (itemInHand != null && itemInHand.getType() == Material.DIAMOND_HORSE_ARMOR) {
            ItemMeta meta = itemInHand.getItemMeta();
            if (meta != null && meta.hasCustomModelData() && meta.getCustomModelData() == 100) {
                if (event.getAction().toString().contains("RIGHT_CLICK")) {
                    player.getWorld().strikeLightning(player.getLocation());

                    SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_PAINTING_BREAK, 1.0f, 1.0f, 3);
                    SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f, 16);
                    player.getLocation().add(0,1.3,0).getWorld().spawnParticle(Particle.REDSTONE, player.getLocation().add(0,1.3,0), 30, 0.4, 0.4, 0.4, 0.7, new Particle.DustOptions(Color.AQUA, 1));
                    player.getLocation().add(0,1.3,0).getWorld().spawnParticle(Particle.REDSTONE, player.getLocation().add(0,1.3,0), 30, 0.4, 0.4, 0.4, 0.7, new Particle.DustOptions(org.bukkit.Color.WHITE, 1));

                    if (itemInHand.getAmount() > 1) {
                        itemInHand.setAmount(itemInHand.getAmount() - 1);
                    } else {
                        player.getInventory().setItemInMainHand(null);
                    }
                }
            } else if (meta != null && meta.hasCustomModelData() && meta.getCustomModelData() == 99) {
                if (event.getAction().toString().contains("RIGHT_CLICK")) {
                    player.setVelocity(new Vector(0, 1.4, 0));
                    SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_PAINTING_BREAK, 1.0f, 1.0f, 3);
                    player.getLocation().add(0, 1.3, 0).getWorld().spawnParticle(Particle.REDSTONE, player.getLocation().add(0, 1.3, 0), 30, 0.4, 0.4, 0.4, 0.7, new Particle.DustOptions(Color.AQUA, 1));
                    player.getLocation().add(0, 1.3, 0).getWorld().spawnParticle(Particle.REDSTONE, player.getLocation().add(0, 1.3, 0), 30, 0.4, 0.4, 0.4, 0.7, new Particle.DustOptions(org.bukkit.Color.WHITE, 1));

                    if (itemInHand.getAmount() > 1) {
                        itemInHand.setAmount(itemInHand.getAmount() - 1);
                    } else {
                        player.getInventory().setItemInMainHand(null);
                    }
                }
            } else if (meta != null && meta.hasCustomModelData() && meta.getCustomModelData() == 98) {
                if (event.getAction().toString().contains("RIGHT_CLICK")) {
                    SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_PAINTING_BREAK, 1.0f, 1.0f, 3);
                    player.getLocation().add(0, 1.3, 0).getWorld().spawnParticle(Particle.REDSTONE, player.getLocation().add(0, 1.3, 0), 30, 0.4, 0.4, 0.4, 0.7, new Particle.DustOptions(Color.AQUA, 1));
                    player.getLocation().add(0, 1.3, 0).getWorld().spawnParticle(Particle.REDSTONE, player.getLocation().add(0, 1.3, 0), 30, 0.4, 0.4, 0.4, 0.7, new Particle.DustOptions(org.bukkit.Color.WHITE, 1));

                    if (itemInHand.getAmount() > 1) {
                        itemInHand.setAmount(itemInHand.getAmount() - 1);
                    } else {
                        player.getInventory().setItemInMainHand(null);
                    }

                    SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 1.0f, 20);
                    Collection<Entity> nearbyEntities = player.getLocation().getWorld().getNearbyEntities(player.getLocation(), 16, 16, 16);
                    Random random = new Random();
                    int duration = 20 + random.nextInt(81);
                    for (Entity entity : nearbyEntities) {
                        if (entity instanceof LivingEntity) {
                            ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, duration, 2, false, false));
                        }
                    }
                }
            } else if (meta != null && meta.hasCustomModelData() && meta.getCustomModelData() == 97) {
                if (event.getAction().toString().contains("RIGHT_CLICK")) {
                    SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_PAINTING_BREAK, 1.0f, 1.0f, 3);
                    player.getLocation().add(0, 1.3, 0).getWorld().spawnParticle(Particle.REDSTONE, player.getLocation().add(0, 1.3, 0), 30, 0.4, 0.4, 0.4, 0.7, new Particle.DustOptions(Color.AQUA, 1));
                    player.getLocation().add(0, 1.3, 0).getWorld().spawnParticle(Particle.REDSTONE, player.getLocation().add(0, 1.3, 0), 30, 0.4, 0.4, 0.4, 0.7, new Particle.DustOptions(org.bukkit.Color.WHITE, 1));

                    if (itemInHand.getAmount() > 1) {
                        itemInHand.setAmount(itemInHand.getAmount() - 1);
                    } else {
                        player.getInventory().setItemInMainHand(null);
                    }
                    createExplosionParticles(player);
                    player.getWorld().createExplosion(player.getLocation(), 2.0f, false, false, player);
                    if (player.getHealth() > 6) {
                        player.damage(6);
                    } else {
                        player.setHealth(0);
                    }
                }
            } else if (meta != null && meta.hasCustomModelData() && meta.getCustomModelData() == 96) {
                if (event.getAction().toString().contains("RIGHT_CLICK")) {
                    SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_PAINTING_BREAK, 1.0f, 1.0f, 3);
                    player.getLocation().add(0, 1.3, 0).getWorld().spawnParticle(Particle.REDSTONE, player.getLocation().add(0, 1.3, 0), 30, 0.4, 0.4, 0.4, 0.7, new Particle.DustOptions(Color.AQUA, 1));
                    player.getLocation().add(0, 1.3, 0).getWorld().spawnParticle(Particle.REDSTONE, player.getLocation().add(0, 1.3, 0), 30, 0.4, 0.4, 0.4, 0.7, new Particle.DustOptions(org.bukkit.Color.WHITE, 1));

                    if (itemInHand.getAmount() > 1) {
                        itemInHand.setAmount(itemInHand.getAmount() - 1);
                    } else {
                        player.getInventory().setItemInMainHand(null);
                    }
                    player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 15*20, 0, false, false));
                }
            }
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        ItemStack droppedItem = event.getItemDrop().getItemStack();

        if (isSpellIngredient(droppedItem.getType())) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Location droppedItemLocation = event.getItemDrop().getLocation();
                    if (checkIngredientsAround(droppedItemLocation)) {
                        droppedItemLocation.getWorld().dropItemNaturally(droppedItemLocation, createArmorHorse());
                        playParticleEffect(droppedItemLocation);
                    }
                }
            }.runTaskLater(plugin, 10L);
        }
    }

    private ItemStack createArmorHorse() {
        ItemStack pigstepDisc = new ItemStack(Material.DIAMOND_HORSE_ARMOR);
        ItemMeta meta = pigstepDisc.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + "" + ChatColor.GOLD + "Испорченный свиток");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_PURPLE + "   §kаоышщуаорщушщыоаыущшаоышуы");
        lore.add("");
        lore.add(ChatColor.DARK_PURPLE + "   §kаоышщуор wdaщушщыоаы");
        lore.add("");
        lore.add(ChatColor.DARK_PURPLE + "   §kаоышщуорщушщыоаы");
        lore.add("");
        lore.add(ChatColor.AQUA + "   Использование маны: " + ChatColor.GRAY + "§k231");
        lore.add(ChatColor.RED + "   Кулдаун: " + ChatColor.GRAY + "§k236");
        meta.setLore(lore);

        Random random = new Random();
        int customModelData = 96 + random.nextInt(5);
        meta.setCustomModelData(customModelData);

        pigstepDisc.setItemMeta(meta);
        return pigstepDisc;
    }



    private boolean isSpellIngredient(Material material) {
        return material == Material.FEATHER ||
                material == Material.PAPER ||
                material == Material.GLOW_INK_SAC;
    }

    private boolean checkIngredientsAround(Location location) {
        // Проверка наличия стола зачарования в радиусе 0.2 блока
        boolean enchantingTableNearby = false;
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    Location nearbyLocation = location.clone().add(x, y, z);
                    if (nearbyLocation.getBlock().getType() == Material.ENCHANTING_TABLE && location.distance(nearbyLocation) <= 0.2) {
                        enchantingTableNearby = true;
                        break;
                    }
                }
            }
        }

        if (!enchantingTableNearby) {
            return false;
        }

        HashMap<Material, Integer> ingredientCounts = new HashMap<>();

        for (Item nearbyItem : location.getWorld().getEntitiesByClass(Item.class)) {
            Location itemLocation = nearbyItem.getLocation();
            if (location.distance(itemLocation) <= 1) {
                ItemStack itemStack = nearbyItem.getItemStack();
                Material itemType = itemStack.getType();
                ingredientCounts.put(itemType, ingredientCounts.getOrDefault(itemType, 0) + itemStack.getAmount());
            }
        }

        if (ingredientCounts.containsKey(Material.PAPER) && ingredientCounts.get(Material.PAPER) == 1 &&
                ingredientCounts.containsKey(Material.FEATHER) && ingredientCounts.get(Material.FEATHER) == 1 &&
                ingredientCounts.containsKey(Material.GLOW_INK_SAC) && ingredientCounts.get(Material.GLOW_INK_SAC) == 1) {

            for (Item nearbyItem : location.getWorld().getEntitiesByClass(Item.class)) {
                Location itemLocation = nearbyItem.getLocation();
                if (location.distance(itemLocation) <= 1) {
                    ItemStack itemStack = nearbyItem.getItemStack();
                    Material itemType = itemStack.getType();
                    if (itemType == Material.PAPER || itemType == Material.FEATHER || itemType == Material.GLOW_INK_SAC) {
                        nearbyItem.remove();
                    }
                }
            }


            return true;
        }

        return false;
    }

    private void playParticleEffect(Location location) {
        SoundUtil.playSoundForNearbyPlayers(location, Sound.ENTITY_VILLAGER_WORK_CARTOGRAPHER, 0.9f, 1.0f, 10);
        location.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, location, 10, 0.4, 0.4, 0.4, 0.7);
        location.getWorld().spawnParticle(Particle.REDSTONE, location, 30, 0.4, 0.4, 0.4, 0.7, new Particle.DustOptions(Color.AQUA, 1));
        location.getWorld().spawnParticle(Particle.REDSTONE, location, 30, 0.4, 0.4, 0.4, 0.7, new Particle.DustOptions(org.bukkit.Color.WHITE, 1));
    }

    public void createExplosionParticles(Player player) {
        Location location = player.getLocation();
        World world = location.getWorld();

        if (world != null) {
            double radius = 2.0;
            int count = 60;

            world.spawnParticle(Particle.EXPLOSION_LARGE, location, count, radius, radius, radius, 0.1);
        }
    }
}
