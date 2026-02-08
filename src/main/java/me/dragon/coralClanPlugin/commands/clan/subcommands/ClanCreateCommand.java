package me.dragon.coralClanPlugin.commands.clan.subcommands;

import me.dragon.coralClanPlugin.CoralClanPlugin;
import me.dragon.coralClanPlugin.commands.interfaces.ISubCommand;
import me.dragon.coralClanPlugin.database.data.beans.ClanBean;
import me.dragon.coralClanPlugin.database.data.beans.ClanMemberBean;
import me.dragon.coralClanPlugin.database.data.dao.ClanMembersDao;
import me.dragon.coralClanPlugin.database.data.dao.ClansDao;
import me.dragon.coralClanPlugin.database.data.enums.Roles;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ClanCreateCommand implements ISubCommand {
	@Override
	public void execute(@NotNull final Player player, @NotNull final String[] args,
		@NotNull final CoralClanPlugin plugin) {
		final String clanName = args[1];
		final String clanTag = args[2];

		if (clanName.length() > 255) {
			player.sendMessage("Il nome del clan è troppo lungo max 255 caratteri!");
			return;
		}

		if (clanTag.length() > 10) {
			player.sendMessage("Il tag del clan è troppo lungo max 10 caratteri!");
			return;
		}

		final ClanBean bean = new ClanBean();
		bean.setName(args[1]);
		bean.setTag(args[2]);

		Bukkit
			.getScheduler()
			.runTaskAsynchronously(plugin, () -> {
				final ClansDao clansDao = new ClansDao(plugin.getDatabase());
				final ClanMembersDao clanMembersDao = new ClanMembersDao(plugin.getDatabase());

				final boolean exists = clansDao.readExists(bean);

				if (clansDao.isError()) {
					Bukkit
						.getScheduler()
						.runTask(plugin, () -> player.sendMessage(ChatColor.RED + "Errore interno"));
					return;
				}

				if (exists) {
					Bukkit
						.getScheduler()
						.runTask(plugin, () -> player.sendMessage(ChatColor.DARK_RED + "Esiste già un clan con " +
							"questo" +
							" " +
							"nome/tag!"));
					return;
				}

				clansDao.create(bean);
				if (clansDao.isError()) {
					Bukkit
						.getScheduler()
						.runTask(plugin, () -> player.sendMessage(ChatColor.RED + "Errore interno"));
					return;
				}

				final Optional<ClanBean> clanBean = clansDao.read(ClanBean.fromName(bean.getName()));
				if (clansDao.isError()) {
					Bukkit
						.getScheduler()
						.runTask(plugin, () -> player.sendMessage(ChatColor.RED + "Errore interno"));
					return;
				}

				clanMembersDao.create(
					ClanMemberBean.of(
						player.getUniqueId(),
						Roles.LEADER,
						clanBean
							.get()
							.getId()
					)
				);
				if (clanMembersDao.isError()) {
					Bukkit
						.getScheduler()
						.runTask(plugin, () -> player.sendMessage(ChatColor.RED + "Errore interno"));
					return;
				}

				Bukkit
					.getScheduler()
					.runTask(plugin, () -> player.sendMessage(ChatColor.GREEN + "Clan creato con successo!"));
			});
	}
}
