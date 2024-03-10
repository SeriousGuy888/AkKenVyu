package io.github.seriousguy888.akkenvyu.commands;

import io.github.seriousguy888.akkenvyu.AkKenVyu;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MainCommand implements TabExecutor {
    private final AkKenVyu plugin;
    private final List<Subcommand> subcommands;

    public MainCommand(AkKenVyu plugin) {
        this.plugin = plugin;

        subcommands = new ArrayList<>();
        subcommands.add(new EnableSubcommand(plugin));
        subcommands.add(new DisableSubcommand(plugin));
        subcommands.add(new CheckForUpdatesSubcommand(plugin));
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender sender,
                             @Nonnull Command command,
                             @Nonnull String label,
                             @Nonnull String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Subcommands: "
                    + subcommands.stream().map(Subcommand::getName).collect(Collectors.joining(", ")));
            return true;
        }

        String subcommandName = args[0];
        Subcommand subcommand = getSubcommand(subcommandName);

        if (subcommand == null) {
            sender.sendMessage(ChatColor.RED + "No matching subcommand.");
            return true;
        }

        if (!subcommand.senderHasPermission(sender)) {
            sender.sendMessage(ChatColor.RED + "Insufficient permission.");
            return true;
        }

        subcommand.execute(sender, removeFirstArg(args));
        return false;
    }


    @Nonnull
    @Override
    public List<String> onTabComplete(@Nonnull CommandSender sender,
                                      @Nonnull Command command,
                                      @Nonnull String label,
                                      @Nonnull String[] args) {

        if (args.length == 1) { // If autocompleting the first argument...
            return subcommands.stream()
                    .filter(subcommand -> subcommand.senderHasPermission(sender)
                            && subcommand.getName().toLowerCase().startsWith(args[0].toLowerCase()))
                    .map(Subcommand::getName).toList();
        } else if (args.length > 1) { // If autocompleting args inside a subcommand...

            Subcommand subcommand = getSubcommand(args[0]);
            if (subcommand == null) {
                return List.of();
            }

            // Have subcommand handle the tabcompletion from here.
            return subcommand.tabComplete(sender, removeFirstArg(args));
        }

        return List.of();
    }

    @Nullable
    private Subcommand getSubcommand(String name) {
        return subcommands.stream()
                .filter(subcommand -> subcommand.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    private String[] removeFirstArg(String[] args) {
        return Arrays.copyOfRange(args, 1, args.length);
    }
}
