package me.swanis.mobcoins.storage.impl;

import me.swanis.mobcoins.Configuration;
import me.swanis.mobcoins.MobCoins;
import me.swanis.mobcoins.profile.Profile;
import me.swanis.mobcoins.storage.Storable;
import me.swanis.mobcoins.utils.YamlFile;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class YamlStorage implements Storable {

    private MobCoins instance;

    private YamlFile file;
    private FileConfiguration config;

    public YamlStorage(MobCoins instance) {
        this.instance = instance;
    }

    @Override
    public boolean init() {
        file = new YamlFile("profiles", instance);
        config = file.getConfig();

        return true;
    }

    @Override
    public void loadProfile(UUID uuid) {
        String prefix = "Profile." + uuid.toString();

        Profile profile = new Profile(uuid);

        if(config.getConfigurationSection(prefix) == null) {
            instance.getProfileManager().load(profile);
            return;
        }

        long mobCoins = config.getLong(prefix + ".mobcoins");

        profile.setMobCoins(mobCoins);

        instance.getProfileManager().load(profile);
    }

    @Override
    public void saveProfile(UUID uuid) {
        Profile profile = instance.getProfileManager().getProfile(uuid);

        if (profile == null) return;

        String prefix = "Profile." + uuid.toString();

        config.set(prefix + ".mobcoins", profile.getMobCoins());

        file.save();

        instance.getProfileManager().unload(profile);
    }

    @Override
    public void set(UUID uuid, long mobCoins) {
        String prefix = "Profile." + uuid.toString();

        config.set(prefix + ".mobcoins", mobCoins);

        file.save();
    }

    @Override
    public boolean populateTopQueue() {
        ConfigurationSection profileSection = config.getConfigurationSection("Profile");

        if (profileSection == null) {
            return true;
        }

        Set<String> uuids = profileSection.getKeys(false);
        Iterator<String> it = uuids.iterator();

        while (it.hasNext()) {
            String uuidString = it.next();
            UUID uuid = UUID.fromString(uuidString);

            if (instance.getProfileManager().getProfile(uuid) != null) continue;

            long mobCoins = config.getLong("Profile." + uuid + ".mobcoins");

            Profile profile = new Profile(uuid);
            profile.setMobCoins(mobCoins);

            instance.getProfileManager().getTopQueue().add(profile);

            if (instance.getProfileManager().getTopQueue().size() > Configuration.MOBCOINS_TOP_TOTAL_ENTRIES) {
                instance.getProfileManager().getTopQueue().poll();
            }
        }

        return true;
    }
}
