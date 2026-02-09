package me.dragon.coralClanPlugin.database.data.dao;

import me.dragon.coralClanPlugin.database.data.Query;
import me.dragon.coralClanPlugin.database.data.beans.ClanBean;
import me.dragon.coralClanPlugin.database.data.beans.ClanMemberBean;
import me.dragon.coralClanPlugin.database.data.enums.Roles;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class ClanMembersDao extends AbstractGenericDao<ClanMemberBean> {
	@Override
	public void create(@NonNull final ClanMemberBean pBean) {
		this.executeQuery(
			Query.CREATE_CLAN_MEMBER.getQueryString(),
			pBean.getUuid(),
			pBean.getRole(),
			pBean
				.getClanBean()
				.getId()
		);
	}

	@Override
	public Optional<ClanMemberBean> read(@NonNull final ClanMemberBean pBean) {
		return this.executeReadQuerySingle(Query.READ_CLAN_MEMBER.getQueryString(), pBean.getUuid());
	}

	@Override
	public void update(@NonNull final ClanMemberBean pBean) {
		this.executeQuery(Query.UPDATE_CLAN_MEMBER.getQueryString(), pBean.getRole(), pBean.getUuid());
	}

	@Override
	public void delete(@NonNull final ClanMemberBean pBean) {
		this.executeQuery(Query.DELETE_CLAN_MEMBER.getQueryString(), pBean.getUuid());
	}

	@Override
	protected @Nullable ClanMemberBean mapper(@NotNull final ResultSet pResultSet) {
		final ClanMemberBean bean = new ClanMemberBean();

		try {
			bean.setUuid(UUID.fromString(getString(pResultSet, "uuid")));
			bean.setRole(Roles.convert(getString(pResultSet, "role")));

			final ClanBean clanBean = new ClanBean();
			clanBean.setId(getInt(pResultSet, "id"));
			clanBean.setName(getString(pResultSet, "name"));
			clanBean.setTag(getString(pResultSet, "tag"));

			bean.setClanBean(clanBean);
		} catch (final SQLException exception) {
			Bukkit
				.getLogger()
				.severe("Error mapping clan: " + exception);
		}

		return bean;
	}
}
