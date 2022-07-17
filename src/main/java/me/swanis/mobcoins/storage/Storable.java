package me.swanis.mobcoins.storage;

import java.util.UUID;

public interface Storable {

    boolean init();

    void loadProfile(UUID uuid);

    void saveProfile(UUID uuid);

    void set(UUID uuid, long mobCoins);
}
