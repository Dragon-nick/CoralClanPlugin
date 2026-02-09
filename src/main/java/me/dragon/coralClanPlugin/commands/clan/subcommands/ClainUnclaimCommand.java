package me.dragon.coralClanPlugin.commands.clan.subcommands;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import me.dragon.coralClanPlugin.CoralClanPlugin;
import me.dragon.coralClanPlugin.commands.interfaces.ISubCommand;
import me.dragon.coralClanPlugin.database.data.beans.ClanMemberBean;
import me.dragon.coralClanPlugin.database.data.enums.Roles;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ClainUnclaimCommand implements ISubCommand {
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
			player.sendMessage("Devi essere leader per usare questo comando!");
			return;
		}

		unclaimRegion(player, bean);
	}

	private static void unclaimRegion(@NotNull final Player player, final ClanMemberBean bean) {
		final WorldGuard worldGuard = WorldGuard.getInstance();
		final RegionContainer container = worldGuard
			.getPlatform()
			.getRegionContainer();

		final Chunk chunk = player
			.getLocation()
			.getChunk();

		final String regionId = new StringBuilder(StringUtils.EMPTY)
			.append("clan_")
			.append(bean
				.getClanBean()
				.getId())
			.append("_")
			.append(chunk.getX())
			.append("_")
			.append(chunk.getZ())
			.toString();

		final RegionManager regionManager = container.get(BukkitAdapter.adapt(player.getWorld()));
		if (regionManager == null) {
			player.sendMessage("Errore interno");
			return;
		}

		final ProtectedRegion region = regionManager.getRegion(regionId);
		if (region == null) {
			player.sendMessage("Questo territorio non è del clan!");
			return;
		}

		regionManager.removeRegion(regionId);
		player.sendMessage("Territorio liberato con successo!");
	}
}
