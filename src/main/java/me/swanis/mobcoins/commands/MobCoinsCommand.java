package me.swanis.mobcoins.commands;

import me.swanis.mobcoins.Configuration;
import me.swanis.mobcoins.MobCoins;
import me.swanis.mobcoins.profile.Profile;
import me.swanis.mobcoins.utils.ItemBuilder;
import me.swanis.mobcoins.utils.LocationUtil;
import me.swanis.mobcoins.utils.Sounds;
import me.swanis.mobcoins.utils.TimeUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class MobCoinsCommand implements CommandExecutor {

    private MobCoins instance;

    public MobCoinsCommand(MobCoins instance) {
        this.instance = instance;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(args.length == 0) {
            if(!(commandSender instanceof Player)) {
                commandSender.sendMessage(Configuration.NOT_PLAYER_MESSAGE);
                return true;
            }

            Player player = (Player) commandSender;
            Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());

            if(!player.hasPermission("mobcoins.use")) {
                player.sendMessage(Configuration.NO_PERMISSION_MESSAGE);
                return true;
            }

            if(profile == null) {
                player.sendMessage(Configuration.PROFILE_NOT_FOUND_MESSAGE.replace("%profile%", player.getUniqueId().toString()));
                return true;
            }

            Inventory inventory = instance.getInventory();

            List<String> amountLore = new ArrayList<>();
            Configuration.GUI_AMOUNT_ITEM_LORE.forEach(string -> amountLore.add(string.replace("%coins%", String.valueOf(profile.getMobCoins()))));
            String name = Configuration.GUI_AMOUNT_ITEM_NAME.replace("%coins%", String.valueOf(profile.getMobCoins()));

            ItemStack amountItem = new ItemBuilder(inventory.getItem(Configuration.GUI_AMOUNT_ITEM_SLOT)).setName(name).setLore(amountLore).toItemStack();
            inventory.setItem(Configuration.GUI_AMOUNT_ITEM_SLOT, amountItem);

            long normalTimeLeft = instance.getNormalTime() - System.currentTimeMillis();
            long specialTimeLeft = instance.getSpecialTime() - System.currentTimeMillis();

            List<String> normalMobCoinsLore = new ArrayList<>();
            Configuration.GUI_MOBCOINS_ITEM_LORE.forEach(string -> normalMobCoinsLore.add(string.replace("%time%", TimeUtil.getFormattedString(normalTimeLeft))));

            ItemStack normalMobCoinsItem = new ItemBuilder(inventory.getItem(Configuration.GUI_MOBCOINS_ITEM_SLOT)).setLore(normalMobCoinsLore).toItemStack();
            inventory.setItem(Configuration.GUI_MOBCOINS_ITEM_SLOT, normalMobCoinsItem);

            List<String> specialMobCoinsLore = new ArrayList<>();
            Configuration.GUI_SPECIAL_MOBCOINS_ITEM_LORE.forEach(string -> specialMobCoinsLore.add(string.replace("%time%", TimeUtil.getFormattedString(specialTimeLeft))));

            ItemStack specialMobCoinsItem = new ItemBuilder(inventory.getItem(Configuration.GUI_SPECIAL_MOBCOINS_ITEM_SLOT)).setLore(specialMobCoinsLore).toItemStack();
            inventory.setItem(Configuration.GUI_SPECIAL_MOBCOINS_ITEM_SLOT, specialMobCoinsItem);

            if(Configuration.GUI_OPEN_SOUND_ENABLED) {
                player.playSound(player.getLocation(), Configuration.GUI_OPEN_SOUND_TYPE, 10, 1);
            }

            player.openInventory(inventory);
            return true;
        }

        switch(args[0]) {
            case "give": {
                if(!commandSender.hasPermission("mobcoins.give")) {
                    commandSender.sendMessage(Configuration.NO_PERMISSION_MESSAGE);
                    return true;
                }

                if(args.length < 3) {
                    commandSender.sendMessage(Configuration.USAGE_MESSAGE.replace("%usage%", "/mobcoins give <player> <amount>"));
                    return true;
                }

                if(Bukkit.getPlayerExact(args[1]) == null) {
                    commandSender.sendMessage(Configuration.PLAYER_NOT_FOUND_MESSAGE.replace("%player%", args[1]));
                    return true;
                }

                Player target = Bukkit.getPlayerExact(args[1]);
                Profile profile = instance.getProfileManager().getProfile(target.getUniqueId());

                if(profile == null) {
                    commandSender.sendMessage(Configuration.PROFILE_NOT_FOUND_MESSAGE.replace("%profile%", target.getUniqueId().toString()));
                    return true;
                }

                if(!StringUtils.isNumeric(args[2])) {
                    commandSender.sendMessage(Configuration.NOT_NUMERIC_MESSAGE.replace("%arg%", args[2]));
                    return true;
                }

                int amount = Integer.valueOf(args[2]);

                profile.setMobCoins(profile.getMobCoins() + amount);
                commandSender.sendMessage(Configuration.GAVE_MOBCOINS_MESSAGE.replace("%amount%", String.valueOf(amount)).replace("%player%", target.getName()));
                target.sendMessage(Configuration.RECEIVED_MOBCOINS_MESSAGE.replace("%amount%", String.valueOf(amount)).replace("%sender%", commandSender.getName()));
                break;
            }

            case "take": {
                if(!commandSender.hasPermission("mobcoins.take")) {
                    commandSender.sendMessage(Configuration.NO_PERMISSION_MESSAGE);
                    return true;
                }

                if(args.length < 3) {
                    commandSender.sendMessage(Configuration.USAGE_MESSAGE.replace("%usage%", "/mobcoins take <player> <amount>"));
                    return true;
                }

                if(Bukkit.getPlayerExact(args[1]) == null) {
                    commandSender.sendMessage(Configuration.PLAYER_NOT_FOUND_MESSAGE.replace("%player%", args[1]));
                    return true;
                }

                Player target = Bukkit.getPlayerExact(args[1]);
                Profile profile = instance.getProfileManager().getProfile(target.getUniqueId());

                if(profile == null) {
                    commandSender.sendMessage(Configuration.PROFILE_NOT_FOUND_MESSAGE.replace("%profile%", target.getUniqueId().toString()));
                    return true;
                }

                if(!StringUtils.isNumeric(args[2])) {
                    commandSender.sendMessage(Configuration.NOT_NUMERIC_MESSAGE.replace("%arg%", args[2]));
                    return true;
                }

                int amount = Integer.valueOf(args[2]);

                if(profile.getMobCoins() < amount) {
                    commandSender.sendMessage(Configuration.PLAYER_NOT_ENOUGH_MOBCOINS.replace("%player%", target.getName()).replace("%amount%", String.valueOf(amount)));
                    return true;
                }

                profile.setMobCoins(profile.getMobCoins() - amount);
                commandSender.sendMessage(Configuration.TOOK_MOBCOINS_MESSAGE.replace("%amount%", String.valueOf(amount)).replace("%player%", target.getName()));
                target.sendMessage(Configuration.PLAYER_TOOK_MOBCOINS_MESSAGE.replace("%player%", commandSender.getName()).replace("%amount%", String.valueOf(amount)));
                break;
            }

            case "set": {
                if(!commandSender.hasPermission("mobcoins.set")) {
                    commandSender.sendMessage(Configuration.NO_PERMISSION_MESSAGE);
                    return true;
                }

                if(args.length < 3) {
                    commandSender.sendMessage(Configuration.USAGE_MESSAGE.replace("%usage%", "/mobcoins set <player> <amount>"));
                    return true;
                }

                if(Bukkit.getPlayerExact(args[1]) == null) {
                    commandSender.sendMessage(Configuration.PLAYER_NOT_FOUND_MESSAGE.replace("%player%", args[1]));
                    return true;
                }

                Player target = Bukkit.getPlayerExact(args[1]);
                Profile profile = instance.getProfileManager().getProfile(target.getUniqueId());

                if(profile == null) {
                    commandSender.sendMessage(Configuration.PROFILE_NOT_FOUND_MESSAGE.replace("%profile%", target.getUniqueId().toString()));
                    return true;
                }

                if(!StringUtils.isNumeric(args[2])) {
                    commandSender.sendMessage(Configuration.NOT_NUMERIC_MESSAGE.replace("%arg%", args[2]));
                    return true;
                }

                int amount = Integer.valueOf(args[2]);

                profile.setMobCoins(amount);
                commandSender.sendMessage(Configuration.SET_MOBCOINS_MESSAGE.replace("%amount%", String.valueOf(amount)).replace("%player%", target.getName()));
                target.sendMessage(Configuration.YOUR_MOBCOINS_SET_MESSAGE.replace("%amount%", String.valueOf(amount)).replace("%player%", commandSender.getName()));
                break;
            }

            case "giveitem": {
                if(!commandSender.hasPermission("mobcoins.giveitem")) {
                    commandSender.sendMessage(Configuration.NO_PERMISSION_MESSAGE);
                    return true;
                }

                if(args.length < 3) {
                    commandSender.sendMessage(Configuration.USAGE_MESSAGE.replace("%usage%", "/mobcoins give <player> <amount>"));
                    return true;
                }

                if(Bukkit.getPlayerExact(args[1]) == null) {
                    commandSender.sendMessage(Configuration.PLAYER_NOT_FOUND_MESSAGE.replace("%player%", args[1]));
                    return true;
                }

                Player target = Bukkit.getPlayerExact(args[1]);
                Profile profile = instance.getProfileManager().getProfile(target.getUniqueId());

                if(profile == null) {
                    commandSender.sendMessage(Configuration.PROFILE_NOT_FOUND_MESSAGE.replace("%profile%", target.getUniqueId().toString()));
                    return true;
                }

                if(!StringUtils.isNumeric(args[2])) {
                    commandSender.sendMessage(Configuration.NOT_NUMERIC_MESSAGE.replace("%arg%", args[2]));
                    return true;
                }

                int amount = Integer.valueOf(args[2]);

                ItemStack mobCoinItem = new ItemBuilder(Material.DOUBLE_PLANT)
                        .setName(Configuration.MOBCOIN_ITEM_NAME)
                        .setLore(Configuration.MOBCOIN_ITEM_LORE)
                        .setAmount(amount)
                        .toItemStack();

                target.getInventory().addItem(mobCoinItem);
                commandSender.sendMessage(Configuration.GAVE_MOBCOIN_ITEMS_MESSAGE.replace("%amount%", String.valueOf(amount)).replace("%player%", target.getName()));
                target.sendMessage(Configuration.RECEIVED_MOBCOIN_ITEMS_MESSAGE.replace("%amount%", String.valueOf(amount)).replace("%sender%", commandSender.getName()));
                break;
            }

            case "withdraw": {
                if(!(commandSender instanceof Player)) {
                    commandSender.sendMessage(Configuration.NOT_PLAYER_MESSAGE);
                    return true;
                }

                Player player = (Player) commandSender;

                if(!player.hasPermission("mobcoins.withdraw")) {
                    player.sendMessage(Configuration.NO_PERMISSION_MESSAGE);
                    return true;
                }

                if(args.length < 2) {
                    player.sendMessage(Configuration.USAGE_MESSAGE.replace("%usage%", "/mobcoins withdraw <amount>"));
                    return true;
                }

                if(!StringUtils.isNumeric(args[1])) {
                    player.sendMessage(Configuration.NOT_NUMERIC_MESSAGE.replace("%arg%", args[1]));
                    return true;
                }

                int amount = Integer.valueOf(args[1]);

                if(amount == 0) {
                    player.sendMessage(Configuration.AMOUNT_CANT_BE_ZERO_MESSAGE);
                    return true;
                }

                Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());

                if(profile == null) {
                    player.sendMessage(Configuration.PROFILE_NOT_FOUND_MESSAGE.replace("%profile%", player.getUniqueId().toString()));
                    return true;
                }

                if(profile.getMobCoins() < amount) {
                    player.sendMessage(Configuration.NOT_ENOUGH_MOBCOINS_MESSAGE);
                    return true;
                }

                if(player.getInventory().firstEmpty() == -1) {
                    player.sendMessage(Configuration.INVENTORY_FULL_MESSAGE);
                    return true;
                }

                int actualAmount = amount;
                boolean full = false;

                for (int i = 0; i < amount; i++) {
                    if(player.getInventory().firstEmpty() == -1) {
                        actualAmount = i;
                        full = true;
                        break;
                    }

                    ItemStack mobCoinItem = new ItemBuilder(Material.DOUBLE_PLANT)
                            .setName(Configuration.MOBCOIN_ITEM_NAME)
                            .setLore(Configuration.MOBCOIN_ITEM_LORE)
                            .toItemStack();

                    player.getInventory().addItem(mobCoinItem);
                }

                profile.setMobCoins(profile.getMobCoins() - actualAmount);
                if(!full) {
                    player.sendMessage(Configuration.WITHDREW_MOBCOINS_MESSAGE.replace("%amount%", String.valueOf(actualAmount)));
                } else {
                    player.sendMessage(Configuration.INVENTORY_GOT_FILLED_MESSAGE.replace("%amount%", String.valueOf(actualAmount)));
                }
                break;
            }

            case "viewcoins": {
                if(!commandSender.hasPermission("mobcoins.viewcoins")) {
                    commandSender.sendMessage(Configuration.NO_PERMISSION_MESSAGE);
                    return true;
                }

                if(args.length < 2) {
                    commandSender.sendMessage(Configuration.USAGE_MESSAGE.replace("%usage%", "/mobcoins viewcoins <player>"));
                    return true;
                }

                if(Bukkit.getPlayer(args[1]) == null) {
                    commandSender.sendMessage(Configuration.PLAYER_NOT_FOUND_MESSAGE.replace("%player%", args[1]));
                    return true;
                }

                Player player = Bukkit.getPlayer(args[1]);
                Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());

                if(profile == null) {
                    commandSender.sendMessage(Configuration.PROFILE_NOT_FOUND_MESSAGE.replace("%profile%", player.getUniqueId().toString()));
                    return true;
                }

                commandSender.sendMessage(Configuration.MOBCOINS_OF_PLAYER_MESSAGE.replace("%player%", player.getName()).replace("%amount%", String.valueOf(profile.getMobCoins())));
                break;
            }

            case "reload": {
                if(!commandSender.hasPermission("mobcoins.reload")) {
                    commandSender.sendMessage(Configuration.NO_PERMISSION_MESSAGE);
                    return true;
                }

                instance.reloadConfig();
                new Configuration(instance);
                instance.getRewardManager().reloadRewards();
                instance.getChanceManager().reloadChances();
                commandSender.sendMessage("The configuration has been reloaded");
                break;
            }

            case "author": {
                commandSender.sendMessage("This server is running MobCoins created by Swanis");
                break;
            }

            case "refresh": {
                if(!commandSender.hasPermission("mobcoins.refresh")) {
                    commandSender.sendMessage(Configuration.NO_PERMISSION_MESSAGE);
                    return true;
                }

                if(args.length < 2) {
                    commandSender.sendMessage("Usage: /mobcoins refresh <category>");
                    return true;
                }

                if(!args[1].equalsIgnoreCase("normal") && !args[1].equalsIgnoreCase("special")) {
                    commandSender.sendMessage("Category should be either: 'normal' or 'special'");
                    return true;
                }

                if(args[1].equalsIgnoreCase("normal")) {
                    instance.getNormalTimer().cancel();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            instance.setNormalTimer(this);

                            instance.updateNormalRewards();
                            instance.setNormalTime(System.currentTimeMillis() + (Configuration.MOBCOIN_NORMAL_SHOP_UPDATE_HOURS * (60 * 60)) * 1000);
                            Bukkit.broadcastMessage(Configuration.MOBCOIN_NORMAL_SHOP_UPDATED_MESSAGE);
                        }
                    }.runTaskTimer(instance, 0L,  (Configuration.MOBCOIN_NORMAL_SHOP_UPDATE_HOURS * (60 * 60)) * 20);

                    return true;
                }

                if(args[1].equalsIgnoreCase("special")) {
                    instance.getSpecialTimer().cancel();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            instance.setSpecialTimer(this);

                            instance.updateSpecialRewards();
                            instance.setSpecialTime(System.currentTimeMillis() + (Configuration.MOBCOIN_SPECIAL_SHOP_UPDATE_HOURS * (60 * 60)) * 1000);
                            Bukkit.broadcastMessage(Configuration.MOBCOIN_SPECIAL_SHOP_UPDATED_MESSAGE);
                        }
                    }.runTaskTimer(instance, 0L, (Configuration.MOBCOIN_SPECIAL_SHOP_UPDATE_HOURS * (60 * 60)) * 20);

                    return true;
                }

                break;
            }

            default: {
                Configuration.MOBCOINS_HELP_LORE.forEach(commandSender::sendMessage);
                break;
            }
        }
        return false;
    }
}
