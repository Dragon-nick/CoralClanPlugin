package me.dragon.coralClanPlugin.database.data.dao;

import me.dragon.coralClanPlugin.database.data.Query;
import me.dragon.coralClanPlugin.database.data.beans.ClanHomeBean;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class ClanHomeDao extends AbstractGenericDao<ClanHomeBean> {
	@Override
	public void create(@NotNull final ClanHomeBean pBean) {
		this.executeQuery(Query.CREATE_CLAN_HOME.getQueryString(),
			pBean
				.getLocation()
				.getX(),
			pBean
				.getLocation()
				.getY(),
			pBean
				.getLocation()
				.getZ(),
			pBean.getClanId()
		);
	}

	@Override
	public Optional<ClanHomeBean> read(@NotNull final ClanHomeBean pBean) {
		return this.executeReadQuerySingle(Query.READ_CLAN_HOME.getQueryString(), pBean.getClanId());
	}

	@Override
	public void update(@NotNull final ClanHomeBean pBean) {
		this.executeQuery(Query.UPDATE_CLAN_HOME.getQueryString(),
			pBean
				.getLocation()
				.getX(),
			pBean
				.getLocation()
				.getY(),
			pBean
				.getLocation()
				.getZ(),
			pBean.getClanId()
		);
	}

	@Override
	protected @Nullable ClanHomeBean mapper(@NotNull final ResultSet pResultSet) {
		final ClanHomeBean bean = new ClanHomeBean();

		try {
			bean.setClanId(getInt(pResultSet, "clanId"));
			final Location location = new Location(
				Bukkit.getWorld("world"),
				getDouble(pResultSet, "x"),
				getDouble(pResultSet, "y"),
				getDouble(pResultSet, "z")
			);
			bean.setLocation(location);
		} catch (final SQLException exception) {
			Bukkit
				.getLogger()
				.severe("Error mapping clan: " + exception);
		}

		return bean;
	}
}
