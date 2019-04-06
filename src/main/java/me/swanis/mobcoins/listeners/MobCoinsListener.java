package me.swanis.mobcoins.listeners;

import me.swanis.mobcoins.Configuration;
import me.swanis.mobcoins.MobCoins;
import me.swanis.mobcoins.events.MobCoinsReceiveEvent;
import me.swanis.mobcoins.events.MobCoinsRedeemEvent;
import me.swanis.mobcoins.events.MobCoinsShopEvent;
import me.swanis.mobcoins.profile.Profile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class MobCoinsListener implements Listener {

    private MobCoins instance;

    public MobCoinsListener(MobCoins instance) {
        this.instance = instance;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMobCoinsReceive(MobCoinsReceiveEvent event) {
        if(event.isCancelled()) return;

        Profile profile = event.getProfile();
        Player player = profile.getPlayer();

        profile.setMobCoins(profile.getMobCoins() + event.getAmount());

        if(Configuration.RECEIVED_MOBCOIN_FROM_MOB_MESSAGE_SENT) {
            player.sendMessage(Configuration.RECEIVED_MOBCOIN_FROM_MOB_MESSAGE.replace("%amount%", String.valueOf(event.getAmount())));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMobCoinsRedeem(MobCoinsRedeemEvent event) {
        if(event.isCancelled()) return;

        Profile profile = event.getProfile();
        Player player = profile.getPlayer();

        profile.setMobCoins(profile.getMobCoins() + event.getAmount());
        player.getInventory().setItem(player.getInventory().getHeldItemSlot(), null);
        player.sendMessage(Configuration.REDEEMED_MOBCOIN_MESSAGE.replace("%amount%", String.valueOf(event.getAmount())));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMobCoinsShop(MobCoinsShopEvent event) {
        if(event.isCancelled()) return;

        instance.getServer().dispatchCommand(instance.getServer().getConsoleSender(), event.getReward().getCommand().replace("%name%", event.getProfile().getPlayer().getName()).replace("%uuid%", event.getProfile().getPlayer().getUniqueId().toString()));
        event.getProfile().setMobCoins(event.getProfile().getMobCoins() - event.getPrice());
        event.getProfile().getPlayer().sendMessage(Configuration.BOUGHT_REWARD_MESSAGE.replace("%reward%", event.getReward().getName()).replace("%amount%", String.valueOf(event.getReward().getPrice())));
    }
}
