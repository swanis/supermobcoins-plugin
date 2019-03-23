package me.swanis.mobcoins.listeners;

import me.swanis.mobcoins.Configuration;
import me.swanis.mobcoins.MobCoins;
import me.swanis.mobcoins.profile.Profile;
import me.swanis.mobcoins.reward.Reward;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
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
        if(!inventory.getTitle().equals(Configuration.GUI_TITLE)) return;

        Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());

        if(profile == null) {
            event.setCancelled(true);
            closeInventory(player);
            player.sendMessage(Configuration.PROFILE_NOT_FOUND_MESSAGE.replace("%profile%", player.getUniqueId().toString()));
            return;
        }

        if(inventory.getItem(event.getSlot()) != null) {
            Reward reward = instance.getRewardManager().getCurrentRewards().values().stream().filter(r -> r.getSlot() == event.getSlot()).findFirst().orElse(null);

            if(reward == null) {
                event.setCancelled(true);
                return;
            }

            if(profile.getMobCoins() < reward.getPrice()) {
                event.setCancelled(true);
                player.sendMessage(Configuration.NOT_ENOUGH_MOBCOINS_MESSAGE);
                return;
            }

            instance.getServer().dispatchCommand(instance.getServer().getConsoleSender(), reward.getCommand().replace("%name%", player.getName()).replace("%uuid%", player.getUniqueId().toString()));
            profile.setMobCoins(profile.getMobCoins() - reward.getPrice());
            player.sendMessage(Configuration.BOUGHT_REWARD_MESSAGE.replace("%reward%", reward.getName()).replace("%amount%", String.valueOf(reward.getPrice())));
        }

        event.setCancelled(true);

        if(Configuration.CLOSE_GUI_ON_BUY) {
            closeInventory(player);
        }
    }

    private void closeInventory(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                player.closeInventory();
            }
        }.runTaskLater(instance, 1L);
    }

    /*
    private int getRewardSlot(int slot, boolean special) {
        if(!special) {
            if (slot == Configuration.GUI_REWARDSLOT_1) return 1;
            if (slot == Configuration.GUI_REWARDSLOT_2) return 2;
            if (slot == Configuration.GUI_REWARDSLOT_3) return 3;
            if (slot == Configuration.GUI_REWARDSLOT_4) return 4;
            if (slot == Configuration.GUI_REWARDSLOT_5) return 5;
            if (slot == Configuration.GUI_REWARDSLOT_6) return 6;
        } else {
            if (slot == Configuration.GUI_SPECIAL_REWARDSLOT_1) return 1;
            if (slot == Configuration.GUI_SPECIAL_REWARDSLOT_2) return 2;
        }

        return 0;
    }
    */
}
