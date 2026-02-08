package me.dragon.coralClanPlugin.commands.clan.subcommands;

import me.dragon.coralClanPlugin.CoralClanPlugin;
import me.dragon.coralClanPlugin.commands.interfaces.ISubCommand;
import me.dragon.coralClanPlugin.database.data.beans.ClanMemberBean;
import me.dragon.coralClanPlugin.database.data.dao.ClanMembersDao;
import me.dragon.coralClanPlugin.database.data.dao.ClansDao;
import me.dragon.coralClanPlugin.database.data.enums.Roles;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public class ClanDisbandCommand implements ISubCommand {
	@Override
	public void execute(@NotNull final Player player, @NonNull @NotNull final String[] args,
		@NotNull final CoralClanPlugin plugin) {
		final ClansDao clansDao = new ClansDao(plugin.getDatabase());
		final ClanMembersDao clanMembersDao = new ClanMembersDao(plugin.getDatabase());

		Bukkit
			.getScheduler()
			.runTaskAsynchronously(plugin, () -> {
				final Optional<ClanMemberBean> optClanMemberBean =
					clanMembersDao.read(ClanMemberBean.fromUUID(player.getUniqueId()));

				if (optClanMemberBean.isEmpty() || ! optClanMemberBean
					.get()
					.getRole()
					.equals(Roles.LEADER)) {
					Bukkit
						.getScheduler()
						.runTask(plugin, () -> player.sendMessage("Non sei leader di nessun clan"));
					return;
				}

				clansDao.delete(optClanMemberBean
					.get()
					.getClanBean());

				if (clansDao.isError()) {
					Bukkit
						.getScheduler()
						.runTask(plugin, () -> player.sendMessage("Errore interno"));
					return;
				}
				Bukkit
					.getScheduler()
					.runTask(plugin, () -> player.sendMessage(ChatColor.GREEN + "Clan cancellato con successo!"));
			});
	}
}
