package io.github.seriousguy888.akkenvyu.commands;

import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public abstract class Subcommand {
    @Nonnull
    private final String name;
    @Nullable
    private final String permission;

    protected Subcommand(@Nonnull String name, @Nullable String permission) {
        this.name = name;
        this.permission = permission;
    }

    public abstract void execute(CommandSender sender, String[] args);

    @Nonnull
    public abstract List<String> tabComplete(CommandSender sender, String[] args);

    public boolean senderHasPermission(CommandSender sender) {
        if (permission == null) {
            return true;
        }

        return sender.hasPermission(permission);
    }

    @Nonnull
    public String getName() {
        return name;
    }
}
