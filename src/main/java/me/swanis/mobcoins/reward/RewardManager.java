package me.swanis.mobcoins.reward;

import me.swanis.mobcoins.MobCoins;
import me.swanis.mobcoins.utils.ItemBuilder;
import me.swanis.mobcoins.utils.StringUtil;
import me.swanis.mobcoins.utils.YamlFile;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class RewardManager {

    private MobCoins instance;

    private YamlFile rewardsFile;
    private YamlFile lastRewardsFile;
    private List<Reward> rewards = new ArrayList<>();
    private Map<Integer, Reward> currentRewards = new HashMap<>();

    public RewardManager(MobCoins instance) {
        this.instance = instance;

        rewardsFile = new YamlFile("rewards", instance);
        lastRewardsFile = new YamlFile("lastrewards", instance);
        loadRewards();
    }

    private void loadRewards() {
        FileConfiguration config = rewardsFile.getConfig();

        config.getKeys(false).forEach(string -> {
            String name = StringUtil.color(config.getString(string + ".name"));
            String command = config.getString(string + ".command");
            int price = config.getInt(string + ".price");
            Material material = Material.valueOf(config.getString(string + ".material"));
            int amount =  config.getInt(string + ".amount");
            List<String> lore = new ArrayList<>();
            config.getStringList(string + ".lore").forEach(line -> lore.add(StringUtil.color(line)));
            short durability = (short) config.getInt(string + ".durability");
            boolean special = config.getBoolean(string + ".special");
            int slot = config.getInt(string + ".slot");

            Reward reward = new Reward(string, name, command, price, material, amount, lore, durability, special, slot);
            rewards.add(reward);
        });
    }

    public void saveLastRewards() {
        FileConfiguration config = lastRewardsFile.getConfig();

        config.set("normaltime", instance.getNormalTime());
        config.set("specialtime", instance.getSpecialTime());

        currentRewards.values().forEach(reward -> {
            config.set(reward.getConfigKey() + ".name", reward.getName());
            config.set(reward.getConfigKey() + ".command", reward.getCommand());
            config.set(reward.getConfigKey() + ".price", reward.getPrice());
            config.set(reward.getConfigKey() + ".material", reward.getMaterial().name());
            config.set(reward.getConfigKey() + ".amount", reward.getAmount());
            config.set(reward.getConfigKey() + ".lore", reward.getLore());
            config.set(reward.getConfigKey() + ".durability", reward.getDurability());
            config.set(reward.getConfigKey() + ".special", reward.isSpecial());
            config.set(reward.getConfigKey() + ".slot", reward.getSlot());
        });

        lastRewardsFile.save();
    }

    public void loadLastRewards() {
        FileConfiguration config = lastRewardsFile.getConfig();

        if(config.getKeys(false).size() == 0) return;

        instance.setNormalTime(config.getLong("normaltime"));
        instance.setSpecialTime(config.getLong("specialtime"));
        instance.setLoaded(true);

        config.getKeys(false).forEach(string -> {
            Reward reward = getReward(string);

            if(reward == null) return;

            currentRewards.put(reward.getSlot(), reward);

            List<String> lore = new ArrayList<>();
            reward.getLore().forEach(line -> lore.add(line.replace("%price%", String.valueOf(reward.getPrice()))));

            ItemStack rewardItem = new ItemBuilder(reward.getMaterial())
                    .setName(reward.getName())
                    .setAmount(reward.getAmount())
                    .setLore(lore)
                    .setDurability(reward.getDurability())
                    .toItemStack();

            instance.getInventory().setItem(reward.getSlot(), rewardItem);
        });
    }

    public void refreshNormalRewards() {
        Random random = new Random();
        Set<Integer> usedSlots = new HashSet<>();

        rewards.forEach(reward -> {
            if(usedSlots.contains(reward.getSlot())) return;

            usedSlots.add(reward.getSlot());
        });

        usedSlots.forEach(integer -> {
            List<Reward> rewardList = rewards.stream().filter(reward -> !reward.isSpecial()).filter(reward -> reward.getSlot() == integer).collect(Collectors.toList());

            if(rewardList.isEmpty()) return;

            int r = random.nextInt(rewardList.size());
            Reward reward = rewardList.get(r);

            currentRewards.put(reward.getSlot(), reward);

            int price = reward.getPrice();
            List<String> lore = new ArrayList<>();
            reward.getLore().forEach(string -> lore.add(string.replace("%price%", String.valueOf(price))));

            ItemStack rewardItem = new ItemBuilder(reward.getMaterial())
                    .setName(reward.getName())
                    .setAmount(reward.getAmount())
                    .setLore(lore)
                    .setDurability(reward.getDurability())
                    .toItemStack();

            instance.getInventory().setItem(reward.getSlot(), rewardItem);
        });
    }

    public void refreshSpecialRewards() {
        Random random = new Random();
        Set<Integer> usedSlots = new HashSet<>();

        rewards.forEach(reward -> {
            if(usedSlots.contains(reward.getSlot())) return;

            usedSlots.add(reward.getSlot());
        });

        usedSlots.forEach(integer -> {
            List<Reward> rewardList = rewards.stream().filter(reward -> reward.isSpecial()).filter(reward -> reward.getSlot() == integer).collect(Collectors.toList());

            if(rewardList.isEmpty()) return;

            int r = random.nextInt(rewardList.size());
            Reward reward = rewardList.get(r);

            currentRewards.put(reward.getSlot(), reward);

            int price = reward.getPrice();
            List<String> lore = new ArrayList<>();
            reward.getLore().forEach(string -> lore.add(string.replace("%price%", String.valueOf(price))));

            ItemStack rewardItem = new ItemBuilder(reward.getMaterial())
                    .setName(reward.getName())
                    .setAmount(reward.getAmount())
                    .setLore(lore)
                    .setDurability(reward.getDurability())
                    .toItemStack();

            instance.getInventory().setItem(reward.getSlot(), rewardItem);
        });
    }

    public void reloadRewards() {
        saveLastRewards();
        currentRewards.clear();
        rewardsFile = new YamlFile("rewards", instance);
        loadLastRewards();
    }

    public Reward getReward(String configKey) {
        return rewards.stream().filter(reward -> reward.getConfigKey().equals(configKey)).findFirst().orElse(null);
    }

    public List<Reward> getRewards() {
        return rewards;
    }

    public Map<Integer, Reward> getCurrentRewards() {
        return currentRewards;
    }
}
