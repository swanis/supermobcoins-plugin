package me.swanis.mobcoins.command.subcommands;

import me.swanis.mobcoins.Configuration;
import me.swanis.mobcoins.MobCoins;
import me.swanis.mobcoins.profile.Profile;
import me.swanis.mobcoins.utils.ItemBuilder;
import me.swanis.mobcoins.utils.command.Command;
import me.swanis.mobcoins.utils.command.PluginCommand;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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

        if(Bukkit.getPlayer(args[1]) == null) {
            commandSender.sendMessage(Configuration.PLAYER_NOT_FOUND_MESSAGE.replace("%player%", args[1]));
            return;
        }

        Player player = Bukkit.getPlayer(args[1]);
        Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());

        if(profile == null) {
            commandSender.sendMessage(Configuration.PROFILE_NOT_FOUND_MESSAGE.replace("%profile%", player.getUniqueId().toString()));
            return;
        }

        commandSender.sendMessage(Configuration.MOBCOINS_OF_PLAYER_MESSAGE.replace("%player%", player.getName()).replace("%amount%", String.valueOf(profile.getMobCoins())));
    }
}
