package me.swanis.mobcoins;

import me.swanis.mobcoins.utils.ItemBuilder;
import me.swanis.mobcoins.utils.Sounds;
import me.swanis.mobcoins.utils.StringUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import java.text.NumberFormat;
import java.util.*;

public class Configuration {

    public static boolean MYSQL_ENABLED;
    public static String MYSQL_HOST;
    public static int MYSQL_PORT;
    public static String MYSQL_DATABASE;
    public static String MYSQL_USER;
    public static String MYSQL_PASSWORD;
    public static String GUI_TITLE;
    public static int GUI_ROWS;
    public static Map<Integer, ItemStack> GUI_DECORATION_ITEMS = new HashMap();
    public static boolean GUI_FILLER_ENABLED;
    public static Material GUI_FILLER_ITEM_MATERIAL;
    public static String GUI_FILLER_ITEM_NAME;
    public static short GUI_FILLER_ITEM_DURABILITY;
    public static boolean GUI_FILLER_ITEM_GLOW;
    public static Material MOBCOIN_ITEM_MATERIAL;
    public static String MOBCOIN_ITEM_NAME;
    public static List<String> MOBCOIN_ITEM_LORE = new ArrayList<>();
    public static short MOBCOIN_ITEM_DURABILITY;
    public static boolean FORMAT_ENABLED;
    public static NumberFormat FORMAT_NUMBER_FORMAT;
    public static int MOBCOIN_NORMAL_SHOP_UPDATE_HOURS;
    public static int MOBCOIN_SPECIAL_SHOP_UPDATE_HOURS;
    public static boolean GUI_OPEN_SOUND_ENABLED;
    public static Sound GUI_OPEN_SOUND_TYPE;
    public static boolean MOBCOINS_ONLY_FROM_NATURALLY_SPAWNED_MOBS;
    public static boolean RECEIVED_MOBCOIN_FROM_MOB_MESSAGE_SENT;
    public static boolean CLOSE_GUI_ON_BUY;
    public static boolean STACKING_SUPPORT;
    public static List<String> DISABLED_WORLDS = new ArrayList<>();
    public static long MOBCOIN_TOP_UPDATE_DELAY;

    public static String NO_PERMISSION_MESSAGE;
    public static String USAGE_MESSAGE;
    public static String PLAYER_NOT_FOUND_MESSAGE;
    public static String PROFILE_NOT_FOUND_MESSAGE;
    public static String NOT_NUMERIC_MESSAGE;
    public static String GAVE_MOBCOINS_MESSAGE;
    public static String RECEIVED_MOBCOINS_MESSAGE;
    public static String GAVE_MOBCOIN_ITEMS_MESSAGE;
    public static String RECEIVED_MOBCOIN_ITEMS_MESSAGE;
    public static String NOT_PLAYER_MESSAGE;
    public static String AMOUNT_CANT_BE_ZERO_MESSAGE;
    public static String NOT_ENOUGH_MOBCOINS_MESSAGE;
    public static String WITHDREW_MOBCOINS_MESSAGE;
    public static String BOUGHT_REWARD_MESSAGE;
    public static String RECEIVED_MOBCOIN_FROM_MOB_MESSAGE;
    public static String REDEEMED_MOBCOIN_MESSAGE;
    public static String MOBCOIN_NORMAL_SHOP_UPDATED_MESSAGE;
    public static String MOBCOIN_SPECIAL_SHOP_UPDATED_MESSAGE;
    public static String MOBCOINS_OF_PLAYER_MESSAGE;
    public static String PLAYER_NOT_ENOUGH_MOBCOINS;
    public static String TOOK_MOBCOINS_MESSAGE;
    public static String PLAYER_TOOK_MOBCOINS_MESSAGE;
    public static String SET_MOBCOINS_MESSAGE;
    public static String YOUR_MOBCOINS_SET_MESSAGE;
    public static String INVENTORY_FULL_MESSAGE;
    public static String INVENTORY_GOT_FILLED_MESSAGE;
    public static String AMOUNT_INPUT_TOO_LONG;
    public static String CANNOT_PAY_YOURSELF_MESSAGE;
    public static List<String> MOBCOINS_HELP_LORE = new ArrayList<>();
    public static List<String> MOBCOINS_HELP_ADMIN_LORE = new ArrayList<>();
    public static List<String> MOBCOINS_TOP_MESSAGE = new ArrayList<>();

