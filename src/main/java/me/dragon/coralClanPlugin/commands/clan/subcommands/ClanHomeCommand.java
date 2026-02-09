package me.dragon.coralClanPlugin.commands.clan.subcommands;

import me.dragon.coralClanPlugin.CoralClanPlugin;
import me.dragon.coralClanPlugin.commands.interfaces.ISubCommand;
import me.dragon.coralClanPlugin.database.data.beans.ClanMemberBean;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ClanHomeCommand implements ISubCommand {
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

		final Location location = instance
			.getClanHomes()
			.get(bean
				.getClanBean()
				.getId());
		if (location == null) {
			player.sendMessage("Home non impostata!");
			return;
		}

		player.teleport(location);
	}
}
