package me.swanis.mobcoins.profile;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Profile {

    private UUID uuid;
    private long mobCoins;

    public Profile(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUUID() {
        return uuid;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public long getMobCoins() {
        return mobCoins;
    }

    public void setMobCoins(long mobCoins) {
        this.mobCoins = mobCoins;
    }
}
