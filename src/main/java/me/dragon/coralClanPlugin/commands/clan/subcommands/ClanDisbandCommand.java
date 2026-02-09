package me.dragon.coralClanPlugin.commands.clan.subcommands;

import me.dragon.coralClanPlugin.CoralClanPlugin;
import me.dragon.coralClanPlugin.commands.interfaces.ISubCommand;
import me.dragon.coralClanPlugin.database.data.beans.ClanBean;
import me.dragon.coralClanPlugin.database.data.beans.ClanMemberBean;
import me.dragon.coralClanPlugin.database.data.dao.ClansDao;
import me.dragon.coralClanPlugin.database.data.enums.Roles;
import me.dragon.coralClanPlugin.utils.AsyncUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.UUID;

public class ClanDisbandCommand implements ISubCommand {
	@Override
	public void execute(@NotNull final Player player, @NonNull @NotNull final String[] args) {
		final ClansDao clansDao = new ClansDao();

		AsyncUtils.runAsync(() -> {
			final Map<UUID, ClanMemberBean> clanMember = CoralClanPlugin
				.getInstance()
				.getClanMember();
			final ClanMemberBean clanMemberBean = clanMember.get(player.getUniqueId());

			if (clanMemberBean == null || ! clanMemberBean
				.getRole()
				.equals(Roles.LEADER)) {
				AsyncUtils.runTask(() -> player.sendMessage("Non sei leader di nessun clan"));
				return;
			}

			clansDao.delete(clanMemberBean.getClanBean());

			if (clansDao.isError()) {
				AsyncUtils.runTask(() -> player.sendMessage("Errore interno"));
				return;
			}

			final int id = clanMemberBean
				.getClanBean()
				.getId();

			clanMember
				.entrySet()
				.removeIf(e -> {
					final ClanMemberBean bean = e.getValue();
					final ClanBean clan = (bean == null) ?
						null:
						bean.getClanBean();
					final Integer clanId = (clan == null) ?
						null:
						clan.getId();
					return clanId != null && clanId == id;
				});

			AsyncUtils.runTask(() -> player.sendMessage(ChatColor.GREEN + "Clan cancellato con successo!"));
		});
	}
}
