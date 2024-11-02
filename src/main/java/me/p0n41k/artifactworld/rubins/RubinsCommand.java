package me.p0n41k.artifactworld.rubins;

import me.p0n41k.artifactworld.data.ManaMechanics;
import me.p0n41k.artifactworld.data.PlayerDataRubins;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RubinsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Использование: /addrubins или /setrubins <Игрок> <значение>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Игрок не найден.");
            return true;
        }

        PlayerDataRubins playerData = new PlayerDataRubins(target.getUniqueId());
        int currentValue = playerData.getRubins();

        int newValue;
        try {
            newValue = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Второй аргумент должен быть числом.");
            return true;
        }

        if (command.getName().equalsIgnoreCase("setrubins")) {
            playerData.setRubins(newValue);
            sender.sendMessage(ChatColor.GREEN + "Рубины для игрока " + target.getName() + " было установлено на " + newValue);
        } else if (command.getName().equalsIgnoreCase("addrubins")) {
            playerData.setRubins(currentValue + newValue);
            sender.sendMessage(ChatColor.GREEN + "Рубины для игрока " + target.getName() + " было увеличено на " + newValue);
        }

        return true;
    }
}
