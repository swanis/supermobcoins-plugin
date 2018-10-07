package me.swanis.mobcoins.utils.command;

import me.swanis.mobcoins.MobCoins;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class CommandManager {

    private MobCoins instance;

    private Map<String, PluginCommand> commands = new HashMap();

    public CommandManager(MobCoins instance) {
        this.instance = instance;
    }

    public void register(PluginCommand pluginCommand) {
        Class<?> clazz = pluginCommand.getClass();
        Command command = null;

        for(Method method : clazz.getMethods()) {
            if(method.getAnnotation(Command.class) != null) {
                command = method.getAnnotation(Command.class);
            }
        }

        if(command == null) return;

        pluginCommand.setCommand(command.command());
        pluginCommand.setPermission(command.permission());
        pluginCommand.setSubCommand(command.subCommand());
        pluginCommand.setSubCommands(command.subCommands());

        if(!command.subCommand()) {
            commands.put(command.command().toLowerCase(), pluginCommand);
            instance.getCommand(command.command()).setExecutor(pluginCommand);
        } else {
            commands.put(command.baseCommand().toLowerCase() + "." + command.command().toLowerCase(), pluginCommand);
        }
    }

    public PluginCommand getCommand(String string) {
        return commands.get(string.toLowerCase());
    }
}
