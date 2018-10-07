package me.swanis.mobcoins.storage;

import org.bukkit.entity.Player;

public interface Storable {

    void init();

    void loadProfile(Player player);

    void saveProfile(Player player);

}
