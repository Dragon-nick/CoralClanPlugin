package me.dragon.coralClanPlugin.listeners.player;

import me.dragon.coralClanPlugin.CoralClanPlugin;
import me.dragon.coralClanPlugin.database.data.beans.ClanHomeBean;
import me.dragon.coralClanPlugin.database.data.beans.ClanMemberBean;
import me.dragon.coralClanPlugin.database.data.dao.ClanHomeDao;
import me.dragon.coralClanPlugin.database.data.dao.ClanMembersDao;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PlayerJoinListener implements Listener {
	@EventHandler
	public static void onPlayerJoinEvent(@NotNull final PlayerJoinEvent event) {
		final CoralClanPlugin instance = CoralClanPlugin.getInstance();

		final ClanMembersDao clanMembersDao = new ClanMembersDao();
		final Optional<ClanMemberBean> clanMemberBean = clanMembersDao.read(ClanMemberBean.fromUUID(event
			.getPlayer()
			.getUniqueId()));

		clanMemberBean.ifPresent(localBean -> {
			instance
				.getClanMember()
				.put(localBean.getUuid(), localBean);
		});

		final int clanId = instance
			.getClanMember()
			.get(event
				.getPlayer()
				.getUniqueId())
			.getClanBean()
			.getId();

		if (instance
			.getClanHomes()
			.get(clanId) == null) {
			final ClanHomeDao homeDao = new ClanHomeDao();
			final Optional<ClanHomeBean> clanHomeBean =
				homeDao.read(ClanHomeBean.fromClanId(clanId));

			clanHomeBean.ifPresent(localBean -> {
				instance
					.getClanHomes()
					.put(clanId, localBean.getLocation());
			});
		}
	}
}
