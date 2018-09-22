package me.swanis.mobcoins.profile;

import org.bukkit.entity.Player;

public class Profile {

    private Player player;
    private int mobCoins;

    public Profile(Player player) {
        this.player = player;
    }


    public Player getPlayer() {
        return player;
    }

    public int getMobCoins() {
        return mobCoins;
    }

    public void setMobCoins(int mobCoins) {
        this.mobCoins = mobCoins;
    }
}
