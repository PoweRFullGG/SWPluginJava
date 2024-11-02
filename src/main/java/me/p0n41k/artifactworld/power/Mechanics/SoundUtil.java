package me.p0n41k.artifactworld.power.Mechanics;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundUtil {
    public static void playSoundForNearbyPlayers(Location location, Sound sound, float volume, float pitch, double radius) {
        for (Player nearbyPlayer : location.getWorld().getPlayers()) {
            if (nearbyPlayer.getLocation().distance(location) <= radius) {
                nearbyPlayer.playSound(location, sound, volume, pitch);
            }
        }
    }
}
