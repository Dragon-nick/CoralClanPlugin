package me.dragon.coralClanPlugin.commands.interfaces;

import me.dragon.coralClanPlugin.CoralClanPlugin;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ISubCommand {
	void execute(@NotNull Player player, @NotNull String[] args, @NotNull CoralClanPlugin plugin);
}
