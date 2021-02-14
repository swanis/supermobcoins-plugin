package me.swanis.mobcoins.command.subcommands;

import me.swanis.mobcoins.Configuration;
import me.swanis.mobcoins.MobCoins;
import me.swanis.mobcoins.profile.Profile;
import me.swanis.mobcoins.utils.command.Command;
import me.swanis.mobcoins.utils.command.PluginCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MobCoinsViewCoinsCommand extends PluginCommand {

    private MobCoins instance;

    public MobCoinsViewCoinsCommand(MobCoins instance) {
        super(instance);
        this.instance = instance;
    }

    @Command(command = "viewcoins", permission = "mobcoins.viewcoins", subCommand = true, baseCommand = "mobcoins")
    public void onCommand(CommandSender commandSender, String[] args) {
        if(args.length < 2) {
            commandSender.sendMessage(Configuration.USAGE_MESSAGE.replace("%usage%", "/mobcoins viewcoins <player>"));
            return;
        }

        if(instance.getServer().getPlayer(args[1]) == null) {
            commandSender.sendMessage(Configuration.PLAYER_NOT_FOUND_MESSAGE.replace("%player%", args[1]));
            return;
        }

        Player player = instance.getServer().getPlayer(args[1]);
        Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());

        if(profile == null) {
            commandSender.sendMessage(Configuration.PROFILE_NOT_FOUND_MESSAGE.replace("%profile%", player.getUniqueId().toString()));
            return;
        }

        if(String.valueOf(profile.getMobCoins()).length() > 10) {
            commandSender.sendMessage(Configuration.AMOUNT_INPUT_TOO_LONG);
            return;
        }

        String amount = Configuration.FORMAT_ENABLED ? Configuration.FORMAT_NUMBER_FORMAT.format(profile.getMobCoins()) : String.valueOf(profile.getMobCoins());

        commandSender.sendMessage(Configuration.MOBCOINS_OF_PLAYER_MESSAGE.replace("%player%", player.getName()).replace("%amount%", amount));
    }
}
