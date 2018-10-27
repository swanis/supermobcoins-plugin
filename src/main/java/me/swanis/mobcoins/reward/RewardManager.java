package me.swanis.mobcoins.reward;

import me.swanis.mobcoins.Configuration;
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
    private List<Reward> currentRewards = new ArrayList<>();

    public RewardManager(MobCoins instance) {
        this.instance = instance;

        rewardsFile = new YamlFile("rewards", instance);
        lastRewardsFile = new YamlFile("lastrewards", instance);
        loadRewards();
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

    public void saveLastRewards() {
        FileConfiguration config = lastRewardsFile.getConfig();

        config.set("normaltime", instance.getNormalTime());
        config.set("specialtime", instance.getSpecialTime());

        currentRewards.forEach(reward -> {
            String prefix = !reward.isSpecial() ? String.valueOf(reward.getSlot()) : "Special" + reward.getSlot();

            config.set(prefix + ".name", reward.getName());
            config.set(prefix + ".command", reward.getCommand());
            config.set(prefix + ".price", reward.getPrice());
            config.set(prefix + ".material", reward.getMaterial().name());
            config.set(prefix + ".amount", reward.getAmount());
            config.set(prefix + ".lore", reward.getLore());
            config.set(prefix + ".durability", reward.getDurability());
        });

        lastRewardsFile.save();
    }

    public void loadLastRewards() {
        FileConfiguration config = lastRewardsFile.getConfig();
        List<Reward> rewardList = rewards.stream().filter(reward -> !reward.isSpecial()).collect(Collectors.toList());
        List<Reward> specialRewardList = rewards.stream().filter(reward -> reward.isSpecial()).collect(Collectors.toList());
        Random random = new Random();

        if(config.getKeys(false).size() == 0) return;

        instance.setNormalTime(config.getLong("normaltime"));
        instance.setSpecialTime(config.getLong("specialtime"));
        instance.setLoaded(true);

        for(int i = 0; i<6; i++) {
            int slot = i + 1;
            Reward reward = getReward(config.getString(slot + ".name"));

            if(reward == null) {
                int r = random.nextInt(rewardList.size());
                reward = rewardList.get(r);

                while(reward.getSlot() != slot) {
                    int rand = random.nextInt(rewardList.size());
                    reward = rewardList.get(rand);
                }
            }

            currentRewards.add(reward);

            int rewardSlot = getRewardSlot(slot, false);
            int price = reward.getPrice();
            List<String> lore = new ArrayList<>();
            reward.getLore().forEach(string -> lore.add(string.replace("%price%", String.valueOf(price))));

            ItemStack rewardItem = new ItemBuilder(reward.getMaterial())
                    .setName(reward.getName())
                    .setAmount(reward.getAmount())
                    .setLore(lore)
                    .setDurability(reward.getDurability())
                    .toItemStack();

            instance.getInventory().setItem(rewardSlot, rewardItem);
        }

        for(int i = 0; i<2; i++) {
            int slot = i + 1;
            Reward reward = getReward(config.getString("Special" + slot + ".name"));

            if(reward == null) {
                int r = random.nextInt(specialRewardList.size());
                reward = specialRewardList.get(r);

                while(reward.getSlot() != slot) {
                    int rand = random.nextInt(specialRewardList.size());
                    reward = specialRewardList.get(rand);
                }
            }

            currentRewards.add(reward);

            int rewardSlot = getRewardSlot(slot, true);
            int price = reward.getPrice();
            List<String> lore = new ArrayList<>();
            reward.getLore().forEach(string -> lore.add(string.replace("%price%", String.valueOf(price))));

            ItemStack rewardItem = new ItemBuilder(reward.getMaterial())
                    .setName(reward.getName())
                    .setAmount(reward.getAmount())
                    .setLore(lore)
                    .setDurability(reward.getDurability())
                    .toItemStack();

            instance.getInventory().setItem(rewardSlot, rewardItem);
        }
    }

    public void refreshNormalRewards() {
        currentRewards.stream().filter(reward -> !reward.isSpecial()).collect(Collectors.toSet()).forEach(currentRewards::remove);
        Random random = new Random();

        instance.getInventory().setItem(Configuration.GUI_REWARDSLOT_1, null);
        instance.getInventory().setItem(Configuration.GUI_REWARDSLOT_2, null);
        instance.getInventory().setItem(Configuration.GUI_REWARDSLOT_3, null);
        instance.getInventory().setItem(Configuration.GUI_REWARDSLOT_4, null);
        instance.getInventory().setItem(Configuration.GUI_REWARDSLOT_5, null);
        instance.getInventory().setItem(Configuration.GUI_REWARDSLOT_6, null);

        for(int i = 0; i < 6; i++) {
            List<Reward> rewardList = rewards.stream().filter(reward -> !reward.isSpecial()).collect(Collectors.toList());
            int r = random.nextInt(rewardList.size());
            Reward reward = rewardList.get(r);

            int slot = getRewardSlot(reward.getSlot(), false);

            while(instance.getInventory().getItem(slot) != null) {
                int rand = random.nextInt(rewardList.size());
                reward = rewardList.get(rand);
                slot = getRewardSlot(reward.getSlot(), false);
            }

            while(currentRewards.contains(reward)) {
                int rand = random.nextInt(rewardList.size());
                reward = rewardList.get(rand);
            }

            currentRewards.add(reward);

            int price = reward.getPrice();
            List<String> lore = new ArrayList<>();
            reward.getLore().forEach(string -> lore.add(string.replace("%price%", String.valueOf(price))));

            ItemStack rewardItem = new ItemBuilder(reward.getMaterial())
                    .setName(reward.getName())
                    .setAmount(reward.getAmount())
                    .setLore(lore)
                    .setDurability(reward.getDurability())
                    .toItemStack();

            instance.getInventory().setItem(slot, rewardItem);
        }
    }

    public void refreshSpecialRewards() {
        currentRewards.stream().filter(reward -> reward.isSpecial()).collect(Collectors.toSet()).forEach(currentRewards::remove);
        Random random = new Random();

        instance.getInventory().setItem(Configuration.GUI_SPECIAL_REWARDSLOT_1, null);
        instance.getInventory().setItem(Configuration.GUI_SPECIAL_REWARDSLOT_2, null);

        for (int i = 0; i < 2; i++) {
            List<Reward> rewardList = rewards.stream().filter(reward -> reward.isSpecial()).collect(Collectors.toList());
            int r = random.nextInt(rewardList.size());
            Reward reward = rewardList.get(r);

            int slot = getRewardSlot(reward.getSlot(), true);

            while(instance.getInventory().getItem(slot) != null) {
                int rand = random.nextInt(rewardList.size());
                reward = rewardList.get(rand);
                slot = getRewardSlot(reward.getSlot(), true);
            }

            while(currentRewards.contains(reward)) {
                int rand = random.nextInt(rewardList.size());
                reward = rewardList.get(rand);
            }

            currentRewards.add(reward);

            int price = reward.getPrice();
            List<String> lore = new ArrayList<>();
            reward.getLore().forEach(string -> lore.add(string.replace("%price%", String.valueOf(price))));

            ItemStack rewardItem = new ItemBuilder(reward.getMaterial())
                    .setName(reward.getName())
                    .setAmount(reward.getAmount())
                    .setLore(lore)
                    .setDurability(reward.getDurability())
                    .toItemStack();

            instance.getInventory().setItem(slot, rewardItem);
        }
    }

    public void reloadRewards() {
        rewardsFile = new YamlFile("rewards", instance);
        rewards.clear();
        loadRewards();
        instance.loadInventory();
    }

    public Reward getReward(String name) {
        return rewards.stream().filter(reward -> reward.getName().equals(name)).findFirst().orElse(null);
    }

    public List<Reward> getRewards() {
        return rewards;
    }

    public List<Reward> getCurrentRewards() {
        return currentRewards;
    }

    private int getRewardSlot(int i, boolean special) {
        int slot = 0;

        if(!special) {
            if (i == 1) slot = Configuration.GUI_REWARDSLOT_1;
            if (i == 2) slot = Configuration.GUI_REWARDSLOT_2;
            if (i == 3) slot = Configuration.GUI_REWARDSLOT_3;
            if (i == 4) slot = Configuration.GUI_REWARDSLOT_4;
            if (i == 5) slot = Configuration.GUI_REWARDSLOT_5;
            if (i == 6) slot = Configuration.GUI_REWARDSLOT_6;
        } else {
            if (i == 1) slot = Configuration.GUI_SPECIAL_REWARDSLOT_1;
            if (i == 2) slot = Configuration.GUI_SPECIAL_REWARDSLOT_2;
        }

        return slot;
    }
}
