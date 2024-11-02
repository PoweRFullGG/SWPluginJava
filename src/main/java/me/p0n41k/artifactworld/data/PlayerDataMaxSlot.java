package me.p0n41k.artifactworld.data;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PlayerDataMaxSlot {
    private final File file;
    private final FileConfiguration config;
    private int maxSlot;

    public PlayerDataMaxSlot(UUID playerUUID) {
        File dataFolder = new File("plugins/Artifactworld/artmaxslot");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        this.file = new File(dataFolder, playerUUID.toString() + ".yml");
        this.config = YamlConfiguration.loadConfiguration(file);

        int defaultValue = 1;
        if (!config.contains("maxslot")) {
            config.set("maxslot", defaultValue);
            save();
        }

        if (config.contains("maxslot")) {
            maxSlot = config.getInt("maxslot");
        }
    }

    public int getMaxSlot() {
        return maxSlot;
    }

    public void setMaxSlot(int value) {
        this.maxSlot = value;
        config.set("maxslot", value);
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
