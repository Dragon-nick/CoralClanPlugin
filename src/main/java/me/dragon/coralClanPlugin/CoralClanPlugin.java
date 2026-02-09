package me.dragon.coralClanPlugin;

import lombok.Getter;
import me.dragon.coralClanPlugin.commands.clan.ClanCommand;
import me.dragon.coralClanPlugin.database.DatabaseManager;
import me.dragon.coralClanPlugin.database.data.beans.ClanMemberBean;
import me.dragon.coralClanPlugin.listeners.player.PlayerJoinListener;
import me.dragon.coralClanPlugin.placeholder.ClanPlaceholders;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class CoralClanPlugin extends JavaPlugin {
	@Getter
	private final DatabaseManager database = new DatabaseManager();

	@Getter
	private static CoralClanPlugin instance = null;

	@Getter
	private final Map<UUID, ClanMemberBean> clanMember = new HashMap<>(Collections.emptyMap());
	@Getter
	private final Map<Integer, Location> clanHomes = new HashMap<>(Collections.emptyMap());

	@Override
	public void onEnable() {
		instance = this;

		this.saveDefaultConfig();

		this.connectDatabase();

		this.registerPlaceholders();

		this
			.getServer()
			.getPluginManager()
			.registerEvents(new PlayerJoinListener(), this);

		Objects
			.requireNonNull(this.getCommand("clan"))
			.setExecutor(new ClanCommand());
	}

	@Override
	public void onDisable() {
		this.database.disconnect();
	}

	private void registerPlaceholders() {
		if (Bukkit
			.getPluginManager()
			.isPluginEnabled("PlaceholderAPI")) {
			new ClanPlaceholders().register();
		} else {
			this
				.getLogger()
				.warning("Could not find PlaceholderAPI! This plugin is required.");
			Bukkit
				.getPluginManager()
				.disablePlugin(this);
		}
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
