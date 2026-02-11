package me.dragon.coralClanPlugin.commands.clan;

import me.dragon.coralClanPlugin.commands.clan.subcommands.*;
import me.dragon.coralClanPlugin.commands.interfaces.ISubCommand;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class ClanCommand implements TabExecutor {
	private final Map<String, ISubCommand> subCommands = Map.ofEntries(
		Map.entry("create", new ClanCreateCommand()),
		Map.entry("disband", new ClanDisbandCommand()),
		Map.entry("invite", new ClanInviteCommand()),
		Map.entry("kick", new ClanKickCommand()),
		Map.entry("promote", new ClanPromoteCommand()),
		Map.entry("demote", new ClanDemoteCommand()),
		Map.entry("chat", new ClanChatCommand()),
		Map.entry("claim", new ClanClaimCommand()),
		Map.entry("unclaim", new ClanUnclaimCommand()),
		Map.entry("home", new ClanHomeCommand()),
		Map.entry("sethome", new ClanSetHomeCommand()),
		Map.entry("info", new ClanInfoCommand())
	);

	@Override
	public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command,
		@NotNull final String label, @NotNull final String[] args) {
		if (sender instanceof final Player player && args.length != 0 && this.subCommands.containsKey(args[0])) {
			this.subCommands
				.get(args[0])
				.execute(player, args);

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
					sender.sendMessage("/clan create <nome> <tag> - Crea nuovo clan");
					break;
				case "invite":
					sender.sendMessage("/clan invite <player> - Invita giocatore");
					break;
				case "kick":
					sender.sendMessage("/clan kick <player> - Espelli membro");
					break;
				case "promote", "demote":
					sender.sendMessage("/clan promote/demote <player> - Gestisci ruoli");
					break;
				case "chat":
					sender.sendMessage("/clan chat <messaggio> - Chat clan");
					break;
				case "info":
					sender.sendMessage("/clan info [clan] - Info clan");
			}
		}

		return null;
	}
}
