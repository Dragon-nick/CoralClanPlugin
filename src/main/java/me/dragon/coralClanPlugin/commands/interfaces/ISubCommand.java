package me.dragon.coralClanPlugin.commands.interfaces;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ISubCommand {
	void execute(@NotNull Player player, @NotNull String[] args);
}
