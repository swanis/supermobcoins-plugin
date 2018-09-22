package me.swanis.mobcoins.utils.command;


public class PluginCommand {

    public @interface Command {
        String command();
        CommandType commandType();
    }

    public enum CommandType {
        COMMAND, SUBCOMMAND
    }
}
