package me.swanis.mobcoins.command.subcommands;

import me.swanis.mobcoins.Configuration;
import me.swanis.mobcoins.MobCoins;
import me.swanis.mobcoins.profile.Profile;
import me.swanis.mobcoins.utils.FormatUtil;
import me.swanis.mobcoins.utils.TimeUtil;
import me.swanis.mobcoins.utils.command.Command;
import me.swanis.mobcoins.utils.command.PluginCommand;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class MobCoinsTopCommand extends PluginCommand {

    private MobCoins instance;

    public MobCoinsTopCommand(MobCoins instance) {
        super(instance);
        this.instance = instance;
    }

    @Command(command = "top", permission = "mobcoins.top", subCommand = true, baseCommand = "mobcoins")
    public void onCommand(CommandSender commandSender, String[] args) {
        int page = 1;

        if (args.length >= 2) {
            try {
                page = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                commandSender.sendMessage(Configuration.USAGE_MESSAGE.replace("%usage%", "/mobcoins top <page>"));
                return;
            }
        }

        ArrayList<Profile> topList = instance.getProfileManager().getTopList();

        int pageLimit = (int) Math.ceil((double) topList.size() / (double) Configuration.MOBCOINS_TOP_PLAYERS_PER_PAGE);
        if (page > pageLimit) page = pageLimit;
        if (page <= 0) page = 1;

        int lowerEnd = Configuration.MOBCOINS_TOP_PLAYERS_PER_PAGE * (page - 1);
        int upperEnd = lowerEnd + Configuration.MOBCOINS_TOP_PLAYERS_PER_PAGE;

        upperEnd = Math.min(upperEnd, topList.size());

        for (String line : Configuration.MOBCOINS_TOP_LORE) {
            if (!line.contains("%player%")) {
                commandSender.sendMessage(line
                        .replace("%page%", String.valueOf(page))
                        .replace("%topupdatetime%", TimeUtil.getFormattedString(instance.getTopUpdateTime() - System.currentTimeMillis())));
                continue;
            }

            for (int i = lowerEnd; i < upperEnd; i++) {
                Profile profile = topList.get(i);
                commandSender.sendMessage(line
                        .replace("%number%", String.valueOf(i + 1))
                        .replace("%player%", Bukkit.getOfflinePlayer(profile.getUUID()).getName())
                        .replace("%mobcoins%", String.valueOf(profile.getMobCoins()))
                        .replace("%mobcoins_formatted%", FormatUtil.format(profile.getMobCoins())));
            }
        }
    }
}
