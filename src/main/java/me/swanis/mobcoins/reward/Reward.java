package me.swanis.mobcoins.reward;

import org.bukkit.Material;

import java.util.List;

public class Reward {

    private String name, command;
    private int price, amount;
    private Material material;
    private List<String> lore;
    private short durability;
    private boolean special;
    private int slot;

    public Reward(String name, String command, int price, Material material, int amount, List<String> lore, short durability, boolean special, int slot) {
        this.name = name;
        this.command = command;
        this.price = price;
        this.material = material;
        this.amount = amount;
        this.lore = lore;
        this.durability = durability;
        this.special = special;
        this.slot = slot;
    }


    public String getName() {
        return name;
    }

    public String getCommand() {
        return command;
    }

    public int getPrice() { return price; }

    public Material getMaterial() { return material; }

    public int getAmount() { return amount; }

    public List<String> getLore() { return lore; }

    public short getDurability() { return durability; }

    public boolean isSpecial() { return special; }

    public int getSlot() { return slot; }
}
