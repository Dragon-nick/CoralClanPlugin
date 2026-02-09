package me.dragon.coralClanPlugin.commands.clan.subcommands;

import me.dragon.coralClanPlugin.commands.interfaces.ISubCommand;
import me.dragon.coralClanPlugin.database.data.beans.ClanMemberBean;
import me.dragon.coralClanPlugin.database.data.dao.ClanMembersDao;
import me.dragon.coralClanPlugin.database.data.dao.ClansDao;
import me.dragon.coralClanPlugin.database.data.enums.Roles;
import me.dragon.coralClanPlugin.utils.AsyncUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public class ClanDisbandCommand implements ISubCommand {
	@Override
	public void execute(@NotNull final Player player, @NonNull @NotNull final String[] args) {
		final ClansDao clansDao = new ClansDao();
		final ClanMembersDao clanMembersDao = new ClanMembersDao();

		AsyncUtils.runAsync(() -> {
			final Optional<ClanMemberBean> optClanMemberBean =
				clanMembersDao.read(ClanMemberBean.fromUUID(player.getUniqueId()));

			if (optClanMemberBean.isEmpty() || ! optClanMemberBean
				.get()
				.getRole()
				.equals(Roles.LEADER)) {
				AsyncUtils.runTask(() -> player.sendMessage("Non sei leader di nessun clan"));
				return;
			}

			clansDao.delete(optClanMemberBean
				.get()
				.getClanBean());

			if (clansDao.isError()) {
				AsyncUtils.runTask(() -> player.sendMessage("Errore interno"));
				return;
			}
			AsyncUtils.runTask(() -> player.sendMessage(ChatColor.GREEN + "Clan cancellato con successo!"));
		});
	}
}
