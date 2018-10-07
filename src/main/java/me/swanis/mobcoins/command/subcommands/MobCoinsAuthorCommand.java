package me.swanis.mobcoins.command.subcommands;

import me.swanis.mobcoins.MobCoins;
import me.swanis.mobcoins.utils.command.Command;
import me.swanis.mobcoins.utils.command.PluginCommand;
import org.bukkit.command.CommandSender;

public class MobCoinsAuthorCommand extends PluginCommand {

    private MobCoins instance;

    public MobCoinsAuthorCommand(MobCoins instance) {
        super(instance);
        this.instance = instance;
    }

    @Command(command = "author", subCommand = true, baseCommand = "mobcoins")
    public void onCommand(CommandSender commandSender, String[] args) {
        commandSender.sendMessage("This server is running MobCoins created by Swanis");
    }
}
