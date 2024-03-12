package io.github.seriousguy888.akkenvyu.commands.customitem;

import io.github.seriousguy888.akkenvyu.AkKenVyu;
import io.github.seriousguy888.akkenvyu.commands.Subcommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CustomItemCommand implements TabExecutor {
    private final AkKenVyu plugin;
    private final List<Subcommand> subcommands;

    public CustomItemCommand(AkKenVyu plugin) {
        this.plugin = plugin;

        subcommands = new ArrayList<>();

    }

    @Override
    public boolean onCommand(@Nonnull CommandSender sender,
                             @Nonnull Command command,
                             @Nonnull String label,
                             @Nonnull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("save or load");
            return false;
        }

        if (args[0].equalsIgnoreCase("save")) {

            ItemStack heldItem = player.getInventory().getItem(player.getInventory().getHeldItemSlot());

            if (heldItem == null) {
                player.sendMessage("Please hold an item that you want to save.");
                return false;
            }

            if (args.length < 2) {
                player.sendMessage("please spcify a name for this item");
                return false;
            }

            ItemStack savedItem = plugin.getSavedItemsConfig().saveItem(heldItem, args[1]);

            if (savedItem == null) {
                player.sendMessage("failed to save item");
            } else {
                player.sendMessage("saved item");
                player.getInventory().setItem(player.getInventory().getHeldItemSlot(), savedItem.clone());
            }

        } else if (args[0].equalsIgnoreCase("load")) {
            if (args.length < 2) {
                player.sendMessage("speicfy name of the item you want to load");
                return false;
            }

            ItemStack loadedItem = plugin.getSavedItemsConfig().getItem(args[1]);
            if (loadedItem == null) {
                player.sendMessage("No such item exists");
                return false;
            }

            player.getInventory().addItem(loadedItem);
            player.sendMessage("here you go");

        } else if (args[0].equalsIgnoreCase("delete")) {
            if (args.length < 2) {
                player.sendMessage("speicfy id of the item you want to delete");
                return false;
            }

            boolean success = plugin.getSavedItemsConfig().deleteItem(args[1]);

            if (success) {
                player.sendMessage("deleted item");
            } else {
                player.sendMessage("failed to delete item");
            }

            return false;
        } else {
            player.sendMessage("save or load or delete");
        }


        return false;
    }

    @Override
    public List<String> onTabComplete(@Nonnull CommandSender sender,
                                      @Nonnull Command command,
                                      @Nonnull String label,
                                      @Nonnull String[] args) {
        if (args.length == 1) {
            return List.of("save", "load", "delete");
        }

        if (args.length == 2 &&
                (args[0].equalsIgnoreCase("load") || args[0].equalsIgnoreCase("delete"))) {
            return plugin.getSavedItemsConfig().getSavedItemIds();
        }

        return List.of();
    }
}
