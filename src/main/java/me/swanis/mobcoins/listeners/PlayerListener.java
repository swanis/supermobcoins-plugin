package me.swanis.mobcoins.listeners;

import me.swanis.mobcoins.Configuration;
import me.swanis.mobcoins.MobCoins;
import me.swanis.mobcoins.events.MobCoinsRedeemEvent;
import me.swanis.mobcoins.profile.Profile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerKick(PlayerKickEvent event) {
        Player player = event.getPlayer();

        instance.getStorage().saveProfile(player.getUniqueId());
    }

    @EventHandler
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
        if(!player.getInventory().getItemInHand().isSimilar(item)) return;

        //if(item.getType() != Configuration.MOBCOIN_ITEM_MATERIAL) return; (removed as it screws with 1.14 and 1.15 support)

        if(!item.hasItemMeta()) return;
        if(!item.getItemMeta().hasDisplayName()) return;
        if(!item.getItemMeta().getDisplayName().equals(Configuration.MOBCOIN_ITEM_NAME)) return;
        if(!item.getItemMeta().hasLore()) return;
        //if(!item.getItemMeta().getLore().equals(Configuration.MOBCOIN_ITEM_LORE)) return;
        if(!isSimilar(item.getItemMeta().getLore(), Configuration.MOBCOIN_ITEM_LORE)) return;

        Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());

        if(profile == null) return;

        event.setCancelled(true);

        MobCoinsRedeemEvent mobCoinsRedeemEvent = new MobCoinsRedeemEvent(profile, item.getAmount());
        instance.getServer().getPluginManager().callEvent(mobCoinsRedeemEvent);
    }

    private boolean isSimilar(List<String> first, List<String> second) {
        return first.size() == second.size() && !first.stream().anyMatch(s -> !first.contains(s));
    }
}
