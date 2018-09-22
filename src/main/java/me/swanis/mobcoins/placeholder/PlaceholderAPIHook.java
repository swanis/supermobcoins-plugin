package me.swanis.mobcoins.placeholder;

import me.clip.placeholderapi.external.EZPlaceholderHook;
import me.swanis.mobcoins.MobCoins;
import me.swanis.mobcoins.profile.Profile;
import me.swanis.mobcoins.utils.TimeUtil;
import org.bukkit.entity.Player;

public class PlaceholderAPIHook extends EZPlaceholderHook {

    private MobCoins instance;

    public PlaceholderAPIHook(MobCoins instance) {
        super(instance, "supermobcoins");
        this.instance = instance;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if(identifier.equals("mobcoins")) {
            Profile profile = instance.getProfileManager().getProfile(player);

            if(profile == null) return null;

            return String.valueOf(profile.getMobCoins());
        }

        if(identifier.equals("normal_time")) {
            long normalTimeLeft = instance.getNormalTime() - System.currentTimeMillis();
            return TimeUtil.getFormattedString(normalTimeLeft);
        }

        if(identifier.equals("special_time")) {
            long specialTimeLeft = instance.getSpecialTime() - System.currentTimeMillis();
            return TimeUtil.getFormattedString(specialTimeLeft);
        }

        return null;
    }
}
