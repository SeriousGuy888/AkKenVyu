package io.github.seriousguy888.akkenvyu.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class DisableSubcommand extends Subcommand {
    protected DisableSubcommand() {
        super("disable", null);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return;
        }

        // the removeResourcePacks() method doesn't exist in 1.20.2
        player.setResourcePack("");
    }

    @Nonnull
    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
