package me.dragon.coralClanPlugin;

import lombok.Getter;
import me.dragon.coralClanPlugin.commands.clan.ClanCommand;
import me.dragon.coralClanPlugin.database.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class CoralClanPlugin extends JavaPlugin {
	@Getter
	private final DatabaseManager database = new DatabaseManager();

	@Override
	public void onEnable() {
		this.saveDefaultConfig();

		this.connectDatabase();

		Objects
			.requireNonNull(this.getCommand("clan"))
			.setExecutor(new ClanCommand(this));
	}

	@Override
	public void onDisable() {
		this.database.disconnect();
	}

	private void connectDatabase() {
		final ConfigurationSection configurationSection = this
			.getConfig()
			.getConfigurationSection("database");

		if (configurationSection == null) {
			this
				.getLogger()
				.severe("Database is not configured!");
			Bukkit
				.getPluginManager()
				.disablePlugin(this);
			return;
		}

		this.database.connect(
			Objects.requireNonNull(configurationSection.getString("host")),
			configurationSection.getInt("port"),
			Objects.requireNonNull(configurationSection.getString("name")),
			Objects.requireNonNull(configurationSection.getString("user")),
			Objects.requireNonNull(configurationSection.getString("password"))
		);
	}
}
