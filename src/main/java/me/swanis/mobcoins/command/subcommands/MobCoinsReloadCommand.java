package me.swanis.mobcoins.command.subcommands;

import me.swanis.mobcoins.Configuration;
import me.swanis.mobcoins.MobCoins;
import me.swanis.mobcoins.profile.Profile;
import me.swanis.mobcoins.utils.command.Command;
import me.swanis.mobcoins.utils.command.PluginCommand;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class MobCoinsReloadCommand extends PluginCommand {

    private MobCoins instance;

    public MobCoinsReloadCommand(MobCoins instance) {
        super(instance);
        this.instance = instance;
    }

    @Command(command = "reload", permission = "mobcoins.reload", subCommand = true, baseCommand = "mobcoins")
    public void onCommand(CommandSender commandSender, String[] args) {
        instance.reloadConfig();
        new Configuration(instance);
        instance.loadInventory();
        instance.getRewardManager().reloadRewards();
        instance.getChanceManager().reloadChances();
        instance.setNormalTime(System.currentTimeMillis());
        instance.setSpecialTime(System.currentTimeMillis());
        commandSender.sendMessage("The configuration has been reloaded");
    }
}
