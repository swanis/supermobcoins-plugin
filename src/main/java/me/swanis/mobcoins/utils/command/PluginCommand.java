package me.swanis.mobcoins.utils.command;


import me.swanis.mobcoins.Configuration;
import me.swanis.mobcoins.MobCoins;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerListHeaderFooter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public abstract class PluginCommand implements CommandExecutor {

    private MobCoins instance;

    private String command;
    private String permission;

    public PluginCommand(MobCoins instance) {
        this.instance = instance;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command cmd, String string, String[] args) {
        if(args.length > 0) {
            PluginCommand pluginCommand = instance.getCommandManager().getCommand(command + "." + args[0].toLowerCase());

            if(pluginCommand != null) {
                if(!pluginCommand.permission.equals("") && !commandSender.hasPermission(pluginCommand.permission)) {
                    commandSender.sendMessage(Configuration.NO_PERMISSION_MESSAGE);
                    return true;
                }

                pluginCommand.onCommand(commandSender, args);
                return true;
            }
        }

        if(!permission.equals("") && !commandSender.hasPermission(permission)) {
            commandSender.sendMessage(Configuration.NO_PERMISSION_MESSAGE);
            return true;
        }

        onCommand(commandSender, args);
        return false;
    }

    public abstract void onCommand(CommandSender commandSender, String[] args);

    public void setCommand(String command) {
        this.command = command;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }
}
