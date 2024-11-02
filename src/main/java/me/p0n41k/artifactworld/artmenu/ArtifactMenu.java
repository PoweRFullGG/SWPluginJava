package me.p0n41k.artifactworld.artmenu;

import me.p0n41k.artifactworld.Artifactworld;
import me.p0n41k.artifactworld.data.PlayerData;
import me.p0n41k.artifactworld.data.PlayerDataMaxSlot;
import me.p0n41k.artifactworld.power.Mechanics.Silence;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class ArtifactMenu implements Listener, CommandExecutor {

    private final Artifactworld plugin;

    private Map<UUID, BukkitTask> taskMap = new HashMap<>();

    private static Map<UUID, Inventory> playerMenus;

    private static Map<UUID, PlayerData> playerDataMap;


    public ArtifactMenu(Artifactworld plugin) {
        this.plugin = plugin;
        playerMenus = new HashMap<>();
        playerDataMap = new HashMap<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("spellmenu")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                openMenu(player);
            } else {
                sender.sendMessage("Команда доступна только игрокам.");
            }
            return true;
        }
        return false;
    }

    public void startArtifactMenuTask(Player player) {
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (player.getOpenInventory().getTitle().equals(ChatColor.DARK_PURPLE + "Меню заклинаний")) {
                savePlayerInventory(player);
                List<Integer> emptySlots = getEmptySlots(player);
                int artSlots = countItemsInArtifactSlots(player);
                PlayerDataMaxSlot playerDataa = new PlayerDataMaxSlot(player.getUniqueId());
                int maxSlotPlayer = playerDataa.getMaxSlot();

                if (artSlots >= maxSlotPlayer) {
                    for (Integer emptySlot : emptySlots) {
                        player.getOpenInventory().setItem(emptySlot, createBarrierItem(maxSlotPlayer));
                    }
                } else if (artSlots < maxSlotPlayer) {
                    Inventory inventory = player.getOpenInventory().getTopInventory();
                    for (int i = 0; i < inventory.getSize(); i++) {
                        ItemStack item = inventory.getItem(i);
                        if (item != null && item.getType() == Material.BARRIER) {
                            inventory.setItem(i, null);
                        }
                    }
                }
            } else {
                BukkitTask currentTask = taskMap.get(player.getUniqueId());
                if (currentTask != null) {
                    currentTask.cancel();
                }
            }
        }, 5L, 5L);

        taskMap.put(player.getUniqueId(), task);
    }

    public void stopArtifactMenuTask(Player player) {
        BukkitTask task = taskMap.remove(player.getUniqueId());
        if (task != null) {
            task.cancel();
        }
    }

    public void saveartmenu(Player player) {
        UUID playerUUID = player.getUniqueId();
        PlayerData playerData = getPlayerDataMap().computeIfAbsent(playerUUID, uuid -> new PlayerData(playerUUID));
        ItemStack[] inventoryContents = playerData.loadInventory();
        Inventory newMenu = Bukkit.createInventory(null, 45, ChatColor.DARK_PURPLE + "Меню заклинаний");
        newMenu.setContents(inventoryContents);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        PlayerData playerData = getPlayerDataMap().computeIfAbsent(playerUUID, uuid -> new PlayerData(playerUUID));
        ItemStack[] inventoryContents = playerData.loadInventory();
        Inventory newMenu = Bukkit.createInventory(null, 45, ChatColor.DARK_PURPLE + "Меню заклинаний");
        newMenu.setContents(inventoryContents);
    }

    public void openMenu(Player player) {
        Silence.plusSilenceState(player, 1);
        startArtifactMenuTask(player);
        UUID playerUUID = player.getUniqueId();
        PlayerData playerData = playerDataMap.computeIfAbsent(playerUUID, uuid -> new PlayerData(playerUUID));
        ItemStack[] inventoryContents = playerData.loadInventory();

        Inventory newMenu = Bukkit.createInventory(null, 45, ChatColor.DARK_PURPLE + "Меню заклинаний");
        newMenu.setContents(inventoryContents);

        for (int i = 0; i < 45; i++) {
            if (i != 9 && i != 10 && i != 11 && i != 12 && i != 13 && i != 14 && i != 15 && i != 16 && i != 17) {
                newMenu.setItem(i, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
            }
        }

        player.openInventory(newMenu);
    }

    public List<Integer> getEmptySlots(Player player) {
        if (player == null) {
            return null;
        }
        UUID playerUUID = player.getUniqueId();
        PlayerData playerData = playerDataMap.get(playerUUID);
        if (playerData == null) {
            return null;
        }

        ItemStack[] inventoryContents = playerData.loadInventory();
        List<Integer> emptySlots = new ArrayList<>();
        for (int i = 9; i <= 17 && i < inventoryContents.length; i++) {
            ItemStack item = inventoryContents[i];
            if (item == null || item.getType() == Material.AIR) {
                emptySlots.add(i);
            }
        }
        return emptySlots;
    }

    public Integer checkArtifactMenu(Player player, int customModelData) {
        UUID playerUUID = player.getUniqueId();
        PlayerData playerData = playerDataMap.get(playerUUID);
        if (playerData == null) {
            return null;
        }

        ItemStack[] inventoryContents = playerData.loadInventory();
        for (int i = 0; i < inventoryContents.length; i++) {
            ItemStack item = inventoryContents[i];
            if (item != null && item.getType() != Material.AIR) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null && meta.hasCustomModelData() && meta.getCustomModelData() == customModelData) {
                    if (item.getType() == Material.DIAMOND_HORSE_ARMOR) {
                        return i; // Возвращаем индекс слота, на котором находится предмет
                    }
                }
            }
        }
        return null;
    }


    public Integer countItemsInArtifactSlots(Player player) {
        UUID playerUUID = player.getUniqueId();
        PlayerData playerData = playerDataMap.get(playerUUID);
        if (playerData == null) {
            return null;
        }

        ItemStack[] inventoryContents = playerData.loadInventory();
        int itemCount = 0;
        // Проверяем, что индексы находятся в пределах границ массива
        int endIndex = Math.min(17, inventoryContents.length - 1);
        for (int i = 9; i <= endIndex; i++) {
            ItemStack item = inventoryContents[i];
            if (item != null && item.getType() != Material.AIR && hasCustomModelData(item)) {
                itemCount++;
            }
        }
        return itemCount;
    }

    public List<Integer> getDiamondHorseArmorSlots(Player player) {
        if (player == null) {
            return Collections.emptyList();
        }
        UUID playerUUID = player.getUniqueId();
        PlayerData playerData = playerDataMap.get(playerUUID);
        if (playerData == null) {
            return Collections.emptyList();
        }

        ItemStack[] inventoryContents = playerData.loadInventory();
        List<Integer> diamondHorseArmorSlots = new ArrayList<>();
        for (int i = 0; i < inventoryContents.length; i++) {
            if (i >= 9 && i <= 17) {
                ItemStack item = inventoryContents[i];
                if (item != null && item.getType() == Material.DIAMOND_HORSE_ARMOR) {
                    diamondHorseArmorSlots.add(i - 9);
                }
            }
        }
        return diamondHorseArmorSlots;
    }

    private boolean hasCustomModelData(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.hasCustomModelData();
    }

    public static void savePlayerInventory(Player player) {
        UUID playerUUID = player.getUniqueId();

        Inventory menu = player.getOpenInventory().getTopInventory();
        ItemStack[] inventoryContents = menu.getContents();

        PlayerData playerData = playerDataMap.computeIfAbsent(playerUUID, PlayerData::new);
        playerData.saveInventory(inventoryContents);
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        Player player = (Player) event.getWhoClicked();

        if (title.equals(ChatColor.DARK_PURPLE + "Меню заклинаний")) {
            savePlayerInventory(player);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                savePlayerInventory(player);
                List<Integer> emptySlots = getEmptySlots(player);
                int artSlots = countItemsInArtifactSlots(player);
                PlayerDataMaxSlot playerDataa = new PlayerDataMaxSlot(player.getUniqueId());
                int maxSlotPlayer = playerDataa.getMaxSlot();

                if (artSlots >= maxSlotPlayer) {
                    for (Integer emptySlot : emptySlots) {
                        player.getOpenInventory().setItem(emptySlot, createBarrierItem(maxSlotPlayer));
                    }
                } else if (artSlots < maxSlotPlayer) {
                    Inventory inventory = player.getOpenInventory().getTopInventory();
                    for (int i = 0; i < inventory.getSize(); i++) {
                        ItemStack item = inventory.getItem(i);
                        if (item != null && item.getType() == Material.BARRIER) {
                            inventory.setItem(i, null);
                        }
                    }
                }
            }, 2);

            int[] blockedSlots = {0, 1, 2, 3, 4, 5, 6, 7, 8, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44};
            int rawSlot = event.getRawSlot();
            for (int blockedSlot : blockedSlots) {
                if (rawSlot == blockedSlot) {
                    event.setCancelled(true);
                    return;
                }
            }

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null && clickedItem.getType() == Material.DIAMOND_HORSE_ARMOR) {
                ItemMeta meta = clickedItem.getItemMeta();
                if (meta != null && meta.hasCustomModelData() && meta.getCustomModelData() >= 101) {
                    return;
                }
            }

            if (clickedItem != null) {
                event.setCancelled(true);
            }
        }
    }



    private ItemStack createBarrierItem(int maxSlot) {
        ItemStack barrier = new ItemStack(Material.BARRIER);
        ItemMeta meta = barrier.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.RED + "Максимальное количество заклинаний: " + maxSlot);
            barrier.setItemMeta(meta);
        }
        return barrier;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        String title = event.getView().getTitle();
        Player player = (Player) event.getPlayer();

        stopArtifactMenuTask(player);
        if (title.equals(ChatColor.DARK_PURPLE + "Меню заклинаний")) {
            Silence.plusSilenceState(player, -1);
            UUID playerUUID = player.getUniqueId();

            Inventory menu = event.getInventory();
            ItemStack[] inventoryContents = menu.getContents();

            PlayerData playerData = playerDataMap.computeIfAbsent(playerUUID, PlayerData::new);
            playerData.saveInventory(inventoryContents);
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        if (player.getOpenInventory().getTitle().equals(ChatColor.DARK_PURPLE + "Меню заклинаний")) {
            savePlayerInventory(player);
        }
    }

    public static Map<UUID, PlayerData> getPlayerDataMap() {
        return playerDataMap;
    }
}
