package me.swanis.mobcoins.listeners;

import me.swanis.mobcoins.Configuration;
import me.swanis.mobcoins.MobCoins;
import me.swanis.mobcoins.MobCoinsAPI;
import me.swanis.mobcoins.events.MobCoinsReceiveEvent;
import me.swanis.mobcoins.events.MobCoinsRedeemEvent;
import me.swanis.mobcoins.profile.Profile;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {

    private MobCoins instance;

    public PlayerListener(MobCoins instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        instance.getStorage().loadProfile(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());

        instance.getStorage().saveProfile(profile);
        instance.getProfileManager().unload(profile);
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        Player player = event.getPlayer();
        Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());

        instance.getStorage().saveProfile(profile);
        instance.getProfileManager().unload(profile);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if(event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;

        ItemStack item = event.getItem();

        if(item == null) return;
        if(item.getType() != Configuration.MOBCOIN_ITEM_MATERIAL) return;
        if(!item.hasItemMeta()) return;
        if(!item.getItemMeta().hasDisplayName()) return;
        if(!item.getItemMeta().getDisplayName().equals(Configuration.MOBCOIN_ITEM_NAME)) return;

        Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());

        if(profile == null) return;

        event.setCancelled(true);

        MobCoinsRedeemEvent mobCoinsRedeemEvent = new MobCoinsRedeemEvent(profile, item.getAmount());
        instance.getServer().getPluginManager().callEvent(mobCoinsRedeemEvent);
    }
}
