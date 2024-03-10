package io.github.seriousguy888.akkenvyu.commands;

import io.github.seriousguy888.akkenvyu.AkKenVyu;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class CheckForUpdatesSubcommand extends Subcommand {
    private final AkKenVyu plugin;

    protected CheckForUpdatesSubcommand(AkKenVyu plugin) {
        super("check_for_updates", "akkenvyu.command.check_for_updates");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage("Polling the GitHub repo. Check the console for further logging.");
        plugin.getGithubFetcher().pollForUpdatesAsync();
    }

    @Nonnull
    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
