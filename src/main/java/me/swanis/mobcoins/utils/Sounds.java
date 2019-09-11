package me.swanis.mobcoins.utils;

import org.bukkit.Sound;

/**
 * Version independent spigot sounds.
 */
public enum Sounds {
    ENDERDRAGON_HIT("ENDERDRAGON_HIT", "ENTITY_ENDERDRAGON_HURT", "ENTITY_ENDER_DRAGON_HURT");

    private String pre19sound;
    private String post19sound;
    private String post112sound;

    Sounds(String pre19sound, String post19sound, String post112sound) {
        this.pre19sound = pre19sound;
        this.post19sound = post19sound;
        this.post112sound = post112sound;
    }

    public Sound bukkitSound() {
        try {
            //Try pre 1.9 sound
            return Sound.valueOf(pre19sound);
        } catch (IllegalArgumentException e) {
            try {
                //Try post 1.9 sound
                return Sound.valueOf(post19sound);
            } catch (IllegalArgumentException ex) {
                //Try post 1.12 sound
                return Sound.valueOf(post112sound);
            }
        }
    }
}