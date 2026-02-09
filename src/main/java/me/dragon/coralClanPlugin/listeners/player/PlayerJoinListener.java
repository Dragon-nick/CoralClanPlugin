package me.dragon.coralClanPlugin.listeners.player;

import me.dragon.coralClanPlugin.CoralClanPlugin;
import me.dragon.coralClanPlugin.database.data.beans.ClanMemberBean;
import me.dragon.coralClanPlugin.database.data.dao.ClanMembersDao;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PlayerJoinListener implements Listener {
	@EventHandler
	public static void onPlayerJoinEvent(@NotNull final PlayerJoinEvent event) {
		final ClanMembersDao dao = new ClanMembersDao();
		final Optional<ClanMemberBean> bean = dao.read(ClanMemberBean.fromUUID(event
			.getPlayer()
			.getUniqueId()));

		bean.ifPresent(clanMemberBean -> {
			CoralClanPlugin
				.getInstance()
				.getClanMember()
				.put(clanMemberBean.getUuid(), clanMemberBean);
		});
	}
}
