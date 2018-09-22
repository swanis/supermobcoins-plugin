package me.swanis.mobcoins.storage;

import me.swanis.mobcoins.profile.Profile;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface Storable {

    void init();

    void saveProfile(Profile profile);

    void loadProfile(Player player);
}
