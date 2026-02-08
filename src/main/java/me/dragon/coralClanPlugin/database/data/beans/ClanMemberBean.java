package me.dragon.coralClanPlugin.database.data.beans;

import lombok.Data;
import me.dragon.coralClanPlugin.database.data.enums.Roles;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Data
public class ClanMemberBean {
	private UUID uuid;
	private Roles role;
	private ClanBean clanBean;

	public static ClanMemberBean of(@NotNull final UUID uuid, @NotNull final Roles role,
		@NotNull final Integer clanId) {
		final ClanMemberBean bean = new ClanMemberBean();
		bean.setUuid(uuid);
		bean.setRole(role);
		bean.setClanBean(ClanBean.fromId(clanId));

		return bean;
	}
}
