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
import org.bukkit.scheduler.BukkitRunnable;

public class MobCoinsRefreshCommand extends PluginCommand {

    private MobCoins instance;

    public MobCoinsRefreshCommand(MobCoins instance) {
        super(instance);
        this.instance = instance;
    }

    @Command(command = "refresh", permission = "mobcoins.refresh", subCommand = true, baseCommand = "mobcoins")
    public void onCommand(CommandSender commandSender, String[] args) {
        if(args.length < 2) {
            commandSender.sendMessage(Configuration.USAGE_MESSAGE.replace("%usage%", "/mobcoins refresh <category>"));
            return;
        }

        if(!args[1].equalsIgnoreCase("normal") && !args[1].equalsIgnoreCase("special")) {
            commandSender.sendMessage("Category should be either: 'normal' or 'special'");
            return;
        }

        if(args[1].equalsIgnoreCase("normal")) {
            instance.getNormalTimer().cancel();
            new BukkitRunnable() {
                @Override
                public void run() {
                    instance.setNormalTimer(this);

                    instance.updateNormalRewards();
                    instance.setNormalTime(System.currentTimeMillis() + (Configuration.MOBCOIN_NORMAL_SHOP_UPDATE_HOURS * (60 * 60)) * 1000);
                    Bukkit.broadcastMessage(Configuration.MOBCOIN_NORMAL_SHOP_UPDATED_MESSAGE);
                }
            }.runTaskTimer(instance, 0L,  (Configuration.MOBCOIN_NORMAL_SHOP_UPDATE_HOURS * (60 * 60)) * 20);

            return;
        }

        if(args[1].equalsIgnoreCase("special")) {
            instance.getSpecialTimer().cancel();
            new BukkitRunnable() {
                @Override
                public void run() {
                    instance.setSpecialTimer(this);

                    instance.updateSpecialRewards();
                    instance.setSpecialTime(System.currentTimeMillis() + (Configuration.MOBCOIN_SPECIAL_SHOP_UPDATE_HOURS * (60 * 60)) * 1000);
                    Bukkit.broadcastMessage(Configuration.MOBCOIN_SPECIAL_SHOP_UPDATED_MESSAGE);
                }
            }.runTaskTimer(instance, 0L, (Configuration.MOBCOIN_SPECIAL_SHOP_UPDATE_HOURS * (60 * 60)) * 20);

            return;
        }
    }
}
