package me.dragon.coralClanPlugin.database.data.beans;

import lombok.Data;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

@Data
public class ClanHomeBean {
	private Location location;
	private Integer clanId;

	public static ClanHomeBean fromClanId(@NotNull final Integer clanId) {
		final ClanHomeBean bean = new ClanHomeBean();
		bean.setClanId(clanId);

		return bean;
	}
}
