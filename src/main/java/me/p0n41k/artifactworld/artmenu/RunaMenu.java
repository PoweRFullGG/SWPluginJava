package me.p0n41k.artifactworld.artmenu;

import me.p0n41k.artifactworld.Artifactworld;
import me.p0n41k.artifactworld.data.PlayerData;
import me.p0n41k.artifactworld.data.PlayerDataMaxSlot;
import me.p0n41k.artifactworld.data.PlayerDataRuna;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RunaMenu implements Listener, CommandExecutor {
    private final Artifactworld plugin;

    private static Map<UUID, PlayerDataRuna> playerDataRunaMap;

    public RunaMenu(Artifactworld plugin) {
        this.plugin = plugin;
        playerDataRunaMap = new HashMap<>();
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("runamenu")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                openRunaMenu(player);
            } else {
                sender.sendMessage("Команда доступна только игрокам.");
            }
            return true;
        }
        return false;
    }

    public void openRunaMenu(Player player) {
        UUID playerUUID = player.getUniqueId();
        PlayerDataRuna playerDataRuna = playerDataRunaMap.computeIfAbsent(playerUUID, uuid -> new PlayerDataRuna(playerUUID));

        ItemStack[] inventoryContents = playerDataRuna.loadInventory();

        Inventory newMenu = Bukkit.createInventory(null, 45, ChatColor.DARK_PURPLE + "Меню рун");
        newMenu.setContents(inventoryContents);

        for (int i = 0; i < 45; i++) {
            if (i != 22) {
                newMenu.setItem(i, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
            }
        }

        player.openInventory(newMenu);
    }

    public static boolean isItemInSlotRuna(Player player) {
        UUID playerUUID = player.getUniqueId();
        PlayerDataRuna playerDataRuna = playerDataRunaMap.get(playerUUID);
        if (playerDataRuna == null) {
            return false;
        }

        ItemStack[] inventoryContents = playerDataRuna.loadInventory();
        if (inventoryContents == null || inventoryContents.length <= 22) {
            return false;
        }

        ItemStack itemInSlot22 = inventoryContents[22];
        return itemInSlot22 != null && itemInSlot22.getType() != Material.AIR;
    }


    public static Integer checkRunaMenu(Player player, int customModelData) {
        UUID playerUUID = player.getUniqueId();
        PlayerDataRuna playerDataRuna = playerDataRunaMap.get(playerUUID);
        if (playerDataRuna == null) {
            return null;
        }

        ItemStack[] inventoryContents = playerDataRuna.loadInventory();
        for (int i = 0; i < inventoryContents.length; i++) {
            ItemStack item = inventoryContents[i];
            if (item != null && item.getType() != Material.AIR) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null && meta.hasCustomModelData() && meta.getCustomModelData() == customModelData) {
                    if (item.getType() == Material.IRON_HORSE_ARMOR) {
                        return i;
                    }
                }
            }
        }
        return null;
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        Player player = (Player) event.getWhoClicked();

        if (title.equals(ChatColor.DARK_PURPLE + "Меню рун")) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                savePlayerInventoryRuna(player);
            }, 2);
            int[] blockedSlots = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 23, 24, 25, 26, 27, 28, 29, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44};
            int rawSlot = event.getRawSlot();
            for (int blockedSlot : blockedSlots) {
                if (rawSlot == blockedSlot) {
                    event.setCancelled(true);
                    return;
                }
            }

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null && clickedItem.getType() == Material.IRON_HORSE_ARMOR) {
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

    public static void savePlayerInventoryRuna(Player player) {
        UUID playerUUID = player.getUniqueId();

        Inventory menu = player.getOpenInventory().getTopInventory();
        ItemStack[] inventoryContents = menu.getContents();

        PlayerDataRuna playerDataRuna = playerDataRunaMap.computeIfAbsent(playerUUID, PlayerDataRuna::new);
        playerDataRuna.saveInventory(inventoryContents);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        String title = event.getView().getTitle();
        Player player = (Player) event.getPlayer();

        if (title.equals(ChatColor.DARK_PURPLE + "Меню рун")) {
            UUID playerUUID = player.getUniqueId();

            Inventory menu = event.getInventory();
            ItemStack[] inventoryContents = menu.getContents();

            PlayerDataRuna playerDataRuna = playerDataRunaMap.computeIfAbsent(playerUUID, PlayerDataRuna::new);
            playerDataRuna.saveInventory(inventoryContents);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        PlayerDataRuna playerDataRuna = getPlayerDataRunaMap().computeIfAbsent(playerUUID, uuid -> new PlayerDataRuna(playerUUID));
        ItemStack[] inventoryContents = playerDataRuna.loadInventory();
        Inventory newMenu = Bukkit.createInventory(null, 45, ChatColor.DARK_PURPLE + "Меню рун");
        newMenu.setContents(inventoryContents);
    }

    public void saverunamenu(Player player) {
        UUID playerUUID = player.getUniqueId();
        PlayerDataRuna playerDataRuna = getPlayerDataRunaMap().computeIfAbsent(playerUUID, uuid -> new PlayerDataRuna(playerUUID));
        ItemStack[] inventoryContents = playerDataRuna.loadInventory();
        Inventory newMenu = Bukkit.createInventory(null, 45, ChatColor.DARK_PURPLE + "Меню рун");
        newMenu.setContents(inventoryContents);
    }

    public static Map<UUID, PlayerDataRuna> getPlayerDataRunaMap() {
        return playerDataRunaMap;
    }
}
