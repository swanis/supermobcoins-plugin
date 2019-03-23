package me.swanis.mobcoins.command.subcommands;

import me.swanis.mobcoins.Configuration;
import me.swanis.mobcoins.MobCoins;
import me.swanis.mobcoins.profile.Profile;
import me.swanis.mobcoins.utils.ItemBuilder;
import me.swanis.mobcoins.utils.command.Command;
import me.swanis.mobcoins.utils.command.PluginCommand;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MobCoinsWithdrawCommand extends PluginCommand {

    private MobCoins instance;

    public MobCoinsWithdrawCommand(MobCoins instance) {
        super(instance);
        this.instance = instance;
    }

    @Command(command = "withdraw", permission = "mobcoins.withdraw", subCommand = true, baseCommand = "mobcoins")
    public void onCommand(CommandSender commandSender, String[] args) {
        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage(Configuration.NOT_PLAYER_MESSAGE);
            return;
        }

        Player player = (Player) commandSender;

        if(args.length < 2) {
            player.sendMessage(Configuration.USAGE_MESSAGE.replace("%usage%", "/mobcoins withdraw <amount>"));
            return;
        }

        if(!StringUtils.isNumeric(args[1])) {
            player.sendMessage(Configuration.NOT_NUMERIC_MESSAGE.replace("%arg%", args[1]));
            return;
        }

        int amount = Integer.valueOf(args[1]);

        if(amount == 0) {
            player.sendMessage(Configuration.AMOUNT_CANT_BE_ZERO_MESSAGE);
            return;
        }

        Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());

        if(profile == null) {
            player.sendMessage(Configuration.PROFILE_NOT_FOUND_MESSAGE.replace("%profile%", player.getUniqueId().toString()));
            return;
        }

        if(profile.getMobCoins() < amount) {
            player.sendMessage(Configuration.NOT_ENOUGH_MOBCOINS_MESSAGE);
            return;
        }

        if(player.getInventory().firstEmpty() == -1) {
            player.sendMessage(Configuration.INVENTORY_FULL_MESSAGE);
            return;
        }

        int actualAmount = amount;
        boolean full = false;

        for (int i = 0; i < amount; i++) {
            if(player.getInventory().firstEmpty() == -1) {
                actualAmount = i;
                full = true;
                break;
            }

            ItemStack mobCoinItem = new ItemBuilder(Configuration.MOBCOIN_ITEM_MATERIAL)
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
    }
}
