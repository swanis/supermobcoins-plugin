package me.swanis.mobcoins.reward;

import me.swanis.mobcoins.MobCoins;
import me.swanis.mobcoins.utils.StringUtil;
import me.swanis.mobcoins.utils.YamlFile;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class RewardManager {

    private MobCoins instance;

    private YamlFile rewardsFile;
    private List<Reward> rewards = new ArrayList<>();

    public RewardManager(MobCoins instance) {
        this.instance = instance;

        rewardsFile = new YamlFile("rewards", instance);
        loadRewards();
    }

    public List<Reward> getRewards() {
        return rewards;
    }

    private void loadRewards() {
        FileConfiguration config = rewardsFile.getConfig();

        for(int i = 0; i<6; i++) {
            int slot = i + 1;
            config.getConfigurationSection("Reward.Normal.Slot" + slot).getKeys(false).forEach(string -> {
                String prefix = "Reward.Normal.Slot" + slot + "." + string;

                String name = StringUtil.color(config.getString(prefix + ".name"));
                String command = config.getString(prefix + ".command");
                int price = config.getInt(prefix + ".price");
                Material material = Material.valueOf(config.getString(prefix + ".material"));
                int amount =  config.getInt(prefix + ".amount");
                List<String> lore = new ArrayList<>();
                config.getStringList(prefix + ".lore").forEach(line -> lore.add(StringUtil.color(line)));
                short durability = (short) config.getInt(prefix + ".durability");

                Reward reward = new Reward(name, command, price, material, amount, lore, durability, false, slot);
                rewards.add(reward);
            });
        }

        for(int i = 0; i <2; i++) {
            int slot = i + 1;
            config.getConfigurationSection("Reward.Special.Slot" + slot).getKeys(false).forEach(string -> {
                String prefix = "Reward.Special.Slot" + slot + "." + string;

                String name = StringUtil.color(config.getString(prefix + ".name"));
                String command = config.getString(prefix + ".command");
                int price = config.getInt(prefix + ".price");
                Material material = Material.valueOf(config.getString(prefix + ".material"));
                int amount =  config.getInt(prefix + ".amount");
                List<String> lore = new ArrayList<>();
                config.getStringList(prefix + ".lore").forEach(line -> lore.add(StringUtil.color(line)));
                short durability = (short) config.getInt(prefix + ".durability");

                Reward reward = new Reward(name, command, price, material, amount, lore, durability, true, slot);
                rewards.add(reward);
            });
        }
    }

    public void reloadRewards() {
        rewardsFile = new YamlFile("rewards", instance);
        rewards.clear();
        loadRewards();
        instance.loadInventory();
    }
}
