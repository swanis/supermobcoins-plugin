package me.swanis.mobcoins.reward;

import org.bukkit.Material;

import java.util.List;

public class Reward {

    private String configKey, name;
    private List<String> commands;
    private int price, amount, slot;
    private Material material;
    private List<String> lore;
    private short durability;
    private boolean special, glow;

    public Reward(String configKey, String name, List<String> commands, int price, Material material, int amount, List<String> lore, short durability, boolean glow, boolean special, int slot) {
        this.configKey = configKey;
        this.name = name;
        this.commands = commands;
        this.price = price;
        this.material = material;
        this.amount = amount;
        this.lore = lore;
        this.durability = durability;
        this.glow = glow;
        this.special = special;
        this.slot = slot;
    }

    public String getConfigKey() {
        return configKey;
    }

    public String getName() {
        return name;
    }

    public List<String> getCommands() {
        return commands;
    }

    public int getPrice() { return price; }

    public Material getMaterial() { return material; }

    public int getAmount() { return amount; }

    public List<String> getLore() { return lore; }

    public short getDurability() { return durability; }

    public boolean isGlow() {
        return glow;
    }

    public boolean isSpecial() { return special; }

    public int getSlot() { return slot; }
}
