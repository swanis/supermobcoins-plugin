package me.swanis.mobcoins.command.subcommands;

import me.swanis.mobcoins.Configuration;
import me.swanis.mobcoins.MobCoins;
import me.swanis.mobcoins.utils.PlayerProfile;
import me.swanis.mobcoins.utils.command.Command;
import me.swanis.mobcoins.utils.command.PluginCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;


public class MobCoinsTopCommand extends PluginCommand {

    private MobCoins instance;

    public MobCoinsTopCommand(MobCoins instance) {
        super(instance);
        this.instance = instance;

    }

    @Command(command = "top", permission = "mobcoins.top", subCommand = true, baseCommand = "mobcoins")
    public void onCommand(CommandSender commandSender, String[] args) {
        for (String s : Configuration.MOBCOINS_TOP_MESSAGE ) {
            if (s.contains("%players%")) {
                int i = 0;
                for (PlayerProfile profile : instance.getMobCoinsTop()) {
                    i++;
                    commandSender.sendMessage(s
                            .replace("%number%", String.valueOf(i))
                            .replace("%players%", Bukkit.getPlayer(profile.getUUID()).getName())
                            .replace("%mobcoins%", String.valueOf(profile.getTokens())));
                }
            } else {
                commandSender.sendMessage(s
                        .replace("%date%", instance.getDate())
                        .replace("%pages%", String.valueOf(1)));
            }

        }
    }
}
