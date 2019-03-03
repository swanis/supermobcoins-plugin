package me.swanis.mobcoins.utils.command;

import com.google.common.collect.Lists;
import me.swanis.mobcoins.MobCoins;

import java.lang.reflect.Method;
import java.util.Arrays;
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
        Method method = Arrays.stream(clazz.getMethods()).filter(m -> m.getAnnotation(Command.class) != null).findFirst().orElse(null);

        if(method == null) return;

        Command command = method.getAnnotation(Command.class);

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
