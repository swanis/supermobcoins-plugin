package me.swanis.mobcoins.utils;

import java.util.UUID;

public class PlayerProfile implements Comparable<PlayerProfile> {

    private UUID uuid;
    private Long tokens;

    public PlayerProfile(UUID uuid, Long tokens) {
        this.uuid = uuid;
        this.tokens = tokens;
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setTokens(long newTokens) {
        this.tokens = newTokens;
    }

    public Long getTokens() {
        return tokens;
    }

    @Override
    public int compareTo(PlayerProfile otherPlayer) {
        long tokenDif = this.tokens - otherPlayer.getTokens();

        if (tokenDif > 1) return 1;
        else if (tokenDif < 1) return -1;
        else return 0;
    }
}
