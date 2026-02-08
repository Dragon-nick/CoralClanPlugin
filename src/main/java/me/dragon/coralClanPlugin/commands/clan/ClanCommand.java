package me.dragon.coralClanPlugin.commands.clan;

import me.dragon.coralClanPlugin.CoralClanPlugin;
import me.dragon.coralClanPlugin.commands.clan.subcommands.ClanCreateCommand;
import me.dragon.coralClanPlugin.commands.interfaces.ISubCommand;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClanCommand implements TabExecutor {
	private final Map<String, ISubCommand> subCommands = new HashMap<>(Collections.emptyMap());
	private CoralClanPlugin plugin = null;

	public ClanCommand(@NotNull final CoralClanPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command,
		@NotNull final String label, @NotNull final String[] args) {
		this.subCommands.put("create", new ClanCreateCommand());

		if (sender instanceof final Player player && args.length != 0 && this.subCommands.containsKey(args[0])) {
			this.subCommands
				.get(args[0])
				.execute(player, args, this.plugin);

			return true;
		}

		return false;
	}


	@Override
	public @Nullable List<String> onTabComplete(@NotNull final CommandSender sender, @NotNull final Command command,
		@NotNull final String label, @NotNull final String[] args) {
		if (args.length == 1) {
			return List.of(
				"create",
				"disband",
				"invite",
				"kick",
				"promote",
				"demote",
				"chat",
				"claim",
				"unclaim",
				"home",
				"sethome",
				"info"
			);
		}

		if (args.length == 2 && StringUtils.isEmpty(args[1])) {
			switch (args[0]) {
				case "create":
					sender.sendMessage("/clan create <nome> <tag>");
			}
		}

		return null;
	}
}
