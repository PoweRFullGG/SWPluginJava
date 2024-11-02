package me.p0n41k.artifactworld;

import me.p0n41k.artifactworld.artmenu.RunaMenu;
import me.p0n41k.artifactworld.data.PlayerDataRep;
import me.p0n41k.artifactworld.data.PlayerDataRubins;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import me.p0n41k.artifactworld.data.ManaMechanics;

public class ScoreBoardPlayer implements Listener {
    private JavaPlugin plugin;

    public ScoreBoardPlayer(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        new BukkitRunnable() {
            @Override
            public void run() {
                updateScoreboard();
            }
        }.runTaskTimer(plugin, 0L, 5L);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ManaMechanics manaMechanics = new ManaMechanics(player.getUniqueId());
        manaMechanics.setSecondMana(0);
        updateScoreboard();
    }

    public void updateScoreboard() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            Objective objective = scoreboard.registerNewObjective("stats", "dummy", "§x§F§F§B§0§0§0[ §x§F§5§B§B§1§4- §x§E§C§C§7§2§9S§x§E§2§D§2§3§Dp§x§E§7§D§A§4§De§x§F§0§E§0§5§Bl§x§F§9§E§6§6§Al §x§F§9§E§6§6§AW§x§F§0§E§0§5§Bo§x§E§7§D§A§4§Dr§x§E§2§D§2§3§Dl§x§E§C§C§7§2§9d §x§F§5§B§B§1§4- §x§F§F§B§0§0§0]");

            objective.setDisplaySlot(DisplaySlot.SIDEBAR);

            ManaMechanics manaMechanics = new ManaMechanics(player.getUniqueId());
            int mana = manaMechanics.getMaxMana();
            int secondMana = manaMechanics.getSecondMana();
            int rubins = getRubins(player);
            int rep = getRep(player);
            String runaActive = getRuna(player);

            Score air1 = objective.getScore(" ");
            Score manaline = objective.getScore( ChatColor.AQUA + "♥ " + ChatColor.WHITE + "Мана: "  + ChatColor.AQUA + secondMana + ChatColor.WHITE + "/" + ChatColor.AQUA + mana);
            Score air2 = objective.getScore("  ");
            Score runaline1 = objective.getScore(ChatColor.GOLD + "☯ " + ChatColor.WHITE + "Руна:");
            Score runaline2 = objective.getScore(ChatColor.GOLD + " " + runaActive);
            Score air3 = objective.getScore("   ");
            Score rubinsScore = objective.getScore( ChatColor.RED + "✦ " + ChatColor.WHITE + "Рубины: " + ChatColor.RED + rubins);
            Score air4 = objective.getScore("    ");
            Score repScore = objective.getScore( ChatColor.GREEN + "☻ " + ChatColor.WHITE + "Репутация: " + ChatColor.GREEN + rep);
            Score air5 = objective.getScore("     ");
            air1.setScore(9);
            manaline.setScore(8);
            air2.setScore(7);
            runaline1.setScore(6);
            runaline2.setScore(5);
            air3.setScore(4);
            rubinsScore.setScore(3);
            air4.setScore(2);
            repScore.setScore(1);
            air5.setScore(0);

            player.setScoreboard(scoreboard);
        }
    }

    private String getRuna(Player player) {
        if (player != null) {
            if (RunaMenu.getPlayerDataRunaMap().containsKey(player.getUniqueId())) {
                if (RunaMenu.isItemInSlotRuna(player)) {
                    if (RunaMenu.checkRunaMenu(player, 101) != null) {
                        return "[Мана]";
                    } else if (RunaMenu.checkRunaMenu(player, 102) != null) {
                        return "[Анонимность]";
                    } else if (RunaMenu.checkRunaMenu(player, 103) != null) {
                        return "[Уклонение]";
                    } else {
                        return "[Неизвестная]";
                    }
                } else {
                    return "Отсутствует";
                }
            } else {
                return "Отсутствует";
            }
        } else {
            return "Отсутствует";
        }
    }


    private int getRubins(Player player) {
        PlayerDataRubins playerData = new PlayerDataRubins(player.getUniqueId());
        return playerData.getRubins();
    }

    private int getRep(Player player) {
        PlayerDataRep playerDataRep = new PlayerDataRep(player.getUniqueId());
        return playerDataRep.getRep();
    }
}