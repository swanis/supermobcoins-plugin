package me.swanis.mobcoins.listeners;

import com.bgsoftware.wildstacker.api.events.EntityUnstackEvent;
import me.swanis.mobcoins.MobCoins;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class WildStackerListener implements Listener {

    private MobCoins instance;

    public WildStackerListener(MobCoins instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onEntityUnstack(EntityUnstackEvent event) {
        instance.getStackManager().getStackedEntities().put(event.getEntity().getLivingEntity(), event.getEntity().getStackAmount());
    }
}
