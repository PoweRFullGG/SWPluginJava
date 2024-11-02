package me.p0n41k.artifactworld.data;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PlayerDataRep {
    private final File file;
    private final FileConfiguration config;
    private int rep;

    public PlayerDataRep(UUID playerUUID) {
        File dataFolder = new File("plugins/Artifactworld/playerRep");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        this.file = new File(dataFolder, playerUUID.toString() + ".yml");
        this.config = YamlConfiguration.loadConfiguration(file);

        int defaultValue = 0;
        if (!config.contains("rep")) {
            config.set("rep", defaultValue);
            save();
        }

        if (config.contains("rep")) {
            rep = config.getInt("rep");
        }
    }

    public int getRep() {
        return rep;
    }

    public void setRep(int value) {
        this.rep = value;
        config.set("rep", value);
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
