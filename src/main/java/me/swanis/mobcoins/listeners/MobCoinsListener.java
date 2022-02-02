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

        if(String.valueOf(event.getAmount()).length() > 19) {
            player.sendMessage(Configuration.AMOUNT_INPUT_TOO_LONG);
            return;
        }

        profile.setMobCoins(profile.getMobCoins() + event.getAmount());

        if(Configuration.RECEIVED_MOBCOIN_FROM_MOB_MESSAGE_SENT) {
            String amount = Configuration.FORMAT_ENABLED ? Configuration.FORMAT_NUMBER_FORMAT.format(event.getAmount()) : String.valueOf(event.getAmount());

            player.sendMessage(Configuration.RECEIVED_MOBCOIN_FROM_MOB_MESSAGE.replace("%amount%", amount));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMobCoinsRedeem(MobCoinsRedeemEvent event) {
        if(event.isCancelled()) return;

        Profile profile = event.getProfile();
        Player player = profile.getPlayer();

        if(String.valueOf(event.getAmount()).length() > 10) {
            player.sendMessage(Configuration.AMOUNT_INPUT_TOO_LONG);
            return;
        }

        profile.setMobCoins(profile.getMobCoins() + event.getAmount());

        player.getInventory().setItem(player.getInventory().getHeldItemSlot(), null);

        String amount = Configuration.FORMAT_ENABLED ? Configuration.FORMAT_NUMBER_FORMAT.format(event.getAmount()) : String.valueOf(event.getAmount());

        player.sendMessage(Configuration.REDEEMED_MOBCOIN_MESSAGE.replace("%amount%", amount));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMobCoinsShop(MobCoinsShopEvent event) {
        if(event.isCancelled()) return;

        Profile profile = event.getProfile();
        Player player = profile.getPlayer();

        if(String.valueOf(event.getReward().getPrice()).length() > 10) {
            player.sendMessage(Configuration.AMOUNT_INPUT_TOO_LONG);
            return;
        }

        event.getReward().getCommands().forEach(command -> instance.getServer().dispatchCommand(instance.getServer().getConsoleSender(), command.replace("%name%", player.getName()).replace("%uuid%", player.getUniqueId().toString())));

        profile.setMobCoins(profile.getMobCoins() - event.getPrice());

        String amount = Configuration.FORMAT_ENABLED ? Configuration.FORMAT_NUMBER_FORMAT.format(event.getReward().getPrice()) : String.valueOf(event.getReward().getPrice());

        player.sendMessage(Configuration.BOUGHT_REWARD_MESSAGE.replace("%reward%", event.getReward().getName()).replace("%amount%", amount));
    }
}
