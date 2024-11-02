package me.p0n41k.artifactworld.RepCommands;

import me.p0n41k.artifactworld.data.PlayerDataRep;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RepCommands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Использование: /addrep или /setrep <Игрок> <значение>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Игрок не найден.");
            return true;
        }

        PlayerDataRep playerData = new PlayerDataRep(target.getUniqueId());
        int currentValue = playerData.getRep();

        int newValue;
        try {
            newValue = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Второй аргумент должен быть числом.");
            return true;
        }

        if (command.getName().equalsIgnoreCase("setrep")) {
            playerData.setRep(newValue);
            sender.sendMessage(ChatColor.GREEN + "Репутация для игрока " + target.getName() + " была установлена на " + newValue);
        } else if (command.getName().equalsIgnoreCase("addrep")) {
            playerData.setRep(currentValue + newValue);
            sender.sendMessage(ChatColor.GREEN + "Репутация для игрока " + target.getName() + " была увеличена на " + newValue);
        }

        return true;
    }
}
