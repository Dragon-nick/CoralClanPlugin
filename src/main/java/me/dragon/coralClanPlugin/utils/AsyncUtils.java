package me.dragon.coralClanPlugin.utils;

import me.dragon.coralClanPlugin.CoralClanPlugin;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public final class AsyncUtils {
	private AsyncUtils() {
		throw new UnsupportedOperationException("Utility class");
	}

	public static void runAsync(@NotNull final Runnable task) {
		Bukkit
			.getScheduler()
			.runTaskAsynchronously(CoralClanPlugin.getInstance(), task);
	}

	public static void runTask(@NotNull final Runnable task) {
		Bukkit
			.getScheduler()
			.runTask(CoralClanPlugin.getInstance(), task);
	}
}
