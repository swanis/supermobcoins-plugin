package me.swanis.mobcoins.listeners;

import com.bgsoftware.wildstacker.api.WildStackerAPI;
import me.swanis.mobcoins.Configuration;
import me.swanis.mobcoins.MobCoins;
import me.swanis.mobcoins.chance.DropChance;
import me.swanis.mobcoins.events.MobCoinsReceiveEvent;
import me.swanis.mobcoins.profile.Profile;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;

public class EntityListener implements Listener {

    private MobCoins instance;

    public EntityListener(MobCoins instance) {
        this.instance = instance;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDeath(EntityDeathEvent event) {
        if(Configuration.DISABLED_WORLDS.contains(event.getEntity().getWorld().getName())) return;
        if(Configuration.MOBCOINS_ONLY_FROM_NATURALLY_SPAWNED_MOBS && !event.getEntity().hasMetadata("naturallySpawned")) return;

        Player killer = event.getEntity().getKiller();

        if(killer == null) return;

        Profile profile = instance.getProfileManager().getProfile(killer.getUniqueId());
        DropChance dropChance = event.getEntityType() == EntityType.SKELETON && ((Skeleton) event.getEntity()).getSkeletonType() == Skeleton.SkeletonType.WITHER ? instance.getChanceManager().getChance("WITHER_SKELETON") : instance.getChanceManager().getChance(event.getEntityType().name());

        if(dropChance == null) return;
        if(event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent entityEvent = (EntityDamageByEntityEvent) event.getEntity().getLastDamageCause();

            if(entityEvent.getDamager().getType() == EntityType.WOLF) return;
        }

        if(instance.hasWildStacker() && Configuration.STACKING_SUPPORT) {
            if(WildStackerAPI.getStackedEntity(event.getEntity()) != null) {
                int stackedAmount = WildStackerAPI.getEntityAmount(event.getEntity());
                int amount = 0;

                for(int i = 0; i < stackedAmount; i++) {
                    if (Math.random() * 100 > dropChance.getChance()) continue;

                    amount++;
                }

                if(amount == 0) return;

                MobCoinsReceiveEvent mobCoinsReceiveEvent = new MobCoinsReceiveEvent(profile, amount);
                instance.getServer().getPluginManager().callEvent(mobCoinsReceiveEvent);
                return;
            }
        }

        if (Math.random() * 100 > dropChance.getChance()) return;

        MobCoinsReceiveEvent mobCoinsReceiveEvent = new MobCoinsReceiveEvent(profile, 1);
        instance.getServer().getPluginManager().callEvent(mobCoinsReceiveEvent);
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if(Configuration.MOBCOINS_ONLY_FROM_NATURALLY_SPAWNED_MOBS) {
            LivingEntity entity = event.getEntity();

            if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER) return;

            entity.setMetadata("naturallySpawned", new FixedMetadataValue(instance, true));
        }
    }
}
