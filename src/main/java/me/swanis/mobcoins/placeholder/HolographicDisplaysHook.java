package me.swanis.mobcoins.placeholder;

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.placeholder.PlaceholderReplacer;
import me.swanis.mobcoins.MobCoins;
import me.swanis.mobcoins.utils.TimeUtil;

public class HolographicDisplaysHook {

    private MobCoins instance;

    public HolographicDisplaysHook(MobCoins instance) {
        this.instance = instance;
    }

    public void hook() {
        HologramsAPI.registerPlaceholder(instance, "%supermobcoins_normal_time%", 1, new PlaceholderReplacer() {
            @Override
            public String update() {
                long normalTimeLeft = instance.getNormalTime() - System.currentTimeMillis();
                return TimeUtil.getFormattedString(normalTimeLeft);
            }
        });

        HologramsAPI.registerPlaceholder(instance, "%supermobcoins_special_time%", 1, new PlaceholderReplacer() {
            @Override
            public String update() {
                long specialTimeLeft = instance.getSpecialTime() - System.currentTimeMillis();
                return TimeUtil.getFormattedString(specialTimeLeft);
            }
        });
    }
}
