package me.swanis.mobcoins.command.subcommands;

import me.swanis.mobcoins.MobCoins;
import me.swanis.mobcoins.utils.command.Command;
import me.swanis.mobcoins.utils.command.PluginCommand;
import org.bukkit.command.CommandSender;

import java.util.Collection;

public class MobCoinsTopCommand extends PluginCommand {

    private MobCoins instance;

    public MobCoinsTopCommand(MobCoins instance) {
        super(instance);
        this.instance = instance;

    }

    @Command(command = "top", permission = "mobcoins.top", subCommand = true, baseCommand = "mobcoins")
    public void onCommand(CommandSender commandSender, String[] args) {

    }
}
