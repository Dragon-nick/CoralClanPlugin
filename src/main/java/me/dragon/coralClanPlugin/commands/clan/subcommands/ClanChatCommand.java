package me.dragon.coralClanPlugin.commands.clan.subcommands;

import me.dragon.coralClanPlugin.CoralClanPlugin;
import me.dragon.coralClanPlugin.commands.interfaces.ISubCommand;
import me.dragon.coralClanPlugin.database.data.beans.ClanMemberBean;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ClanChatCommand implements ISubCommand {
	@Override
	public void execute(@NotNull final Player player, @NotNull final String[] args) {
		final Map<UUID, ClanMemberBean> clanMembers = CoralClanPlugin
			.getInstance()
			.getClanMember();

		if (clanMembers.get(player.getUniqueId()) == null) {
			player.sendMessage("Non fai parte di nessun clan!");
			return;
		}

		if (args.length == 1) {
			player.sendMessage("Il messaggio non può essere vuoto!");
			return;
		}

		final ClanMemberBean bean = clanMembers.get(player.getUniqueId());

		final String message = new StringBuilder(StringUtils.EMPTY)
			.append("[")
			.append(bean
				.getClanBean()
				.getTag())
			.append("]")
			.append(StringUtils.SPACE)
			.append("<")
			.append(player.getName())
			.append(">")
			.append(StringUtils.SPACE)
			.append(args[1])
			.toString();

		clanMembers
			.entrySet()
			.stream()
			.filter(entry -> entry
				.getValue()
				.getClanBean()
				.getId()
				.equals(bean
					.getClanBean()
					.getId()))
			.forEach(entry -> Objects
				.requireNonNull(Bukkit.getPlayer(entry.getKey()))
				.sendMessage(message));
	}
}
