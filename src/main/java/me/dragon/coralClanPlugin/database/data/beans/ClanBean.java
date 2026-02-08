package me.dragon.coralClanPlugin.database.data.beans;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class ClanBean {
	private Integer id;
	private String name;
	private String tag;

	public static ClanBean fromId(@NotNull final Integer id) {
		final ClanBean bean = new ClanBean();
		bean.setId(id);

		return bean;
	}

	public static ClanBean fromName(@NotNull final String name) {
		final ClanBean bean = new ClanBean();
		bean.setName(name);

		return bean;
	}
}
