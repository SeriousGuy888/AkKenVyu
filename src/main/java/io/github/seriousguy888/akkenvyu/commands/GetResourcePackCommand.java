package io.github.seriousguy888.akkenvyu.commands;

import io.github.seriousguy888.akkenvyu.AkKenVyu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class GetResourcePackCommand implements CommandExecutor {
    private final AkKenVyu plugin;

    public GetResourcePackCommand(AkKenVyu plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender sender,
                             @Nonnull Command command,
                             @Nonnull String label,
                             @Nonnull String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        String url = plugin.getConfig().getString("resource_pack.url");
        if (url == null) {
            sender.sendMessage("The resource pack URL is missing from the config.yml.");
            return false;
        }

        player.setResourcePack(url, null, "Resource pack from " + url, false);

        return false;
    }
}
