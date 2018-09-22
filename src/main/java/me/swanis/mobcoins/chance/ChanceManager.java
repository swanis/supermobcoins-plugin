package me.swanis.mobcoins.chance;

import me.swanis.mobcoins.MobCoins;
import me.swanis.mobcoins.utils.YamlFile;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

public class ChanceManager {

    private MobCoins instance;

    private YamlFile chancesFile;
    private Map<EntityType, DropChance> dropChances = new HashMap<>();

    public ChanceManager(MobCoins instance) {
        this.instance = instance;

        chancesFile = new YamlFile("dropchances", instance);
        loadChances();
    }

    public DropChance getChance(EntityType entityType) {
        return dropChances.get(entityType);
    }

    private void loadChances() {
        FileConfiguration config = chancesFile.getConfig();

        config.getConfigurationSection("Chance").getKeys(false).forEach(string -> {
            EntityType entityType = EntityType.valueOf(config.getString("Chance." + string + ".type"));
            int chance = config.getInt("Chance." + string + ".chance");
            dropChances.put(entityType, new DropChance(chance));
        });
    }

    public void reloadChances() {
        chancesFile = new YamlFile("dropchances", instance);
        dropChances.clear();
        loadChances();
    }
}
