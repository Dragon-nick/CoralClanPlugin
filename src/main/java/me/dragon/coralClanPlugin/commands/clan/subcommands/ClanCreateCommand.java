package me.dragon.coralClanPlugin.commands.clan.subcommands;

import me.dragon.coralClanPlugin.CoralClanPlugin;
import me.dragon.coralClanPlugin.commands.interfaces.ISubCommand;
import me.dragon.coralClanPlugin.database.data.beans.ClanBean;
import me.dragon.coralClanPlugin.database.data.beans.ClanMemberBean;
import me.dragon.coralClanPlugin.database.data.dao.ClanMembersDao;
import me.dragon.coralClanPlugin.database.data.dao.ClansDao;
import me.dragon.coralClanPlugin.database.data.enums.Roles;
import me.dragon.coralClanPlugin.utils.AsyncUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ClanCreateCommand implements ISubCommand {
	@Override
	public void execute(@NotNull final Player player, @NotNull final String[] args) {
		final ClanMemberBean clanMemberBean = CoralClanPlugin
			.getInstance()
			.getClanMember()
			.get(player.getUniqueId());

		if (clanMemberBean != null && clanMemberBean
			.getRole()
			.equals(Roles.LEADER)) {
			player.sendMessage("Sei già leader di un clan!");
		}

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

		AsyncUtils.runAsync(() -> {
			final ClansDao clansDao = new ClansDao();
			final ClanMembersDao clanMembersDao = new ClanMembersDao();

			final boolean exists = clansDao.readExists(bean);

			if (clansDao.isError()) {
				AsyncUtils.runTask(() -> player.sendMessage(ChatColor.RED + "Errore interno"));
				return;
			}

			if (exists) {
				AsyncUtils.runTask(() -> player.sendMessage(ChatColor.DARK_RED + "Esiste già un clan con " +
					"questo" +
					" " +
					"nome/tag!"));
				return;
			}

			clansDao.create(bean);
			if (clansDao.isError()) {
				AsyncUtils.runTask(() -> player.sendMessage(ChatColor.RED + "Errore interno"));
				return;
			}

			final Optional<ClanBean> clanBean = clansDao.read(ClanBean.fromName(bean.getName()));
			if (clansDao.isError()) {
				AsyncUtils.runTask(() -> player.sendMessage(ChatColor.RED + "Errore interno"));
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
				AsyncUtils.runTask(() -> player.sendMessage(ChatColor.RED + "Errore interno"));
				return;
			}

			clanMembersDao
				.read(ClanMemberBean.fromUUID(player.getUniqueId()))
				.ifPresent(localBean -> {
					CoralClanPlugin
						.getInstance()
						.getClanMember()
						.put(player.getUniqueId(), localBean);
				});

			AsyncUtils.runTask(() -> player.sendMessage(ChatColor.GREEN + "Clan creato con successo!"));
		});
	}
}
