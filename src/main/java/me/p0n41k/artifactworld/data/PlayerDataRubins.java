package me.p0n41k.artifactworld.data;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PlayerDataRubins {
    private final File file;
    private final FileConfiguration config;
    private int rubins;

    public PlayerDataRubins(UUID playerUUID) {
        File dataFolder = new File("plugins/Artifactworld/rubinsplayer");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        this.file = new File(dataFolder, playerUUID.toString() + ".yml");
        this.config = YamlConfiguration.loadConfiguration(file);

        int defaultValue = 0;
        if (!config.contains("rubins")) {
            config.set("rubins", defaultValue);
            save();
        }

        if (config.contains("rubins")) {
            rubins = config.getInt("rubins");
        }
    }

    public int getRubins() {
        return rubins;
    }

    public void setRubins(int value) {
        this.rubins = value;
        config.set("rubins", value);
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
