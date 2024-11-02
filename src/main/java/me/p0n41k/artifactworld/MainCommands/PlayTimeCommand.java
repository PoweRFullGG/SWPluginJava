package me.p0n41k.artifactworld.MainCommands;

import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayTimeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        long playTimeTicks = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
        long playTimeSeconds = playTimeTicks / 20;
        long playTimeMinutes = playTimeSeconds / 60;
        long playTimeHours = playTimeMinutes / 60;

        playTimeMinutes %= 60;

        sender.sendMessage(ChatColor.GREEN + "Вы наиграли у нас " + playTimeHours + " часов и " + playTimeMinutes + " минут.");
        return true;
    }
}
