package me.swanis.mobcoins.listeners;

import me.swanis.mobcoins.Configuration;
import me.swanis.mobcoins.MobCoins;
import me.swanis.mobcoins.events.MobCoinsShopEvent;
import me.swanis.mobcoins.profile.Profile;
import me.swanis.mobcoins.reward.Reward;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

public class InventoryListener implements Listener {

    private MobCoins instance;

    public InventoryListener(MobCoins instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();

        if(inventory == null) return;
        if(!event.getView().getTitle().equals(Configuration.GUI_TITLE)) return;
        if (inventory.getHolder() instanceof Player && ((Player) inventory.getHolder()).getName().equals(player.getName())) {
            if (event.isShiftClick()) {
                event.setCancelled(true);
            }

            return;
        }

        Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());

        if(profile == null) {
            event.setCancelled(true);
            closeInventory(player);
            player.sendMessage(Configuration.PROFILE_NOT_FOUND_MESSAGE.replace("%profile%", player.getUniqueId().toString()));
            return;
        }

        event.setCancelled(true);

        if(inventory.getItem(event.getSlot()) != null) {
            Reward reward = instance.getRewardManager().getCurrentRewards().values().stream().filter(r -> r.getSlot() == event.getSlot()).findFirst().orElse(null);

            if(reward == null) {
                return;
            }

            if(profile.getMobCoins() < reward.getPrice()) {
                player.sendMessage(Configuration.NOT_ENOUGH_MOBCOINS_MESSAGE);
                return;
            }

            MobCoinsShopEvent mobCoinsShopEvent = new MobCoinsShopEvent(profile, reward, reward.getPrice());
            instance.getServer().getPluginManager().callEvent(mobCoinsShopEvent);

            if(Configuration.CLOSE_GUI_ON_BUY) {
                closeInventory(player);
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        Inventory inventory = event.getInventory();

        if(inventory == null) return;
        if(!event.getView().getTitle().equals(Configuration.GUI_TITLE)) return;

        event.setCancelled(true);
    }

    private void closeInventory(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                player.closeInventory();
            }
        }.runTaskLater(instance, 1L);
    }
}
