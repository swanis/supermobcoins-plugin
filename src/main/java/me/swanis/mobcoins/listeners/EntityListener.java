package me.swanis.mobcoins.listeners;

import com.bgsoftware.wildstacker.api.WildStackerAPI;
import me.swanis.mobcoins.Configuration;
import me.swanis.mobcoins.MobCoins;
import me.swanis.mobcoins.chance.DropChance;
import me.swanis.mobcoins.events.MobCoinsReceiveEvent;
import me.swanis.mobcoins.profile.Profile;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Random;

public class EntityListener implements Listener {

    private MobCoins instance;

    public EntityListener(MobCoins instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if(Configuration.MOBCOINS_ONLY_FROM_NATURALLY_SPAWNED_MOBS) {
            if(!event.getEntity().hasMetadata("naturallySpawned")) {
                return;
            }
        }

        Player killer = event.getEntity().getKiller();

        if(killer == null) return;

        Profile profile = instance.getProfileManager().getProfile(killer.getUniqueId());
        DropChance dropChance = instance.getChanceManager().getChance(event.getEntityType());

        if(dropChance == null) return;

        Random random = new Random();

        if(instance.hasWildstacker() && Configuration.STACKING_SUPPORT) {
            int amount = 0;

            for (int i = 0; i < WildStackerAPI.getEntityAmount(event.getEntity()); i++) {
                if(random.nextInt(100) > dropChance.getChance()) continue;

                amount++;
            }

            if(amount == 0) return;

            MobCoinsReceiveEvent mobCoinsReceiveEvent = new MobCoinsReceiveEvent(profile, amount);
            instance.getServer().getPluginManager().callEvent(mobCoinsReceiveEvent);
        } else {
            if(random.nextInt(100) > dropChance.getChance()) return;

            MobCoinsReceiveEvent mobCoinsReceiveEvent = new MobCoinsReceiveEvent(profile, 1);
            instance.getServer().getPluginManager().callEvent(mobCoinsReceiveEvent);
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();

        if(event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER) return;

        entity.setMetadata("naturallySpawned", new FixedMetadataValue(instance, true));
    }
}
