package me.swanis.mobcoins.command.subcommands;

import me.swanis.mobcoins.MobCoins;
import me.swanis.mobcoins.storage.Storable;
import me.swanis.mobcoins.storage.impl.MySQLStorage;
import me.swanis.mobcoins.utils.YamlFile;
import me.swanis.mobcoins.utils.command.Command;
import me.swanis.mobcoins.utils.command.PluginCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class MobCoinsMigrateCommand extends PluginCommand {

    private MobCoins instance;

    public MobCoinsMigrateCommand(MobCoins instance) {
        super(instance);
        this.instance = instance;
    }

    @Command(command = "migrate", permission = "mobcoins.migrate", subCommand = true, baseCommand = "mobcoins")
    public void onCommand(CommandSender commandSender, String[] args) {
        commandSender.sendMessage("Migrating profiles.. (this can take a while, don't run the command again)");

        Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
            Storable tempStorage = new MySQLStorage(instance);

            if (!tempStorage.init()) {
                commandSender.sendMessage("Could not establish a MySQL connection");
                return;
            }

            YamlFile yamlFile = new YamlFile("profiles", instance);
            FileConfiguration profilesConfig = yamlFile.getConfig();

            Set<String> uuids = profilesConfig.getConfigurationSection("Profile").getKeys(false);
            Iterator<String> it = uuids.iterator();

            int amount = uuids.size();
            long before = System.currentTimeMillis();

            while (it.hasNext()) {
                String uuid = it.next();

                long mobCoins = profilesConfig.getLong("Profile." + uuid + ".mobcoins");

                if (mobCoins == 0) {
                    it.remove();
                    amount--;
                    continue;
                }

                tempStorage.set(UUID.fromString(uuid), mobCoins);
            }

            long after = System.currentTimeMillis();

            commandSender.sendMessage("Migrated " + amount + " profiles in " + (after - before) + "ms");
        });
    }
}
