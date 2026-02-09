package me.dragon.coralClanPlugin.database.data.dao;

import me.dragon.coralClanPlugin.database.data.Query;
import me.dragon.coralClanPlugin.database.data.beans.ClanBean;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class ClansDao extends AbstractGenericDao<ClanBean> {
	@Override
	public void create(@NotNull final ClanBean pBean) {
		this.executeQuery(Query.CREATE_CLAN.getQueryString(), pBean.getName(), pBean.getTag());
	}

	@Override
	public Optional<ClanBean> read(@NotNull final ClanBean pBean) {
		return this.executeReadQuerySingle(Query.READ_CLAN.getQueryString(), pBean.getId(), pBean.getName(),
			pBean.getTag());
	}

	public boolean readExists(@NotNull final ClanBean pBean) {
		return this
			.executeReadQuerySingle(Query.READ_CLAN_EXISTS.getQueryString(), pBean.getName(), pBean.getTag())
			.isPresent();
	}

	@Override
	public void delete(@NotNull final ClanBean pBean) {
		this.executeQuery(Query.DELETE_CLAN.getQueryString(), pBean.getId());
	}

	@Override
	protected @Nullable ClanBean mapper(@NotNull final ResultSet pResultSet) {
		final ClanBean bean = new ClanBean();

		try {
			bean.setId(getInt(pResultSet, "id"));
			bean.setName(getString(pResultSet, "name"));
			bean.setTag(getString(pResultSet, "tag"));
		} catch (final SQLException exception) {
			Bukkit
				.getLogger()
				.severe("Error mapping clan: " + exception);
		}

		return bean;
	}
}
