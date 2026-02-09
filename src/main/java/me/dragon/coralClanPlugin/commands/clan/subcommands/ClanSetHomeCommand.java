package me.dragon.coralClanPlugin.commands.clan.subcommands;

import me.dragon.coralClanPlugin.CoralClanPlugin;
import me.dragon.coralClanPlugin.commands.interfaces.ISubCommand;
import me.dragon.coralClanPlugin.database.data.beans.ClanHomeBean;
import me.dragon.coralClanPlugin.database.data.beans.ClanMemberBean;
import me.dragon.coralClanPlugin.database.data.dao.ClanHomeDao;
import me.dragon.coralClanPlugin.database.data.enums.Roles;
import me.dragon.coralClanPlugin.utils.AsyncUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ClanSetHomeCommand implements ISubCommand {
	@Override
	public void execute(@NotNull final Player player, @NotNull final String[] args) {
		final CoralClanPlugin instance = CoralClanPlugin.getInstance();
		final ClanMemberBean bean = instance
			.getClanMember()
			.get(player.getUniqueId());

		if (bean == null) {
			player.sendMessage("Non fai parte di nessun clan!");
			return;
		}

		if (! bean
			.getRole()
			.equals(Roles.LEADER)) {
			player.sendMessage("Per utilizzare questo comando devi essere leader del clan!");
			return;
		}

		final ClanHomeDao dao = new ClanHomeDao();
		if (instance
			.getClanHomes()
			.get(bean
				.getClanBean()
				.getId()) != null) {
			AsyncUtils.runAsync(() -> {
				final ClanHomeBean clanHomeBean = new ClanHomeBean();
				clanHomeBean.setLocation(player.getLocation());
				clanHomeBean.setClanId(bean
					.getClanBean()
					.getId());

				dao.update(clanHomeBean);

				if (dao.isError()) {
					AsyncUtils.runTask(() -> player.sendMessage("Errore interno"));
					return;
				}

				instance
					.getClanHomes()
					.replace(bean
						.getClanBean()
						.getId(), player.getLocation());

				player.sendMessage("Home cambiata con successo");
			});
		} else {
			final ClanHomeBean clanHomeBean = new ClanHomeBean();
			clanHomeBean.setLocation(player.getLocation());
			clanHomeBean.setClanId(bean
				.getClanBean()
				.getId());

			dao.create(clanHomeBean);

			if (dao.isError()) {
				AsyncUtils.runTask(() -> player.sendMessage("Errore interno"));
				return;
			}

			instance
				.getClanHomes()
				.put(bean
					.getClanBean()
					.getId(), player.getLocation());

			player.sendMessage("Home creata con successo");
		}
	}
}
