package io.github.seriousguy888.akkenvyu.commands;

import io.github.seriousguy888.akkenvyu.AkKenVyu;
import io.github.seriousguy888.akkenvyu.utils.ResourcePackSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;

public class EnableSubcommand extends Subcommand {
    private final AkKenVyu plugin;

    public EnableSubcommand(AkKenVyu plugin) {
        super("enable", null);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players may use this command.");
            return;
        }

        ResourcePackSender.sendResourcePack(plugin, player);
    }

    @Nonnull
    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
