package me.dragon.coralClanPlugin.commands.clan.subcommands;

import me.dragon.coralClanPlugin.CoralClanPlugin;
import me.dragon.coralClanPlugin.commands.interfaces.ISubCommand;
import me.dragon.coralClanPlugin.database.data.beans.ClanBean;
import me.dragon.coralClanPlugin.database.data.dao.ClansDao;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class ClanCreateCommand implements ISubCommand {
	@Override
	public void execute(@NotNull final Player player, @NonNull @NotNull final String[] args,
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
				final ClansDao dao = new ClansDao(plugin.getDatabase());
				final boolean exists = dao.readExists(bean);

				if (dao.isError()) {
					player.sendMessage(ChatColor.RED + "Errore interno");
					return;
				}

				if (exists) {
					player.sendMessage(ChatColor.DARK_RED + "Esiste già un clan con questo nome/tag!");
					return;
				}

				dao.create(bean);
				if (dao.isError()) {
					player.sendMessage(ChatColor.RED + "Errore interno");
					return;
				}

				player.sendMessage(ChatColor.GREEN + "Clan creato con successo!");
			});
	}
}
