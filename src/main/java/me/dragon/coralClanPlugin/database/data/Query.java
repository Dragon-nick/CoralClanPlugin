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
	READ_CLAN("""
		SELECT id, name, tag
		FROM clans
		WHERE id = ? OR name = ? OR tag = ?
		"""),
	READ_CLAN_EXISTS("""
		SELECT id, name, tag
		FROM clans
		WHERE name = ? OR tag = ?
		"""),

	/*** Clan Members **/
	CREATE_CLAN_MEMBER("""
		INSERT INTO clan_members (uuid, role, clan_id)
		VALUES (?, ?, ?)
		"""),
	READ_CLAN_MEMBER("""
		SELECT role, c.name, c.tag
		FROM clan_members
		RIGHT JOIN clan_mc.clans c on c.id = clan_members.clan_id
		WHERE uuid = ?
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
