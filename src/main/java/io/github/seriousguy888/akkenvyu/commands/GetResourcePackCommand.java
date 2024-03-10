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
            sender.sendMessage("Only players may use this command.");
            return true;
        }

        String dlUrl = plugin.getGithubFetcher().getDownloadUrl();

        if (dlUrl == null) {
            player.sendMessage("The resource pack download URL is currently unavailable for some reason.");
            return false;
        }

        byte[] hash = plugin.getGithubFetcher().getSha1Hash();
        player.setResourcePack(dlUrl, hash, "Applying latest resource pack...", false);

        return false;
    }
}
