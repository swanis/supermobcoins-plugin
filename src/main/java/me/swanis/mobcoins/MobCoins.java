package me.swanis.mobcoins;

import me.swanis.mobcoins.chance.ChanceManager;
import me.swanis.mobcoins.command.MobCoinsCommand;
import me.swanis.mobcoins.command.subcommands.*;
import me.swanis.mobcoins.listeners.EntityListener;
import me.swanis.mobcoins.listeners.InventoryListener;
import me.swanis.mobcoins.listeners.MobCoinsListener;
import me.swanis.mobcoins.listeners.PlayerListener;
import me.swanis.mobcoins.placeholder.HolographicDisplaysHook;
import me.swanis.mobcoins.placeholder.PlaceholderAPIHook;
import me.swanis.mobcoins.profile.PlayerProfileList;
import me.swanis.mobcoins.profile.Profile;
import me.swanis.mobcoins.profile.ProfileManager;
import me.swanis.mobcoins.reward.RewardManager;
import me.swanis.mobcoins.storage.Storable;
import me.swanis.mobcoins.storage.impl.MySQLStorage;
import me.swanis.mobcoins.storage.impl.YamlStorage;
import me.swanis.mobcoins.utils.ItemBuilder;
import me.swanis.mobcoins.utils.MetricsLite;
import me.swanis.mobcoins.profile.PlayerProfile;
import me.swanis.mobcoins.utils.YamlFile;
import me.swanis.mobcoins.utils.command.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    private boolean wildStacker;
    private boolean forceDisable;

    private PlayerProfileList MobCoinsTop = new PlayerProfileList(100);
    private String formatDate;

    @Override
    public void onEnable() {
        loadConfiguration();

        if (loadStorage()) {
            loadDependencies();

            registerManagers();
            registerCommands();
            registerListeners();
            registerPlaceholders();

            getServer().getOnlinePlayers().stream().map(player -> player.getUniqueId()).forEach(storage::loadProfile);
            loadInventory();
            rewardManager.loadLastRewards();
            runTimer();
            runMobCoinsTopTimer();

            new MobCoinsAPI(this);
            new MetricsLite(this);
        } else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Failed to establish MySQL connection, disabling SuperMobCoins...");

            forceDisable = true;

            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        if (!forceDisable) {
            getServer().getOnlinePlayers().stream().map(player -> player.getUniqueId()).forEach(storage::saveProfile);
            rewardManager.saveLastRewards();
        }
    }

    private void loadConfiguration() {
        saveDefaultConfig();
        new Configuration(this);
    }

    private boolean loadStorage() {
        if (Configuration.MYSQL_ENABLED) {
            storage = new MySQLStorage(this);
        } else {
            storage = new YamlStorage(this);
        }

        return storage.init();
    }

    private void loadDependencies() {
        if(getServer().getPluginManager().getPlugin("WildStacker") != null) {
            wildStacker = true;
        }
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
        commandManager.register(new MobCoinsPayCommand(this));
        commandManager.register(new MobCoinsViewCoinsCommand(this));
        commandManager.register(new MobCoinsGiveCommand(this));
        commandManager.register(new MobCoinsTakeCommand(this));
        commandManager.register(new MobCoinsSetCommand(this));
        commandManager.register(new MobCoinsGiveItemCommand(this));
        commandManager.register(new MobCoinsRefreshCommand(this));
        commandManager.register(new MobCoinsAuthorCommand(this));
        commandManager.register(new MobCoinsReloadCommand(this));
        commandManager.register(new MobCoinsMigrateCommand(this));
        commandManager.register(new MobCoinsTopCommand(this));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
        getServer().getPluginManager().registerEvents(new MobCoinsListener(this), this);
    }

    private void registerPlaceholders() {
        if(getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderAPIHook(this).register();
        }

        if(getServer().getPluginManager().isPluginEnabled("HolographicDisplays")) {
            new HolographicDisplaysHook(this).hook();
        }
    }

    private void runTimer() {
        if(!loaded) {
            normalTime = System.currentTimeMillis() + (Configuration.MOBCOIN_NORMAL_SHOP_UPDATE_HOURS * (60 * 60)) * 1000;
            specialTime = System.currentTimeMillis() + (Configuration.MOBCOIN_SPECIAL_SHOP_UPDATE_HOURS * (60 * 60)) * 1000;
            rewardManager.refreshNormalRewards();
            rewardManager.refreshSpecialRewards();
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
        }.runTaskTimerAsynchronously(this, 0L, 20L);
    }

    private void runMobCoinsTopTimer() {
        // Delay in minutes
        long delay = Configuration.MOBCOIN_TOP_UPDATE_DELAY * 20 * 60;

        // load previous top 100
        YamlFile targetFile = new YamlFile("profiles", this);
        FileConfiguration targetConfig = targetFile.getConfig();
        ConfigurationSection configSection = targetConfig.getConfigurationSection("Profile");

        if (configSection != null) {
            configSection.getKeys(false).forEach(player -> {
                long playerMobcoins = targetConfig.getLong("Profile." + player + ".mobcoins");
                MobCoinsTop.add(new PlayerProfile(UUID.fromString(player), playerMobcoins));
            });
        }

        new BukkitRunnable() {
            public void run() {
                for (Profile profile : MobCoins.this.getProfileManager().getProfiles()) {
                    boolean exists = false;
                    for (PlayerProfile playerProfile : MobCoinsTop) {
                        if (profile.getUUID().equals(playerProfile.getUUID())) {
                            MobCoinsTop.updateprofile(playerProfile, profile.getMobCoins());
                            exists = true;
                        }
                    }
                    if (!exists) {
                        MobCoinsTop.add(new PlayerProfile(profile.getUUID(), profile.getMobCoins()));
                    }
                }

                // Re-sort the list (must be done after new entries has been added)
                MobCoinsTop.update();

                LocalDateTime time = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
                formatDate = time.format(formatter);
            }
        }.runTaskTimerAsynchronously(this, 0L, delay);
    }


    public void loadInventory() {
        inventory = getServer().createInventory(null, (Configuration.GUI_ROWS * 9), Configuration.GUI_TITLE);

        if(Configuration.GUI_FILLER_ENABLED) {
            ItemStack fillerItem = new ItemBuilder(Configuration.GUI_FILLER_ITEM_MATERIAL)
                    .setName(Configuration.GUI_FILLER_ITEM_NAME)
                    .setDurability(Configuration.GUI_FILLER_ITEM_DURABILITY)
                    .addGlow(Configuration.GUI_FILLER_ITEM_GLOW)
                    .toItemStack();

            for(int i = 0; i < inventory.getSize(); i++) {
                if(inventory.getItem(i) == null)
                    inventory.setItem(i, fillerItem);
            }
        }
    }

    public ItemStack getMobCoinItem() {
        return new ItemBuilder(Configuration.MOBCOIN_ITEM_MATERIAL).setName(Configuration.MOBCOIN_ITEM_NAME).setLore(Configuration.MOBCOIN_ITEM_LORE).toItemStack();
    }

    public Storable getStorage() {
        return storage;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public boolean hasWildStacker() {
        return wildStacker;
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

    public PlayerProfileList getMobCoinsTop() { return this.MobCoinsTop; }

    public String getDate() { return formatDate; }
}
