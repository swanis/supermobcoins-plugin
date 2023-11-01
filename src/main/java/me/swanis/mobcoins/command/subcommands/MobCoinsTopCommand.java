package me.swanis.mobcoins.command.subcommands;

import me.swanis.mobcoins.Configuration;
import me.swanis.mobcoins.MobCoins;
import me.swanis.mobcoins.profile.PlayerProfile;
import me.swanis.mobcoins.profile.PlayerProfileList;
import me.swanis.mobcoins.utils.command.Command;
import me.swanis.mobcoins.utils.command.PluginCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Objects;


public class MobCoinsTopCommand extends PluginCommand {

    private MobCoins instance;

    public MobCoinsTopCommand(MobCoins instance) {
        super(instance);
        this.instance = instance;

    }

    @Command(command = "top", permission = "mobcoins.top", subCommand = true, baseCommand = "mobcoins")
    public void onCommand(CommandSender commandSender, String[] args) {
        if (args.length > 2) {
            commandSender.sendMessage(Configuration.USAGE_MESSAGE.replace("%usage%", "/mobcoins top <page>"));
            return;
        }

        int page = 1;
        if (args.length == 2) {
            try {
                page = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                commandSender.sendMessage(Configuration.USAGE_MESSAGE.replace("%usage%", "/mobcoins top <page>"));
                return;
            }
        }

        PlayerProfileList MobCoinsTop = instance.getMobCoinsTop();


        int page_limit = (int) Math.ceil(MobCoinsTop.size() / Configuration.MOBCOINS_TOP_PER_PAGE);
        if (page > page_limit) page = page_limit;
        if (page <= 0) page = 1;

        int lower_end = (Configuration.MOBCOINS_TOP_PER_PAGE * page) - Configuration.MOBCOINS_TOP_PER_PAGE;
        int upper_end = lower_end + Configuration.MOBCOINS_TOP_PER_PAGE;

        if (upper_end > MobCoinsTop.size()) {
            upper_end = MobCoinsTop.size();
        }

        for (String s : Configuration.MOBCOINS_TOP_MESSAGE ) {
            if (s.contains("%players%")) {
                for (int i = lower_end; i < upper_end; i++) {

                    PlayerProfile profile = MobCoinsTop.get(i);
                    commandSender.sendMessage(s
                            .replace("%number%", String.valueOf(i + 1))
                            .replace("%players%", Objects.requireNonNull(Bukkit.getPlayer(profile.getUUID())).getName())
                            .replace("%mobcoins%", String.valueOf(profile.getTokens())));
                }
            } else {
                commandSender.sendMessage(s
                        .replace("%date%", instance.getDate())
                        .replace("%pages%", String.valueOf(page)));
            }

        }
    }
}
