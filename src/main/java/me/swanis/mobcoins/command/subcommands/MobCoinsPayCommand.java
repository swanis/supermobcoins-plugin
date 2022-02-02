package me.swanis.mobcoins.command.subcommands;

import me.swanis.mobcoins.Configuration;
import me.swanis.mobcoins.MobCoins;
import me.swanis.mobcoins.profile.Profile;
import me.swanis.mobcoins.utils.command.Command;
import me.swanis.mobcoins.utils.command.PluginCommand;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MobCoinsPayCommand extends PluginCommand {

    private MobCoins instance;

    public MobCoinsPayCommand(MobCoins instance) {
        super(instance);
        this.instance = instance;
    }

    @Command(command = "pay", permission = "mobcoins.pay", subCommand = true, baseCommand = "mobcoins")
    public void onCommand(CommandSender commandSender, String[] args) {
        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage(Configuration.NOT_PLAYER_MESSAGE);
            return;
        }

        Player player = (Player) commandSender;

        if(args.length < 3) {
            player.sendMessage(Configuration.USAGE_MESSAGE.replace("%usage%", "/mobcoins pay <player> <amount>"));
            return;
        }

        if (player.getName().equalsIgnoreCase(args[1])) {
            player.sendMessage(Configuration.CANNOT_PAY_YOURSELF_MESSAGE);
            return;
        }

        if(instance.getServer().getPlayerExact(args[1]) == null) {
            player.sendMessage(Configuration.PLAYER_NOT_FOUND_MESSAGE.replace("%player%", args[1]));
            return;
        }

        Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());

        if(profile == null) {
            commandSender.sendMessage(Configuration.PROFILE_NOT_FOUND_MESSAGE.replace("%profile%", player.getUniqueId().toString()));
            return;
        }

        Player target = instance.getServer().getPlayerExact(args[1]);
        Profile targetProfile = instance.getProfileManager().getProfile(target.getUniqueId());

        if(targetProfile == null) {
            player.sendMessage(Configuration.PROFILE_NOT_FOUND_MESSAGE.replace("%profile%", target.getUniqueId().toString()));
            return;
        }

        if(!StringUtils.isNumeric(args[2])) {
            player.sendMessage(Configuration.NOT_NUMERIC_MESSAGE.replace("%arg%", args[2]));
            return;
        }

        if(args[2].length() > 9) {
            player.sendMessage(Configuration.AMOUNT_INPUT_TOO_LONG);
            return;
        }

        long amount = Long.valueOf(args[2]);

        if (profile.getMobCoins() < amount) {
            player.sendMessage(Configuration.NOT_ENOUGH_MOBCOINS_MESSAGE);
            return;
        }

        profile.setMobCoins(profile.getMobCoins() - amount);
        targetProfile.setMobCoins(targetProfile.getMobCoins() + amount);

        String amountString = Configuration.FORMAT_ENABLED ? Configuration.FORMAT_NUMBER_FORMAT.format(amount) : String.valueOf(amount);

        player.sendMessage(Configuration.GAVE_MOBCOINS_MESSAGE.replace("%amount%", amountString).replace("%player%", target.getName()));
        target.sendMessage(Configuration.RECEIVED_MOBCOINS_MESSAGE.replace("%amount%", amountString).replace("%sender%", player.getName()));
    }
}
