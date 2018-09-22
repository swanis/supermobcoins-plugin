package me.swanis.mobcoins.storage.impl;

import me.swanis.mobcoins.MobCoins;
import me.swanis.mobcoins.profile.Profile;
import me.swanis.mobcoins.storage.Storable;
import me.swanis.mobcoins.utils.YamlFile;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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
    public void saveProfile(Profile profile) {
        String prefix = "Profile." + profile.getPlayer().getUniqueId().toString();

        config.set(prefix + ".mobcoins", profile.getMobCoins());
        file.save();
    }

    @Override
    public void loadProfile(Player player) {
        String prefix = "Profile." + player.getUniqueId().toString();

        Profile profile = new Profile(player);

        if(config.getConfigurationSection(prefix) == null) {
            instance.getProfileManager().load(profile);
            return;
        }

        int mobCoins = config.getInt(prefix + ".mobcoins");
        profile.setMobCoins(mobCoins);
        instance.getProfileManager().load(profile);
    }
}