    public Configuration(MobCoins instance) {
        MYSQL_ENABLED = instance.getConfig().getBoolean("mysql.enabled");
        MYSQL_HOST = instance.getConfig().getString("mysql.host");
        MYSQL_PORT = instance.getConfig().getInt("mysql.port");
        MYSQL_DATABASE = instance.getConfig().getString("mysql.database");
        MYSQL_USER = instance.getConfig().getString("mysql.user");
        MYSQL_PASSWORD = instance.getConfig().getString("mysql.password");

        GUI_TITLE = StringUtil.color(instance.getConfig().getString("gui.title"));
        GUI_ROWS =  instance.getConfig().getInt("gui.rows");

        GUI_DECORATION_ITEMS.clear();
        instance.getConfig().getConfigurationSection("gui.decoration").getKeys(false).forEach(string -> {
            String prefix = "gui.decoration." + string;

            Material material = null;

            try {
                material = Material.valueOf(instance.getConfig().getString(prefix + ".material"));
            } catch (IllegalArgumentException e) {
                try {
                    if (instance.getConfig().getString(prefix + ".material").equals("STAINED_GLASS_PANE")) {
                        material = Material.valueOf("GRAY_STAINED_GLASS_PANE");
                    } else {
                        material = Material.valueOf("LEGACY_" + instance.getConfig().getString(prefix + ".material"));
                    }
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                }
            }

            String name = StringUtil.color(instance.getConfig().getString(prefix + ".name"));
            List<String> lore = new ArrayList<>();
            instance.getConfig().getStringList(prefix + ".lore").forEach(string1 -> lore.add(StringUtil.color(string1)));
            short durability = (short) instance.getConfig().getInt(prefix + ".durability");
            boolean glow = instance.getConfig().getBoolean(prefix + ".glow");
            int slot = instance.getConfig().getInt(prefix + ".slot");

            ItemStack itemStack = new ItemBuilder(material).setName(name).setLore(lore).setDurability(durability).addGlow(glow).toItemStack();

            GUI_DECORATION_ITEMS.put(slot, itemStack);
        });

        GUI_FILLER_ENABLED = instance.getConfig().getBoolean("gui.filler.enabled");

        try {
            GUI_FILLER_ITEM_MATERIAL = Material.valueOf(instance.getConfig().getString("gui.filler.item.material"));
        } catch (IllegalArgumentException e) {
            try {
                if (instance.getConfig().getString("gui.filler.item.material").equals("STAINED_GLASS_PANE")) {
                    GUI_FILLER_ITEM_MATERIAL = Material.valueOf("GRAY_STAINED_GLASS_PANE");
                } else {
                    GUI_FILLER_ITEM_MATERIAL = Material.valueOf("LEGACY_" + instance.getConfig().getString("gui.filler.item.material"));
                }
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            }
        }

        GUI_FILLER_ITEM_NAME = StringUtil.color(instance.getConfig().getString("gui.filler.item.name"));
        GUI_FILLER_ITEM_DURABILITY = (short) instance.getConfig().getInt("gui.filler.item.durability");
        GUI_FILLER_ITEM_GLOW = instance.getConfig().getBoolean("gui.filler.item.glow");
        GUI_OPEN_SOUND_ENABLED = instance.getConfig().getBoolean("gui_open.sound.enabled");

        try {
            GUI_OPEN_SOUND_TYPE = Sounds.valueOf(instance.getConfig().getString("gui_open.sound.type")).bukkitSound();
        } catch (IllegalArgumentException e) {
            GUI_OPEN_SOUND_TYPE = Sound.valueOf(instance.getConfig().getString("gui_open.sound.type"));
        }

        try {
            MOBCOIN_ITEM_MATERIAL = Material.valueOf(instance.getConfig().getString("mobcoin_item.material"));
        } catch (IllegalArgumentException e) {
            try {
                MOBCOIN_ITEM_MATERIAL = Material.valueOf("LEGACY_" + instance.getConfig().getString("mobcoin_item.material"));
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            }
        }

        MOBCOIN_ITEM_NAME = StringUtil.color(instance.getConfig().getString("mobcoin_item.name"));
        Configuration.MOBCOIN_ITEM_LORE.clear();
        instance.getConfig().getStringList("mobcoin_item.lore").forEach(string -> Configuration.MOBCOIN_ITEM_LORE.add(StringUtil.color(string)));
        MOBCOIN_ITEM_DURABILITY = (short) instance.getConfig().getInt("mobcoin_item.durability");
        FORMAT_ENABLED = instance.getConfig().getBoolean("format.enabled");
        FORMAT_NUMBER_FORMAT = NumberFormat.getNumberInstance(Locale.forLanguageTag(instance.getConfig().getString("format.locale")));
        MOBCOIN_NORMAL_SHOP_UPDATE_HOURS = instance.getConfig().getInt("mobcoin_normal_shop_update_hours");
        MOBCOIN_SPECIAL_SHOP_UPDATE_HOURS = instance.getConfig().getInt("mobcoin_special_shop_update_hours");
        MOBCOINS_ONLY_FROM_NATURALLY_SPAWNED_MOBS = instance.getConfig().getBoolean("mobcoins_only_from_naturally_spawned_mods");
        RECEIVED_MOBCOIN_FROM_MOB_MESSAGE_SENT = instance.getConfig().getBoolean("received_mobcoin_from_mob_message_sent");
        CLOSE_GUI_ON_BUY = instance.getConfig().getBoolean("close_gui_on_buy");
        STACKING_SUPPORT = instance.getConfig().getBoolean("stacking_support");
        instance.getConfig().getStringList("disabled_worlds").forEach(DISABLED_WORLDS::add);
        MOBCOIN_TOP_UPDATE_DELAY = instance.getConfig().getInt("mobcoin_top_update_delay");

        NO_PERMISSION_MESSAGE = StringUtil.color(instance.getConfig().getString("NO_PERMISSION_MESSAGE"));
        USAGE_MESSAGE = StringUtil.color(instance.getConfig().getString("USAGE_MESSAGE"));
        PLAYER_NOT_FOUND_MESSAGE = StringUtil.color(instance.getConfig().getString("PLAYER_NOT_FOUND_MESSAGE"));
        PROFILE_NOT_FOUND_MESSAGE = StringUtil.color(instance.getConfig().getString("PROFILE_NOT_FOUND_MESSAGE"));
        NOT_NUMERIC_MESSAGE = StringUtil.color(instance.getConfig().getString("NOT_NUMERIC_MESSAGE"));
        GAVE_MOBCOINS_MESSAGE = StringUtil.color(instance.getConfig().getString("GAVE_MOBCOINS_MESSAGE"));
        RECEIVED_MOBCOINS_MESSAGE = StringUtil.color(instance.getConfig().getString("RECEIVED_MOBCOINS_MESSAGE"));
        GAVE_MOBCOIN_ITEMS_MESSAGE = StringUtil.color(instance.getConfig().getString("GAVE_MOBCOIN_ITEMS_MESSAGE"));
        RECEIVED_MOBCOIN_ITEMS_MESSAGE = StringUtil.color(instance.getConfig().getString("RECEIVED_MOBCOIN_ITEMS_MESSAGE"));
        NOT_PLAYER_MESSAGE = StringUtil.color(instance.getConfig().getString("NOT_PLAYER_MESSAGE"));
        AMOUNT_CANT_BE_ZERO_MESSAGE = StringUtil.color(instance.getConfig().getString("AMOUNT_CANT_BE_ZERO_MESSAGE"));
        NOT_ENOUGH_MOBCOINS_MESSAGE = StringUtil.color(instance.getConfig().getString("NOT_ENOUGH_MOBCOINS_MESSAGE"));
        WITHDREW_MOBCOINS_MESSAGE = StringUtil.color(instance.getConfig().getString("WITHDREW_MOBCOINS_MESSAGE"));
        BOUGHT_REWARD_MESSAGE = StringUtil.color(instance.getConfig().getString("BOUGHT_REWARD_MESSAGE"));
        RECEIVED_MOBCOIN_FROM_MOB_MESSAGE = StringUtil.color(instance.getConfig().getString("RECEIVED_MOBCOIN_FROM_MOB_MESSAGE"));
        REDEEMED_MOBCOIN_MESSAGE = StringUtil.color(instance.getConfig().getString("REDEEMED_MOBCOIN_MESSAGE"));
        MOBCOIN_NORMAL_SHOP_UPDATED_MESSAGE = StringUtil.color(instance.getConfig().getString("MOBCOIN_NORMAL_SHOP_UPDATED_MESSAGE"));
        MOBCOIN_SPECIAL_SHOP_UPDATED_MESSAGE = StringUtil.color(instance.getConfig().getString("MOBCOIN_SPECIAL_SHOP_UPDATED_MESSAGE"));
        MOBCOINS_OF_PLAYER_MESSAGE = StringUtil.color(instance.getConfig().getString("MOBCOINS_OF_PLAYER_MESSAGE"));
        PLAYER_NOT_ENOUGH_MOBCOINS = StringUtil.color(instance.getConfig().getString("PLAYER_NOT_ENOUGH_MOBCOINS"));
        TOOK_MOBCOINS_MESSAGE = StringUtil.color(instance.getConfig().getString("TOOK_MOBCOINS_MESSAGE"));
        PLAYER_TOOK_MOBCOINS_MESSAGE = StringUtil.color(instance.getConfig().getString("PLAYER_TOOK_MOBCOINS_MESSAGE"));
        SET_MOBCOINS_MESSAGE = StringUtil.color(instance.getConfig().getString("SET_MOBCOINS_MESSAGE"));
        YOUR_MOBCOINS_SET_MESSAGE = StringUtil.color(instance.getConfig().getString("YOUR_MOBCOINS_SET_MESSAGE"));
        INVENTORY_FULL_MESSAGE = StringUtil.color(instance.getConfig().getString("INVENTORY_FULL_MESSAGE"));
        INVENTORY_GOT_FILLED_MESSAGE = StringUtil.color(instance.getConfig().getString("INVENTORY_GOT_FILLED_MESSAGE"));
        AMOUNT_INPUT_TOO_LONG = StringUtil.color(instance.getConfig().getString("AMOUNT_INPUT_TOO_LONG"));
        CANNOT_PAY_YOURSELF_MESSAGE = StringUtil.color(instance.getConfig().getString("CANNOT_PAY_YOURSELF_MESSAGE"));
        MOBCOINS_HELP_LORE.clear();
        instance.getConfig().getStringList("MOBCOINS_HELP_LORE").forEach(string -> Configuration.MOBCOINS_HELP_LORE.add(StringUtil.color(string)));
        MOBCOINS_HELP_ADMIN_LORE.clear();
        instance.getConfig().getStringList("MOBCOINS_HELP_ADMIN_LORE").forEach(string -> Configuration.MOBCOINS_HELP_ADMIN_LORE.add(StringUtil.color(string)));
        MOBCOINS_TOP_MESSAGE.clear();
        instance.getConfig().getStringList("MOBCOINS_TOP_MESSAGE").forEach(s -> Configuration.MOBCOINS_TOP_MESSAGE.add(StringUtil.color(s)));
    }

    private Material getMaterial(String materialName) {
        Material material = null;

        try {
            material = Material.valueOf(materialName);
        } catch (IllegalArgumentException e) {
            try {
                material = Material.valueOf("LEGACY_" + materialName);
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            }
        }

        return material;
    }
}
