package me.dragon.coralClanPlugin.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.Duration;

public final class DatabaseManager {
	private HikariDataSource dataSource = null;

	public void connect(@NotNull final String host, @NotNull final Integer port, @NotNull final String database,
		@NotNull final String username, @NotNull final String password) {

		final HikariConfig config = new HikariConfig();

		config.setDriverClassName("org.mariadb.jdbc.Driver");

		config.setJdbcUrl(
			new StringBuilder("jdbc:mariadb://")
				.append(host)
				.append(":")
				.append(port)
				.append("/")
				.append(database)
				.toString()
		);

		config.setUsername(username);
		config.setPassword(password);

		config.setMaximumPoolSize(20);
		config.setMinimumIdle(2);
		config.setIdleTimeout(Duration
			.ofMinutes(5L)
			.toMillis());
		config.setMaxLifetime(Duration
			.ofMinutes(30L)
			.toMillis());
		config.setConnectionTimeout(Duration
			.ofSeconds(10L)
			.toMillis());

		config.setPoolName("CoralClanPlugin Database");

		this.dataSource = new HikariDataSource(config);
	}

	public Connection getConnection() throws SQLException {
		return this.dataSource.getConnection();
	}

	public void disconnect() {
		if (this.dataSource != null && this.dataSource.isRunning()) {
			this.dataSource.close();
		}
	}

	/**
	 * Checks if a specified column exists in the given ResultSet object.
	 *
	 * @param resultSet  the ResultSet object to check
	 * @param columnName the name of the column to check for
	 * @return true if the column exists, false otherwise
	 * @throws SQLException if an error accessing the ResultSet
	 */
	public static boolean columnNotExists(@NotNull final ResultSet resultSet, final @NonNls String columnName)
		throws SQLException {
		final ResultSetMetaData metadata = resultSet.getMetaData();
		final int columns = metadata.getColumnCount();

		for (int x = 1; x <= columns; x++) {
			if (columnName.equals(metadata.getColumnName(x))) {
				return false;
			}
		}
		return true;
	}
}
