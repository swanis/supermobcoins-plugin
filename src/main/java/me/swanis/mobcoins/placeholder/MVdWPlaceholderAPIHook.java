package me.swanis.mobcoins.placeholder;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;
import me.swanis.mobcoins.Configuration;
import me.swanis.mobcoins.MobCoins;
import me.swanis.mobcoins.profile.Profile;
import me.swanis.mobcoins.utils.TimeUtil;

public class MVdWPlaceholderAPIHook {

    private MobCoins instance;

    public MVdWPlaceholderAPIHook(MobCoins instance) {
        this.instance = instance;
    }

    public void hook() {
        PlaceholderAPI.registerPlaceholder(instance, "supermobcoins_mobcoins", new PlaceholderReplacer() {
            @Override
            public String onPlaceholderReplace(PlaceholderReplaceEvent placeholderReplaceEvent) {
                Profile profile = instance.getProfileManager().getProfile(placeholderReplaceEvent.getPlayer());

                if(profile == null) return "null";

                return Configuration.FORMAT_ENABLED ? Configuration.FORMAT_NUMBER_FORMAT.format(profile.getMobCoins()) : String.valueOf(profile.getMobCoins());
            }
        });

        PlaceholderAPI.registerPlaceholder(instance, "supermobcoins_normal_time", new PlaceholderReplacer() {
            @Override
            public String onPlaceholderReplace(PlaceholderReplaceEvent placeholderReplaceEvent) {
                long normalTimeLeft = instance.getNormalTime() - System.currentTimeMillis();
                return TimeUtil.getFormattedString(normalTimeLeft);
            }
        });

        PlaceholderAPI.registerPlaceholder(instance, "supermobcoins_special_time", new PlaceholderReplacer() {
            @Override
            public String onPlaceholderReplace(PlaceholderReplaceEvent placeholderReplaceEvent) {
                long specialTimeLeft = instance.getSpecialTime() - System.currentTimeMillis();
                return TimeUtil.getFormattedString(specialTimeLeft);
            }
        });
    }
}
