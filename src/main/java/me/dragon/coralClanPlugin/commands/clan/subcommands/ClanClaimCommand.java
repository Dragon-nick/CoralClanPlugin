package me.dragon.coralClanPlugin.commands.clan.subcommands;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import me.dragon.coralClanPlugin.CoralClanPlugin;
import me.dragon.coralClanPlugin.commands.interfaces.ISubCommand;
import me.dragon.coralClanPlugin.database.data.beans.ClanMemberBean;
import me.dragon.coralClanPlugin.database.data.dao.ClanMembersDao;
import me.dragon.coralClanPlugin.database.data.enums.Roles;
import me.dragon.coralClanPlugin.utils.AsyncUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Map;

public class ClanClaimCommand implements ISubCommand {
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
			player.sendMessage("Devi essere leader per usare questo comando!");
			return;
		}

		claimRegion(player, bean);
	}

	private static void claimRegion(@NonNull final Player player, final ClanMemberBean bean) {
		final WorldGuard worldGuard = WorldGuard.getInstance();
		final RegionContainer container = worldGuard
			.getPlatform()
			.getRegionContainer();

		final Chunk chunk = player
			.getLocation()
			.getChunk();

		final int minX = chunk.getX() << 4;
		final int minZ = chunk.getZ() << 4;
		final int maxX = minX + 15;
		final int maxZ = minZ + 15;

		final BlockVector3 min = BlockVector3.at(minX, 0, minZ);
		final BlockVector3 max = BlockVector3.at(maxX, 319, maxZ);

		final RegionManager regionManager = container.get(BukkitAdapter.adapt(player.getWorld()));
		if (regionManager == null) {
			player.sendMessage("Errore interno");
			return;
		}

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

		if (regionManager.hasRegion(regionId)) {
			player.sendMessage("Questo territorio è già reclamato.");
			return;
		}

		final ProtectedCuboidRegion region = new ProtectedCuboidRegion(regionId, min, max);

		region
			.getOwners()
			.addPlayer(player.getUniqueId());

		region.setFlags(Map.of(
			Flags.BUILD, StateFlag.State.DENY,
			Flags.PVP, StateFlag.State.DENY,
			Flags.MOB_SPAWNING,
			StateFlag.State.DENY
		));

		region.setPriority(10);

		regionManager.addRegion(region);

		final ClanMembersDao dao = new ClanMembersDao();
		AsyncUtils.runAsync(() -> {
			dao
				.readList(bean
					.getClanBean()
					.getId())
				.forEach(clanMemberBean -> {
					region
						.getMembers()
						.addPlayer(clanMemberBean.getUuid());
				});
			AsyncUtils.runTask(() -> player.sendMessage("Territorio reclamato con successo!"));
		});
	}
}
