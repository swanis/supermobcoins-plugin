package me.swanis.mobcoins.utils.command;

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

        if(!command.subCommand()) {
            commands.put(command.command().toLowerCase(), pluginCommand);
            instance.getCommand(command.command()).setExecutor(pluginCommand);
            pluginCommand.setSubcommands(command.subCommands());
        } else {
            commands.put(command.baseCommand().toLowerCase() + "." + command.command().toLowerCase(), pluginCommand);

            for (int i = 0; i < command.aliases().length; i++) {
                commands.put(command.baseCommand().toLowerCase() + "." + command.aliases()[i].toLowerCase(), pluginCommand);
            }
        }
    }

    public PluginCommand getCommand(String string) {
        return commands.get(string.toLowerCase());
    }
}
