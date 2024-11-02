package me.p0n41k.artifactworld.data;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class ManaMechanics {
    private final File file;
    private final FileConfiguration config;
    private int maxMana;
    private int secondMana;

    public ManaMechanics(UUID playerUUID) {
        File dataFolder = new File("plugins/Artifactworld/MaxManaPlayer");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        this.file = new File(dataFolder, playerUUID.toString() + ".yml");
        this.config = YamlConfiguration.loadConfiguration(file);

        int defaultValue = 100;
        if (!config.contains("maxmana")) {
            config.set("maxmana", defaultValue);
            save();
        }

        if (config.contains("maxmana")) {
            maxMana = config.getInt("maxmana");
        }

        if (config.contains("secondmana")) {
            secondMana = config.getInt("secondmana");
        } else {
            secondMana = 0;
            config.set("secondmana", 0);
            save();
        }
    }

    public int getMaxMana() {
        return maxMana;
    }

    public void setMaxMana(int value) {
        this.maxMana = value;
        config.set("maxmana", value);

        if (secondMana > maxMana) {
            secondMana = maxMana;
            config.set("secondmana", secondMana);
        }

        save();
    }


    public int getSecondMana() {
        return secondMana;
    }

    public void setSecondMana(int value) {
        this.secondMana = value;
        config.set("secondmana", value);
        save();
    }

    public void addSecondMana(int value) {
        if (secondMana + value <= maxMana) {
            secondMana += value;
        } else {
            secondMana = Math.min(secondMana + value, maxMana);
        }
        config.set("secondmana", secondMana);
        save();
    }


    private void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
