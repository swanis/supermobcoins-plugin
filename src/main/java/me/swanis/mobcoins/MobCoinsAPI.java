package me.swanis.mobcoins;

import me.swanis.mobcoins.chance.ChanceManager;
import me.swanis.mobcoins.profile.ProfileManager;
import me.swanis.mobcoins.reward.RewardManager;
import me.swanis.mobcoins.storage.Storable;

public class MobCoinsAPI {

    private static MobCoins instance;

    public MobCoinsAPI(MobCoins instance) {
        this.instance = instance;
    }

    /*
    Retrieve the profile manager
    */
    public static ProfileManager getProfileManager() {
        return instance.getProfileManager();
    }

    /*
    Retrieve the reward manager
    */
    public static RewardManager getRewardManager() {
        return instance.getRewardManager();
    }

    /*
    Retrieve the dropchance manager
    */
    public static ChanceManager getChanceManager() {
        return instance.getChanceManager();
    }

    /*
    Retrieve the storage
    */
    public static Storable getStorage() {
        return instance.getStorage();
    }

    /*
    Refresh the normal rewards in the shop
    */
    public static void updateNormalRewards() {
        instance.updateNormalRewards();
    }

    /*
    Refresh the special rewards in the shop
    */
    public static void updateSpecialRewards() {
        instance.updateNormalRewards();
    }
}
