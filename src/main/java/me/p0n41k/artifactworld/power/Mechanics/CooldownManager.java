package me.p0n41k.artifactworld.power.Mechanics;

import java.util.HashMap;
import java.util.Map;

public class CooldownManager {
    public final Map<String, Long> cooldowns = new HashMap<>();

    public boolean hasCooldown(String playerName) {
        return cooldowns.containsKey(playerName) && System.currentTimeMillis() < cooldowns.get(playerName);
    }

    public void setCooldown(String playerName, int seconds) {
        long cooldownTime = System.currentTimeMillis() + (seconds * 1000);
        cooldowns.put(playerName, cooldownTime);
    }

    public void resetCooldown(String playerName) {
        cooldowns.remove(playerName);
    }
}
