package me.swanis.mobcoins.storage.impl;

import me.swanis.mobcoins.MobCoins;
import me.swanis.mobcoins.profile.Profile;
import me.swanis.mobcoins.storage.Storable;
import me.swanis.mobcoins.utils.YamlFile;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;

public class YamlStorage implements Storable {

    private MobCoins instance;

    private YamlFile file;
    private FileConfiguration config;

    public YamlStorage(MobCoins instance) {
        this.instance = instance;
    }

    @Override
    public void init() {
        file = new YamlFile("profiles", instance);
        config = file.getConfig();
    }

    @Override
    public void loadProfile(UUID uuid) {
        String prefix = "Profile." + uuid.toString();

        Profile profile = new Profile(uuid);

        if(config.getConfigurationSection(prefix) == null) {
            instance.getProfileManager().load(profile);
            return;
        }

        int mobCoins = config.getInt(prefix + ".mobcoins");

        profile.setMobCoins(mobCoins);

        instance.getProfileManager().load(profile);
    }

    @Override
    public void saveProfile(UUID uuid) {
        Profile profile = instance.getProfileManager().getProfile(uuid);

        if(profile == null) return;

        String prefix = "Profile." + uuid.toString();

        config.set(prefix + ".mobcoins", profile.getMobCoins());

        Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
            file.save();
        });

        instance.getProfileManager().unload(profile);
    }
}
