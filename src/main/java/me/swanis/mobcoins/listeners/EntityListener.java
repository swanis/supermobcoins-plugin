package me.swanis.mobcoins.listeners;

import me.swanis.mobcoins.MobCoins;
import me.swanis.mobcoins.chance.DropChance;
import me.swanis.mobcoins.events.MobCoinsReceiveEvent;
import me.swanis.mobcoins.profile.Profile;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Random;

public class EntityListener implements Listener {

    private MobCoins instance;

    public EntityListener(MobCoins instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if(event.getEntity().getKiller() == null) return;

        Player killer = event.getEntity().getKiller();
        Profile profile = instance.getProfileManager().getProfile(killer.getUniqueId());
        DropChance dropChance = instance.getChanceManager().getChance(event.getEntityType());

        if(killer == null) return;
        if(profile == null) return;
        if(dropChance == null) return;

        Random random = new Random();

        if(random.nextInt(100) > dropChance.getChance()) return;

        MobCoinsReceiveEvent mobCoinsReceiveEvent = new MobCoinsReceiveEvent(profile, 1);
        instance.getServer().getPluginManager().callEvent(mobCoinsReceiveEvent);
    }
}
