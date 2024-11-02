package me.p0n41k.artifactworld.power.Mechanics;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class Silence {
    private static final Map<Player, Integer> silenceState = new HashMap<>();

    public static void setSilenceState(Player player, int state) {
        silenceState.put(player, state);
    }

    public static void plusSilenceState(Player player, int state) {
        int getSLNC = getSilenceState(player);
        silenceState.put(player, getSLNC+state);
    }

    public static int getSilenceState(Player player) {
        return silenceState.getOrDefault(player, 0);
    }
}
