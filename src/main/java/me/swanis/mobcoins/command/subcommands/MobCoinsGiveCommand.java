package me.swanis.mobcoins.command.subcommands;

import me.swanis.mobcoins.Configuration;
import me.swanis.mobcoins.MobCoins;
import me.swanis.mobcoins.profile.Profile;
import me.swanis.mobcoins.utils.command.Command;
import me.swanis.mobcoins.utils.command.PluginCommand;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MobCoinsGiveCommand extends PluginCommand {

    private MobCoins instance;

    public MobCoinsGiveCommand(MobCoins instance) {
        super(instance);
        this.instance = instance;
    }

    @Command(command = "give", permission = "mobcoins.give", subCommand = true, baseCommand = "mobcoins")
    public void onCommand(CommandSender commandSender, String[] args) {
        if(args.length < 3) {
            commandSender.sendMessage(Configuration.USAGE_MESSAGE.replace("%usage%", "/mobcoins give <player> <amount>"));
            return;
        }

        if(instance.getServer().getPlayerExact(args[1]) == null) {
            commandSender.sendMessage(Configuration.PLAYER_NOT_FOUND_MESSAGE.replace("%player%", args[1]));
            return;
        }

        Player target = instance.getServer().getPlayerExact(args[1]);
        Profile profile = instance.getProfileManager().getProfile(target.getUniqueId());

        if(profile == null) {
            commandSender.sendMessage(Configuration.PROFILE_NOT_FOUND_MESSAGE.replace("%profile%", target.getUniqueId().toString()));
            return;
        }

        if(!StringUtils.isNumeric(args[2])) {
            commandSender.sendMessage(Configuration.NOT_NUMERIC_MESSAGE.replace("%arg%", args[2]));
            return;
        }

        if(args[2].length() > 10) {
            commandSender.sendMessage(Configuration.AMOUNT_INPUT_TOO_LONG);
            return;
        }

        int amount = Integer.valueOf(args[2]);

        profile.setMobCoins(profile.getMobCoins() + amount);

        String amountString = Configuration.FORMAT_ENABLED ? Configuration.FORMAT_NUMBER_FORMAT.format(amount) : String.valueOf(amount);

        commandSender.sendMessage(Configuration.GAVE_MOBCOINS_MESSAGE.replace("%amount%", amountString).replace("%player%", target.getName()));

        String string = String.join(" ", args);

        if(!string.endsWith("-s")) {
            target.sendMessage(Configuration.RECEIVED_MOBCOINS_MESSAGE.replace("%amount%", amountString).replace("%sender%", commandSender.getName()));
        }
    }
}
