package me.dragon.coralClanPlugin.commands.clan.subcommands;

import me.dragon.coralClanPlugin.CoralClanPlugin;
import me.dragon.coralClanPlugin.commands.interfaces.ISubCommand;
import me.dragon.coralClanPlugin.database.data.beans.ClanMemberBean;
import me.dragon.coralClanPlugin.database.data.dao.ClanMembersDao;
import me.dragon.coralClanPlugin.database.data.enums.Roles;
import me.dragon.coralClanPlugin.utils.AsyncUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ClanPromoteCommand implements ISubCommand {
	@Override
	public void execute(@NotNull final Player player, @NotNull final String[] args) {
		final ClanMemberBean bean = CoralClanPlugin
			.getInstance()
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

		if (args.length == 2) {
			final Player targerPlayer = Bukkit.getPlayer(args[1]);

			if (targerPlayer == null || targerPlayer == player) {
				player.sendMessage("Player non valido!");
				return;
			}

			final ClanMembersDao dao = new ClanMembersDao();
			AsyncUtils.runAsync(() -> {
				ClanMemberBean targetPlayerBean = CoralClanPlugin
					.getInstance()
					.getClanMember()
					.get(targerPlayer.getUniqueId());

				if (targetPlayerBean == null) {
					targetPlayerBean = dao
						.read(ClanMemberBean.fromUUID(targerPlayer.getUniqueId()))
						.orElseThrow();
				}

				if (targetPlayerBean
					.getRole()
					.equals(Roles.OFFICER)) {
					AsyncUtils.runTask(() -> player.sendMessage("Questo player è già officer!"));
					return;
				}

				targetPlayerBean.setRole(Roles.OFFICER);
				dao.update(targetPlayerBean);

				if (dao.isError()) {
					player.sendMessage("Errore interno");
					return;
				}

				if (targerPlayer.isOnline()) {
					CoralClanPlugin
						.getInstance()
						.getClanMember()
						.replace(targerPlayer.getUniqueId(), targetPlayerBean);
				}

				player.sendMessage("Player promosso ad officer!");
			});
		}
	}
}
