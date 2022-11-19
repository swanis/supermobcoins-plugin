package me.swanis.mobcoins.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.swanis.mobcoins.Configuration;
import me.swanis.mobcoins.MobCoins;
import me.swanis.mobcoins.profile.Profile;
import me.swanis.mobcoins.utils.FormatUtil;
import me.swanis.mobcoins.utils.TimeUtil;
import org.bukkit.entity.Player;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    private MobCoins instance;

    public PlaceholderAPIHook(MobCoins instance) {
        this.instance = instance;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "supermobcoins";
    }

    @Override
    public String getAuthor() {
        return instance.getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return instance.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (player == null) return null;

        switch(params) {
            case "mobcoins": {
                Profile profile = instance.getProfileManager().getProfile(player);

                if (profile == null) return null;

                return Configuration.FORMAT_ENABLED ? Configuration.FORMAT_NUMBER_FORMAT.format(profile.getMobCoins()) : String.valueOf(profile.getMobCoins());
            }

            case "mobcoins_formatted": {
                Profile profile = instance.getProfileManager().getProfile(player);

                if (profile == null) return null;

                return FormatUtil.format(profile.getMobCoins());
            }

            case "normal_time": {
                long normalTimeLeft = instance.getNormalTime() - System.currentTimeMillis();
                return TimeUtil.getFormattedString(normalTimeLeft);
            }

            case "special_time": {
                long specialTimeLeft = instance.getSpecialTime() - System.currentTimeMillis();
                return TimeUtil.getFormattedString(specialTimeLeft);
            }

            default:
                return null;
        }
    }
}
