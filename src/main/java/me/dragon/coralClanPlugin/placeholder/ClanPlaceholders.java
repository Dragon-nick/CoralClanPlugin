package me.dragon.coralClanPlugin.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.dragon.coralClanPlugin.CoralClanPlugin;
import me.dragon.coralClanPlugin.database.data.beans.ClanMemberBean;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ClanPlaceholders extends PlaceholderExpansion {
	@Override
	public @NotNull String getIdentifier() {
		return "clans";
	}

	@Override
	public @NotNull String getAuthor() {
		return "Dragon-nick";
	}

	@Override
	public @NotNull String getVersion() {
		return "1.0";
	}

	@Override
	public boolean canRegister() {
		return true;
	}

	@Override
	public boolean persist() {
		return true;
	}

	@Override
	public @Nullable String onPlaceholderRequest(final Player player, @NotNull final String params) {
		if (player == null) {
			return null;
		}

		final ClanMemberBean bean = CoralClanPlugin
			.getInstance()
			.getClanMember()
			.get(player.getUniqueId());

		if (bean != null) {
			return switch (params) {
				case "player_clan" -> handlePlayerClan(bean);
				case "player_tag" -> handlePlayerTag(bean);
				case "player_role" -> handlePlayerRole(bean);
				case "clan_members_online" -> handleMembersOnline(bean);
				default -> null;
			};
		}

		return "No clan";
	}

	private static @NotNull String handlePlayerClan(@NotNull final ClanMemberBean pBean) {
		return pBean
			.getClanBean()
			.getName();
	}

	private static @NotNull String handlePlayerTag(@NotNull final ClanMemberBean pBean) {
		return pBean
			.getClanBean()
			.getTag();
	}

	private static @NotNull String handlePlayerRole(@NotNull final ClanMemberBean pBean) {
		return pBean
			.getRole()
			.toString();
	}

	private static @NotNull String handleMembersOnline(@NotNull final ClanMemberBean pBean) {
		return String.valueOf(CoralClanPlugin
			.getInstance()
			.getClanMember()
			.values()
			.stream()
			.filter(clanMemberBean -> Objects.equals(clanMemberBean
				.getClanBean()
				.getName(), pBean
				.getClanBean()
				.getName()))
			.count());
	}
}
