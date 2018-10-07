package me.swanis.mobcoins;

import me.swanis.mobcoins.chance.ChanceManager;
import me.swanis.mobcoins.commands.MobCoinsCommand;
import me.swanis.mobcoins.listeners.EntityListener;
import me.swanis.mobcoins.listeners.InventoryListener;
import me.swanis.mobcoins.listeners.MobCoinsListener;
import me.swanis.mobcoins.listeners.PlayerListener;
import me.swanis.mobcoins.placeholder.HolographicDisplaysHook;
import me.swanis.mobcoins.placeholder.MVdWPlaceholderAPIHook;
import me.swanis.mobcoins.placeholder.PlaceholderAPIHook;
import me.swanis.mobcoins.profile.ProfileManager;
import me.swanis.mobcoins.reward.Reward;
import me.swanis.mobcoins.reward.RewardManager;
import me.swanis.mobcoins.storage.Storable;
import me.swanis.mobcoins.storage.impl.YamlStorage;
import me.swanis.mobcoins.utils.ItemBuilder;
import me.swanis.mobcoins.utils.command.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class MobCoins extends JavaPlugin {

    private Storable storage;
    private Inventory inventory;

    private ProfileManager profileManager;
    private RewardManager rewardManager;
    private ChanceManager chanceManager;
    private CommandManager commandManager;

    private Set<Reward> rewards = new HashSet<>();
    private long normalTime;
    private long specialTime;
    private BukkitRunnable normalTimer;
    private BukkitRunnable specialTimer;

    @Override
    public void onEnable() {
        loadConfiguration();
        loadStorage();

        registerManagers();
        registerCommands();
        registerListeners();
        registerPlaceholders();

        getServer().getOnlinePlayers().forEach(storage::loadProfile);
        loadInventory();
        runTaskTimers();

        new MobCoinsAPI(this);
    }

    @Override
    public void onDisable() {
        getServer().getOnlinePlayers().forEach(storage::saveProfile);
    }

    private void loadConfiguration() {
        saveDefaultConfig();
        new Configuration(this);
    }

    private void loadStorage() {
        storage = new YamlStorage(this);
        storage.init();
    }

    private void registerManagers() {
        profileManager = new ProfileManager();
        rewardManager = new RewardManager(this);
        chanceManager = new ChanceManager(this);
        commandManager = new CommandManager(this);
    }

    private void registerCommands() {
        getCommand("mobcoins").setExecutor(new MobCoinsCommand(this));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
        getServer().getPluginManager().registerEvents(new MobCoinsListener(), this);
    }

    private void registerPlaceholders() {
        if(Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")) {
            new MVdWPlaceholderAPIHook(this).hook();
        }

        if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderAPIHook(this).hook();
        }

        if(Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
            new HolographicDisplaysHook(this).hook();
        }
    }

    private void runTaskTimers() {
        new BukkitRunnable() {
            @Override
            public void run() {
                normalTimer = this;

                updateNormalRewards();
                normalTime = System.currentTimeMillis() + (Configuration.MOBCOIN_NORMAL_SHOP_UPDATE_HOURS * (60 * 60)) * 1000;
                Bukkit.broadcastMessage(Configuration.MOBCOIN_NORMAL_SHOP_UPDATED_MESSAGE);
            }
        }.runTaskTimer(this, 0L,  (Configuration.MOBCOIN_NORMAL_SHOP_UPDATE_HOURS * (60 * 60)) * 20);

        new BukkitRunnable() {
            @Override
            public void run() {
                specialTimer = this;

                updateSpecialRewards();
                specialTime = System.currentTimeMillis() + (Configuration.MOBCOIN_SPECIAL_SHOP_UPDATE_HOURS * (60 * 60)) * 1000;
                Bukkit.broadcastMessage(Configuration.MOBCOIN_SPECIAL_SHOP_UPDATED_MESSAGE);
            }
        }.runTaskTimer(this, 0L, (Configuration.MOBCOIN_SPECIAL_SHOP_UPDATE_HOURS * (60 * 60)) * 20);
    }

    public void loadInventory() {
        inventory = Bukkit.createInventory(null, (Configuration.GUI_ROWS * 9), Configuration.GUI_TITLE);

        updateNormalRewards();
        updateSpecialRewards();

        ItemStack mobCoinsItem = new ItemBuilder(Configuration.GUI_MOBCOINS_ITEM_MATERIAL)
                .setName(Configuration.GUI_MOBCOINS_ITEM_NAME)
                .setLore(Configuration.GUI_MOBCOINS_ITEM_LORE)
                .toItemStack();
        inventory.setItem(Configuration.GUI_MOBCOINS_ITEM_SLOT, mobCoinsItem);
        ItemStack specialMobCoinsItem = new ItemBuilder(Configuration.GUI_SPECIAL_MOBCOINS_ITEM_MATERIAL)
                .setName(Configuration.GUI_SPECIAL_MOBCOINS_ITEM_NAME)
                .setLore(Configuration.GUI_SPECIAL_MOBCOINS_ITEM_LORE)
                .toItemStack();
        inventory.setItem(Configuration.GUI_SPECIAL_MOBCOINS_ITEM_SLOT, specialMobCoinsItem);
        ItemStack infoItem = new ItemBuilder(Configuration.GUI_INFO_ITEM_MATERIAL)
                .setName(Configuration.GUI_INFO_ITEM_NAME)
                .setLore(Configuration.GUI_INFO_ITEM_LORE)
                .toItemStack();
        inventory.setItem(Configuration.GUI_INFO_ITEM_SLOT, infoItem);
        ItemStack amountItem = new ItemBuilder(Configuration.GUI_AMOUNT_ITEM_MATERIAL)
                .setName(Configuration.GUI_AMOUNT_ITEM_NAME)
                .setLore(Configuration.GUI_AMOUNT_ITEM_LORE)
                .toItemStack();
        inventory.setItem(Configuration.GUI_AMOUNT_ITEM_SLOT, amountItem);

        if(Configuration.GUI_FILLER_ENABLED) {
            ItemStack fillerItem = new ItemBuilder(Configuration.GUI_FILLER_ITEM_MATERIAL)
                    .setName(Configuration.GUI_FILLER_ITEM_NAME)
                    .setDurability(Configuration.GUI_FILLER_ITEM_DURABILITY)
                    .toItemStack();

            for(int i = 0; i < inventory.getSize(); i++) {
                if(inventory.getItem(i) == null)
                    inventory.setItem(i, fillerItem);
            }
        }
    }

    public void updateNormalRewards() {
        rewards.stream().filter(reward -> !reward.isSpecial()).collect(Collectors.toSet()).forEach(rewards::remove);
        Random random = new Random();

        inventory.setItem(Configuration.GUI_REWARDSLOT_1, null);
        inventory.setItem(Configuration.GUI_REWARDSLOT_2, null);
        inventory.setItem(Configuration.GUI_REWARDSLOT_3, null);
        inventory.setItem(Configuration.GUI_REWARDSLOT_4, null);
        inventory.setItem(Configuration.GUI_REWARDSLOT_5, null);
        inventory.setItem(Configuration.GUI_REWARDSLOT_6, null);

        for(int i = 0; i < 6; i++) {
            List<Reward> rewardList = rewardManager.getRewards().stream().filter(reward -> !reward.isSpecial()).collect(Collectors.toList());
            int r = random.nextInt(rewardList.size());
            Reward reward;
            reward = rewardList.get(r);

            int slot = 0;

            if(reward.getSlot() == 1) slot = Configuration.GUI_REWARDSLOT_1;
            if(reward.getSlot() == 2) slot = Configuration.GUI_REWARDSLOT_2;
            if(reward.getSlot() == 3) slot = Configuration.GUI_REWARDSLOT_3;
            if(reward.getSlot() == 4) slot = Configuration.GUI_REWARDSLOT_4;
            if(reward.getSlot() == 5) slot = Configuration.GUI_REWARDSLOT_5;
            if(reward.getSlot() == 6) slot = Configuration.GUI_REWARDSLOT_6;

            while(inventory.getItem(slot) != null) {
                int rand = random.nextInt(rewardList.size());
                reward = rewardList.get(rand);
                if(reward.getSlot() == 1) slot = Configuration.GUI_REWARDSLOT_1;
                if(reward.getSlot() == 2) slot = Configuration.GUI_REWARDSLOT_2;
                if(reward.getSlot() == 3) slot = Configuration.GUI_REWARDSLOT_3;
                if(reward.getSlot() == 4) slot = Configuration.GUI_REWARDSLOT_4;
                if(reward.getSlot() == 5) slot = Configuration.GUI_REWARDSLOT_5;
                if(reward.getSlot() == 6) slot = Configuration.GUI_REWARDSLOT_6;
            }

            while(rewards.contains(reward)) {
                int rand = random.nextInt(rewardList.size());
                reward = rewardList.get(rand);
            }

            rewards.add(reward);

            int price = reward.getPrice();
            List<String> lore = new ArrayList<>();
            reward.getLore().forEach(string -> lore.add(string.replace("%price%", String.valueOf(price))));

            ItemStack rewardItem = new ItemBuilder(reward.getMaterial())
                    .setName(reward.getName())
                    .setAmount(reward.getAmount())
                    .setLore(lore)
                    .setDurability(reward.getDurability())
                    .toItemStack();
            inventory.setItem(slot, rewardItem);
        }
    }

    public void updateSpecialRewards() {
        rewards.stream().filter(reward -> reward.isSpecial()).collect(Collectors.toSet()).forEach(rewards::remove);
        Random random = new Random();

        inventory.setItem(Configuration.GUI_SPECIAL_REWARDSLOT_1, null);
        inventory.setItem(Configuration.GUI_SPECIAL_REWARDSLOT_2, null);

        for (int i = 0; i < 2; i++) {
            List<Reward> rewardList = rewardManager.getRewards().stream().filter(reward -> reward.isSpecial()).collect(Collectors.toList());
            int r = random.nextInt(rewardList.size());
            Reward reward;
            reward = rewardList.get(r);

            int slot = 0;

            if(reward.getSlot() == 1) slot = Configuration.GUI_SPECIAL_REWARDSLOT_1;
            if(reward.getSlot() == 2) slot = Configuration.GUI_SPECIAL_REWARDSLOT_2;

            while(inventory.getItem(slot) != null) {
                int rand = random.nextInt(rewardList.size());
                reward = rewardList.get(rand);
                if(reward.getSlot() == 1) slot = Configuration.GUI_SPECIAL_REWARDSLOT_1;
                if(reward.getSlot() == 2) slot = Configuration.GUI_SPECIAL_REWARDSLOT_2;
            }

            while(rewards.contains(reward)) {
                int rand = random.nextInt(rewardList.size());
                reward = rewardList.get(rand);
            }

            rewards.add(reward);

            int price = reward.getPrice();
            List<String> lore = new ArrayList<>();
            reward.getLore().forEach(string -> lore.add(string.replace("%price%", String.valueOf(price))));

            ItemStack rewardItem = new ItemBuilder(reward.getMaterial())
                    .setName(reward.getName())
                    .setAmount(reward.getAmount())
                    .setLore(lore)
                    .setDurability(reward.getDurability())
                    .toItemStack();
            inventory.setItem(slot, rewardItem);
        }
    }

    public Storable getStorage() {
        return storage;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }

    public RewardManager getRewardManager() {
        return rewardManager;
    }

    public ChanceManager getChanceManager() {
        return chanceManager;
    }

    public long getNormalTime() {
        return normalTime;
    }

    public void setNormalTime(long normalTime) {
        this.normalTime = normalTime;
    }

    public long getSpecialTime() {
        return specialTime;
    }

    public void setSpecialTime(long specialTime) {
        this.specialTime = specialTime;
    }

    public BukkitRunnable getNormalTimer() {
        return normalTimer;
    }

    public void setNormalTimer(BukkitRunnable normalTimer) {
        this.normalTimer = normalTimer;
    }

    public BukkitRunnable getSpecialTimer() {
        return specialTimer;
    }

    public void setSpecialTimer(BukkitRunnable specialTimer) {
        this.specialTimer = specialTimer;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }
}
