package me.swanis.mobcoins.profile;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileManager {

    private Map<UUID, Profile> profiles = new HashMap<>();

    public void load(Profile profile) {
        profiles.put(profile.getPlayer().getUniqueId(), profile);
    }

    public void unload(Profile profile) {
        profiles.remove(profile.getPlayer().getUniqueId());
    }

    public Profile getProfile(UUID uuid) {
        return profiles.get(uuid);
    }

    public Profile getProfile(Player player) {
        return profiles.get(player.getUniqueId());
    }

    public Collection<Profile> getProfiles() {
        return profiles.values();
    }
}
