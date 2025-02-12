package io.github.seriousguy888.akkenvyu.commands;

import io.github.seriousguy888.akkenvyu.AkKenVyu;
import io.github.seriousguy888.akkenvyu.utils.ResourcePackSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;

public class UnloadSubcommand extends Subcommand {
    private final AkKenVyu plugin;

    protected UnloadSubcommand(AkKenVyu plugin) {
        super("unload", null);

        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return;
        }

        ResourcePackSender.unloadResourcePack(plugin, player);
        plugin.getPlayerDataManager().getPlayerData(player).setResourcePackEnabled(false);
    }

    @Nonnull
    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
