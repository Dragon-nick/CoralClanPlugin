package me.dragon.coralClanPlugin.database.data;

import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public enum Query {
	/*** Clans **/
	CREATE_CLAN("""
		INSERT INTO clans (name, tag)
		VALUES (?, ?)
		"""),
	READ_CLAN_EXISTS("""
		SELECT id, name, tag
		FROM clans
		WHERE name = ? OR tag = ?
		""");

	private final String queryString;

	@Contract(pure = true)
	Query(@Language("MariaDB") @NotNull final String pQuery) {
		this.queryString = pQuery;
	}

	/**
	 * Retrieves the query stored in the object.
	 *
	 * @return the query stored in the object
	 */
	@Contract(pure = true)
	public String getQueryString() {
		return this.queryString;
	}
}
