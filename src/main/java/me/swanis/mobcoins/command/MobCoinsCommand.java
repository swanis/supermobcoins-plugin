package me.swanis.mobcoins.command;

import me.swanis.mobcoins.Configuration;
import me.swanis.mobcoins.MobCoins;
import me.swanis.mobcoins.profile.Profile;
import me.swanis.mobcoins.utils.ItemBuilder;
import me.swanis.mobcoins.utils.TimeUtil;
import me.swanis.mobcoins.utils.command.Command;
import me.swanis.mobcoins.utils.command.PluginCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MobCoinsCommand extends PluginCommand {

    private MobCoins instance;

    public MobCoinsCommand(MobCoins instance) {
        super(instance);
        this.instance = instance;
    }

    @Command(command = "mobcoins", permission = "mobcoins.use", subCommands = {"withdraw", "viewcoins", "give", "take", "set", "giveitem", "refresh", "author", "reload"})
    public void onCommand(CommandSender commandSender, String[] args) {
        if(args.length < 1) {
            if(!(commandSender instanceof Player)) {
                commandSender.sendMessage(Configuration.NOT_PLAYER_MESSAGE);
                return;
            }

            Player player = (Player) commandSender;
            Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());

            if(profile == null) {
                player.sendMessage(Configuration.PROFILE_NOT_FOUND_MESSAGE.replace("%profile%", player.getUniqueId().toString()));
                return;
            }


            Inventory inventory = clone(instance.getInventory());

            List<String> amountLore = new ArrayList<>();
            Configuration.GUI_AMOUNT_ITEM_LORE.forEach(string -> amountLore.add(string.replace("%coins%", String.valueOf(profile.getMobCoins()))));
            String name = Configuration.GUI_AMOUNT_ITEM_NAME.replace("%coins%", String.valueOf(profile.getMobCoins()));

            ItemStack amountItem = new ItemBuilder(inventory.getItem(Configuration.GUI_AMOUNT_ITEM_SLOT)).setName(name).setLore(amountLore).toItemStack();
            inventory.setItem(Configuration.GUI_AMOUNT_ITEM_SLOT, amountItem);

            long normalTimeLeft = instance.getNormalTime() - System.currentTimeMillis();
            long specialTimeLeft = instance.getSpecialTime() - System.currentTimeMillis();

            List<String> normalMobCoinsLore = new ArrayList<>();
            Configuration.GUI_MOBCOINS_ITEM_LORE.forEach(string -> normalMobCoinsLore.add(string.replace("%time%", TimeUtil.getFormattedString(normalTimeLeft))));

            ItemStack normalMobCoinsItem = new ItemBuilder(inventory.getItem(Configuration.GUI_MOBCOINS_ITEM_SLOT)).setLore(normalMobCoinsLore).toItemStack();
            inventory.setItem(Configuration.GUI_MOBCOINS_ITEM_SLOT, normalMobCoinsItem);

            List<String> specialMobCoinsLore = new ArrayList<>();
            Configuration.GUI_SPECIAL_MOBCOINS_ITEM_LORE.forEach(string -> specialMobCoinsLore.add(string.replace("%time%", TimeUtil.getFormattedString(specialTimeLeft))));

            ItemStack specialMobCoinsItem = new ItemBuilder(inventory.getItem(Configuration.GUI_SPECIAL_MOBCOINS_ITEM_SLOT)).setLore(specialMobCoinsLore).toItemStack();
            inventory.setItem(Configuration.GUI_SPECIAL_MOBCOINS_ITEM_SLOT, specialMobCoinsItem);

            if(Configuration.GUI_OPEN_SOUND_ENABLED) {
                player.playSound(player.getLocation(), Configuration.GUI_OPEN_SOUND_TYPE, 10, 1);
            }

            player.openInventory(inventory);
            return;
        }

        if(commandSender.hasPermission("mobcoins.admin")) {
            Configuration.MOBCOINS_HELP_ADMIN_LORE.forEach(commandSender::sendMessage);
            return;
        }

        Configuration.MOBCOINS_HELP_LORE.forEach(commandSender::sendMessage);
    }

    private Inventory clone(Inventory inventory) {
        Inventory clone = instance.getServer().createInventory(null, inventory.getSize(), inventory.getTitle());
        clone.setContents(inventory.getContents());
        return clone;
    }
}
