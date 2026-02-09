package me.dragon.coralClanPlugin.database.data.enums;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum Roles {
	LEADER,
	OFFICER,
	MEMBER;

	public static Roles convert(@NotNull final String pAccountType) {
		try {
			return valueOf(pAccountType.toUpperCase(Locale.ROOT));
		} catch (final IllegalArgumentException exception) {
			return MEMBER;
		}
	}

	public boolean isGreaterThan(@NotNull final Roles other) {
		return this.ordinal() < other.ordinal();
	}
}
