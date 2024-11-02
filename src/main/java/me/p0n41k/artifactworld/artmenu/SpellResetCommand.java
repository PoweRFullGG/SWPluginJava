package me.p0n41k.artifactworld.artmenu;

import me.p0n41k.artifactworld.artmenu.ArtifactMenu;
import me.p0n41k.artifactworld.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class SpellResetCommand implements CommandExecutor {

    private final ArtifactMenu artifactMenu;

    public SpellResetCommand(ArtifactMenu artifactMenu) {
        this.artifactMenu = artifactMenu;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("resetspells")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Команда доступна только игрокам.");
                return true;
            }
            Player player = (Player) sender;
            if (args.length != 1) {
                player.sendMessage(ChatColor.RED + "Использование: /resetspells <игрок>");
                return true;
            }
            String targetPlayerName = args[0];
            Player targetPlayer = Bukkit.getPlayer(targetPlayerName);
            if (targetPlayer == null) {
                player.sendMessage(ChatColor.RED + "Игрок с именем " + targetPlayerName + " не найден.");
                return true;
            }
            resetSpells(targetPlayer);
            player.sendMessage(ChatColor.GREEN + "Все заклинания у игрока " + targetPlayerName + " были обнулены.");
            return true;
        }
        return false;
    }

    private void resetSpells(Player player) {
        UUID playerUUID = player.getUniqueId();
        PlayerData playerData = artifactMenu.getPlayerDataMap().get(playerUUID);
        if (playerData != null) {
            ItemStack[] inventoryContents = playerData.loadInventory();
            for (int i = 9; i <= 17; i++) {
                if (inventoryContents != null && inventoryContents.length > i) {
                    inventoryContents[i] = null;
                }
            }
            if (player.getOpenInventory().getTitle().equals(ChatColor.DARK_PURPLE + "Меню заклинаний")) {
                playerData.saveInventory(inventoryContents);
                Inventory menu = Bukkit.createInventory(null, 45, ChatColor.DARK_PURPLE + "Меню заклинаний");
                menu.setContents(inventoryContents);
                ArtifactMenu.savePlayerInventory(player);
                player.openInventory(menu);
            } else {
                playerData.saveInventory(inventoryContents);
            }
            player.sendActionBar(" ");
        }
    }
}
