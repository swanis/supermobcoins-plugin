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

public class MobCoinsSetCommand extends PluginCommand {

    private MobCoins instance;

    public MobCoinsSetCommand(MobCoins instance) {
        super(instance);
        this.instance = instance;
    }

    @Command(command = "set", permission = "mobcoins.set", subCommand = true, baseCommand = "mobcoins")
    public void onCommand(CommandSender commandSender, String[] args) {
        if(args.length < 3) {
            commandSender.sendMessage(Configuration.USAGE_MESSAGE.replace("%usage%", "/mobcoins set <player> <amount>"));
            return;
        }

        if(Bukkit.getPlayerExact(args[1]) == null) {
            commandSender.sendMessage(Configuration.PLAYER_NOT_FOUND_MESSAGE.replace("%player%", args[1]));
            return;
        }

        Player target = Bukkit.getPlayerExact(args[1]);
        Profile profile = instance.getProfileManager().getProfile(target.getUniqueId());

        if(profile == null) {
            commandSender.sendMessage(Configuration.PROFILE_NOT_FOUND_MESSAGE.replace("%profile%", target.getUniqueId().toString()));
            return;
        }

        if(!StringUtils.isNumeric(args[2])) {
            commandSender.sendMessage(Configuration.NOT_NUMERIC_MESSAGE.replace("%arg%", args[2]));
            return;
        }

        int amount = Integer.valueOf(args[2]);

        profile.setMobCoins(amount);
        commandSender.sendMessage(Configuration.SET_MOBCOINS_MESSAGE.replace("%amount%", String.valueOf(amount)).replace("%player%", target.getName()));
        target.sendMessage(Configuration.YOUR_MOBCOINS_SET_MESSAGE.replace("%amount%", String.valueOf(amount)).replace("%player%", commandSender.getName()));
    }
}
