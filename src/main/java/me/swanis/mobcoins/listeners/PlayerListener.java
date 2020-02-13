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
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {

    private MobCoins instance;

    public PlayerListener(MobCoins instance) {
        this.instance = instance;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        instance.getStorage().loadProfile(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerKick(PlayerKickEvent event) {
        if(event.isCancelled()) return;

        Player player = event.getPlayer();

        instance.getStorage().saveProfile(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        instance.getStorage().saveProfile(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if(event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;

        ItemStack item = event.getItem();

        if(item == null) return;

        //if(item.getType() != Configuration.MOBCOIN_ITEM_MATERIAL) return; (removed as it screws with 1.14 and 1.15 support)

        if(!item.hasItemMeta()) return;
        if(!item.getItemMeta().hasDisplayName()) return;
        if(!item.getItemMeta().getDisplayName().equals(Configuration.MOBCOIN_ITEM_NAME)) return;
        if(!item.getItemMeta().hasLore()) return;
        if(!item.getItemMeta().getLore().equals(Configuration.MOBCOIN_ITEM_LORE)) return;

        Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());

        if(profile == null) return;

        event.setCancelled(true);

        MobCoinsRedeemEvent mobCoinsRedeemEvent = new MobCoinsRedeemEvent(profile, item.getAmount());
        instance.getServer().getPluginManager().callEvent(mobCoinsRedeemEvent);
    }
}
