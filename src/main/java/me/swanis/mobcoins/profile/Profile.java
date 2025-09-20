package me.swanis.mobcoins.profile;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Profile implements Comparable<Profile> {

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

    @Override
    public int compareTo(Profile otherProfile) {
        long diff = this.mobCoins - otherProfile.getMobCoins();

        if (diff > 0) return 1;
        else if (diff < 0) return -1;
        else return 0;
    }
}
