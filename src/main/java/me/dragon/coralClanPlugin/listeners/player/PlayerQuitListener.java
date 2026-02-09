package me.dragon.coralClanPlugin.listeners.player;

import me.dragon.coralClanPlugin.CoralClanPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerQuitListener implements Listener {
	@EventHandler
	public static void onPlayerQuitEvent(@NotNull final PlayerQuitEvent event) {
		CoralClanPlugin
			.getInstance()
			.getClanMember()
			.remove(event
				.getPlayer()
				.getUniqueId());
	}
}
