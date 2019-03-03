package me.swanis.mobcoins.listeners;

import me.swanis.mobcoins.Configuration;
import me.swanis.mobcoins.events.MobCoinsReceiveEvent;
import me.swanis.mobcoins.events.MobCoinsRedeemEvent;
import me.swanis.mobcoins.profile.Profile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class MobCoinsListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMobCoinsReceiveEvent(MobCoinsReceiveEvent event) {
        if(event.isCancelled()) return;

        Profile profile = event.getProfile();
        Player player = profile.getPlayer();

        profile.setMobCoins(profile.getMobCoins() + event.getAmount());

        if(Configuration.RECEIVED_MOBCOIN_FROM_MOB_MESSAGE_SENT) {
            player.sendMessage(Configuration.RECEIVED_MOBCOIN_FROM_MOB_MESSAGE.replace("%amount%", String.valueOf(event.getAmount())));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMobCoinsRedeemEvent(MobCoinsRedeemEvent event) {
        if(event.isCancelled()) return;

        Profile profile = event.getProfile();
        Player player = profile.getPlayer();

        profile.setMobCoins(profile.getMobCoins() + event.getAmount());
        player.getInventory().setItem(player.getInventory().getHeldItemSlot(), null);
        player.sendMessage(Configuration.REDEEMED_MOBCOIN_MESSAGE.replace("%amount%", String.valueOf(event.getAmount())));
    }
}
