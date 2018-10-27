package me.swanis.mobcoins;

import me.swanis.mobcoins.chance.ChanceManager;
import me.swanis.mobcoins.command.MobCoinsCommand;
import me.swanis.mobcoins.command.subcommands.*;
import me.swanis.mobcoins.listeners.EntityListener;
import me.swanis.mobcoins.listeners.InventoryListener;
import me.swanis.mobcoins.listeners.MobCoinsListener;
import me.swanis.mobcoins.listeners.PlayerListener;
import me.swanis.mobcoins.placeholder.HolographicDisplaysHook;
import me.swanis.mobcoins.placeholder.MVdWPlaceholderAPIHook;
import me.swanis.mobcoins.placeholder.PlaceholderAPIHook;
import me.swanis.mobcoins.profile.ProfileManager;
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

public class MobCoins extends JavaPlugin {

    /*
    The code in this plugin was written by Swanis (https://www.mc-market.org/members/71127/) and therefore he owns all rights to it and the plugin.
    */

    private Storable storage;
    private Inventory inventory;

    private ProfileManager profileManager;
    private RewardManager rewardManager;
    private ChanceManager chanceManager;
    private CommandManager commandManager;

    private long normalTime;
    private long specialTime;
    private boolean loaded;

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
        rewardManager.loadLastRewards();
        runTimer();

        new MobCoinsAPI(this);
    }

    @Override
    public void onDisable() {
        getServer().getOnlinePlayers().forEach(storage::saveProfile);
        rewardManager.saveLastRewards();
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
        commandManager.register(new MobCoinsCommand(this));

        //Subcommands
        commandManager.register(new MobCoinsWithdrawCommand(this));
        commandManager.register(new MobCoinsViewCoinsCommand(this));
        commandManager.register(new MobCoinsGiveCommand(this));
        commandManager.register(new MobCoinsTakeCommand(this));
        commandManager.register(new MobCoinsSetCommand(this));
        commandManager.register(new MobCoinsGiveItemCommand(this));
        commandManager.register(new MobCoinsRefreshCommand(this));
        commandManager.register(new MobCoinsAuthorCommand(this));
        commandManager.register(new MobCoinsReloadCommand(this));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
        getServer().getPluginManager().registerEvents(new MobCoinsListener(), this);
    }

    private void registerPlaceholders() {
        if(getServer().getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")) {
            new MVdWPlaceholderAPIHook(this).hook();
        }

        if(getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderAPIHook(this).hook();
        }

        if(getServer().getPluginManager().isPluginEnabled("HolographicDisplays")) {
            new HolographicDisplaysHook(this).hook();
        }
    }

    private void runTimer() {
        if(!loaded) {
            normalTime = System.currentTimeMillis() + (Configuration.MOBCOIN_NORMAL_SHOP_UPDATE_HOURS * (60 * 60)) * 1000;
            specialTime = System.currentTimeMillis() + (Configuration.MOBCOIN_SPECIAL_SHOP_UPDATE_HOURS * (60 * 60)) * 1000;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if(normalTime < System.currentTimeMillis()) {
                    rewardManager.refreshNormalRewards();
                    normalTime = System.currentTimeMillis() + (Configuration.MOBCOIN_NORMAL_SHOP_UPDATE_HOURS * (60 * 60)) * 1000;
                    getServer().broadcastMessage(Configuration.MOBCOIN_NORMAL_SHOP_UPDATED_MESSAGE);
                }

                if(specialTime < System.currentTimeMillis()) {
                    rewardManager.refreshSpecialRewards();
                    specialTime = System.currentTimeMillis() + (Configuration.MOBCOIN_SPECIAL_SHOP_UPDATE_HOURS * (60 * 60)) * 1000;
                    getServer().broadcastMessage(Configuration.MOBCOIN_SPECIAL_SHOP_UPDATED_MESSAGE);
                }
            }
        }.runTaskTimer(this, 0L, 20L);
    }

    public void loadInventory() {
        inventory = Bukkit.createInventory(null, (Configuration.GUI_ROWS * 9), Configuration.GUI_TITLE);

        rewardManager.refreshNormalRewards();
        rewardManager.refreshSpecialRewards();

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

    public CommandManager getCommandManager() {
        return commandManager;
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

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }
}
