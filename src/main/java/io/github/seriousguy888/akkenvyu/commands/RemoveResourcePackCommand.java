package io.github.seriousguy888.akkenvyu.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class RemoveResourcePackCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@Nonnull CommandSender sender,
                             @Nonnull Command command,
                             @Nonnull String label,
                             @Nonnull String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        // the removeResourcePacks() method doesn't exist in 1.20.2
        player.setResourcePack("");

        return false;
    }
}
