package me.p0n41k.artifactworld.data;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

public class PlayerDataRuna {
    private final File file;
    private final FileConfiguration config;

    public PlayerDataRuna(UUID playerUUID) {
        File dataFolder = new File("plugins/Artifactworld/runamenuinf");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        this.file = new File(dataFolder, playerUUID.toString() + ".yml");
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public ItemStack[] loadInventory() {
        if (config.contains("inventory")) {
            return config.getList("inventory").toArray(new ItemStack[0]);
        } else {
            return new ItemStack[0];
        }
    }

    public void saveInventory(ItemStack[] inventory) {
        config.set("inventory", Arrays.asList(inventory));
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
