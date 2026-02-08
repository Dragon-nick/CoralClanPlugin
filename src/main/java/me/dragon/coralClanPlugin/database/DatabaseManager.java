package me.dragon.coralClanPlugin.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
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
}
