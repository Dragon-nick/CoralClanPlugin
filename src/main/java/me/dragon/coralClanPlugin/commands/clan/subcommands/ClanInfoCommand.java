package me.dragon.coralClanPlugin.commands.clan.subcommands;

import me.clip.placeholderapi.PlaceholderAPI;
import me.dragon.coralClanPlugin.CoralClanPlugin;
import me.dragon.coralClanPlugin.commands.interfaces.ISubCommand;
import me.dragon.coralClanPlugin.database.data.beans.ClanBean;
import me.dragon.coralClanPlugin.database.data.dao.ClansDao;
import me.dragon.coralClanPlugin.utils.AsyncUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ClanInfoCommand implements ISubCommand {
	@Override
	public void execute(@NotNull final Player player, @NotNull final String[] args) {
		if (args.length == 1) {
			final String message = new StringBuilder(StringUtils.EMPTY)
				.append("[")
				.append("%clans_player_tag%")
				.append("]")
				.append(StringUtils.SPACE)
				.append("%clans_player_clan%")
				.append(StringUtils.SPACE)
				.append("|")
				.append(StringUtils.SPACE)
				.append("%clans_player_role%")
				.append(StringUtils.SPACE)
				.append("|")
				.append(StringUtils.SPACE)
				.append("Online players: %clans_clan_members_online%")
				.toString();

			player.sendMessage(PlaceholderAPI.setPlaceholders(player, message));
			return;
		}

		if (args.length == 2) {
			final ClansDao dao = new ClansDao();

			AsyncUtils.runAsync(() -> {
				dao
					.read(ClanBean.fromName(args[1]))
					.ifPresentOrElse(clanBean -> {
						final String message = new StringBuilder(StringUtils.EMPTY)
							.append("[")
							.append(clanBean.getTag())
							.append("]")
							.append(StringUtils.SPACE)
							.append(clanBean.getName())
							.append(StringUtils.SPACE)
							.append("|")
							.append(StringUtils.SPACE)
							.append("Online players:")
							.append(StringUtils.SPACE)
							.append(CoralClanPlugin
								.getInstance()
								.getClanMember()
								.entrySet()
								.stream()
								.filter(e -> e
									.getValue()
									.getClanBean()
									.getId()
									.equals(clanBean.getId()))
								.count())
							.toString();
						AsyncUtils.runTask(() -> player.sendMessage(message));
					}, () -> AsyncUtils.runTask(() -> player.sendMessage("Clan non trovato!")));

				if (dao.isError()) {
					AsyncUtils.runTask(() -> player.sendMessage("Errore interno"));
				}
			});
		}
	}
}
