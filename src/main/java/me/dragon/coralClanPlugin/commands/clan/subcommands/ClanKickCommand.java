package me.dragon.coralClanPlugin.commands.clan.subcommands;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import me.dragon.coralClanPlugin.CoralClanPlugin;
import me.dragon.coralClanPlugin.commands.interfaces.ISubCommand;
import me.dragon.coralClanPlugin.database.data.beans.ClanMemberBean;
import me.dragon.coralClanPlugin.database.data.dao.ClanMembersDao;
import me.dragon.coralClanPlugin.database.data.enums.Roles;
import me.dragon.coralClanPlugin.utils.AsyncUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class ClanKickCommand implements ISubCommand {
	@Override
	public void execute(@NotNull final Player player, @NotNull final String[] args) {
		final CoralClanPlugin instance = CoralClanPlugin.getInstance();
		final ClanMemberBean clanMemberBean = instance
			.getClanMember()
			.get(player.getUniqueId());

		if (checkPlayerPermissions(player, clanMemberBean)) {
			return;
		}

		final Player kickedPlayer = Bukkit.getPlayer(args[1]);

		if (kickedPlayer == null) {
			player.sendMessage(ChatColor.DARK_RED + "Questo player non esiste!");
			return;
		}

		if (player == kickedPlayer) {
			player.sendMessage("Non puoi rimuovere te stesso dal clan!");
			return;
		}

		final ClanMemberBean kickedPlayerBean = instance
			.getClanMember()
			.get(kickedPlayer.getUniqueId());

		final ClanMembersDao dao = new ClanMembersDao();
		if (kickedPlayerBean == null) {
			AsyncUtils.runAsync(() -> {
				dao
					.read(ClanMemberBean.fromUUID(kickedPlayer.getUniqueId()))
					.ifPresent(dao :: delete);
				if (dao.isError()) {
					AsyncUtils.runTask(() -> player.sendMessage("Errore interno"));
					return;
				}
				AsyncUtils.runTask(() -> player.sendMessage("Player rimosso con successo!"));
			});
		} else {
			AsyncUtils.runAsync(() -> {
				dao.delete(kickedPlayerBean);
				if (dao.isError()) {
					AsyncUtils.runTask(() -> player.sendMessage("Errore interno"));
					return;
				}
				final WorldGuard worldGuard = WorldGuard.getInstance();
				final RegionContainer container = worldGuard
					.getPlatform()
					.getRegionContainer();

				final RegionManager regionManager = container.get(BukkitAdapter.adapt(player.getWorld()));
				if (regionManager == null) {
					player.sendMessage("Errore interno nel dare l'accesso ai territori");
					return;
				}

				regionManager
					.getRegions()
					.entrySet()
					.stream()
					.filter(e -> e
						.getKey()
						.startsWith("clan_" + kickedPlayerBean
							.getClanBean()
							.getId()))
					.forEach(e -> {
						e
							.getValue()
							.getMembers()
							.removePlayer(player.getUniqueId());
					});

				AsyncUtils.runTask(() -> player.sendMessage("Player rimosso con successo!"));
			});
		}
	}

	private static boolean checkPlayerPermissions(@NonNull final Player player, @Nullable final ClanMemberBean bean) {
		if (bean == null) {
			player.sendMessage(ChatColor.RED + "Non fai parte di nessun clan!");
			return true;
		}

		if (! bean
			.getRole()
			.isGreaterThan(Roles.MEMBER)) {
			player.sendMessage(ChatColor.RED + "Devi essere Leader o Officer per utilizzare questo comando!");
			return true;
		}

		return false;
	}
}
