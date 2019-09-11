package me.swanis.mobcoins.stack;

import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.Map;

public class StackManager {

    private Map<Entity, Integer> stackedEntities = new HashMap<>();

    public Map<Entity, Integer> getStackedEntities() {
        return stackedEntities;
    }
}
