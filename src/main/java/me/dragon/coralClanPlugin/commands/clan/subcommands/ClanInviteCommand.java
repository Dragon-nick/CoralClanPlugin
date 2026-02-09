package me.dragon.coralClanPlugin.commands.clan.subcommands;

import me.clip.placeholderapi.PlaceholderAPI;
import me.dragon.coralClanPlugin.CoralClanPlugin;
import me.dragon.coralClanPlugin.commands.interfaces.ISubCommand;
import me.dragon.coralClanPlugin.database.data.beans.ClanMemberBean;
import me.dragon.coralClanPlugin.database.data.dao.ClanMembersDao;
import me.dragon.coralClanPlugin.database.data.enums.Roles;
import me.dragon.coralClanPlugin.utils.AsyncUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClanInviteCommand implements ISubCommand {
	private final Map<UUID, UUID> invites = new HashMap<>(Collections.emptyMap());

	@Override
	public void execute(@NotNull final Player player, @NotNull final String[] args) {
		if (args.length == 3) {
			switch (args[1]) {
				case "accept":
					this.handleAccept(args, player);
					break;
				case "decline":
					this.handleDecline(player);
			}
		}

		if (args.length == 2) {

			final Player targetPlayer = Bukkit.getPlayer(args[1]);

			if (targetPlayer == null) {
				player.sendMessage(ChatColor.RED + "Questo utente non esiste.");
				return;
			}

			if (checkPlayerPermissions(player)) {
				return;
			}

			if (player == targetPlayer) {
				player.sendMessage("Non puoi invitare te stesso...");
				return;
			}

			this.invites.put(targetPlayer.getUniqueId(), player.getUniqueId());

			final TextComponent textAccept = new TextComponent(ChatColor.GREEN + "[Accetta]");
			textAccept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Accetta l'invito")));

			textAccept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
				"/clan invite accept" + StringUtils.SPACE + player.getUniqueId()));

			final TextComponent textDecline = new TextComponent(ChatColor.RED + "[Rifiuta]");
			textDecline.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Rifiuta l'invito")));

			textDecline.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
				"/clan invite decline" + StringUtils.SPACE + player.getUniqueId()));

			targetPlayer
				.spigot()
				.sendMessage(TextComponent.fromLegacy(PlaceholderAPI.setPlaceholders(player, "Sei stato invitato nel" +
						" " +
						"clan %clans_player_clan%" + StringUtils.SPACE)), textAccept,
					TextComponent.fromLegacy(StringUtils.SPACE), textDecline);
		}
	}

	private static boolean checkPlayerPermissions(@NonNull final Player player) {
		final ClanMemberBean bean = CoralClanPlugin
			.getInstance()
			.getClanMember()
			.get(player.getUniqueId());

		if (bean == null) {
			player.sendMessage(ChatColor.RED + "Non fai parte di nessun clan!");
			return true;
		}

		if (! bean
			.getRole()
			.isGreaterThan(Roles.MEMBER)) {
			player.sendMessage(ChatColor.RED + "Devi essere Leader o Officer per utilizzare questo comando!");
			return true;
		}

		return false;
	}

	private void handleAccept(@NotNull final String[] args, @NotNull final Player player) {
		final UUID inviterUUID = this.invites.get(player.getUniqueId());

		if (inviterUUID == null) {
			return;
		}

		final CoralClanPlugin instance = CoralClanPlugin.getInstance();

		final ClanMemberBean bean = instance
			.getClanMember()
			.get(inviterUUID);

		if (bean == null) {
			return;
		}

		AsyncUtils.runTask(() -> {
			final ClanMembersDao dao = new ClanMembersDao();

			final ClanMemberBean newClanMemberBean = new ClanMemberBean();
			newClanMemberBean.setUuid(player.getUniqueId());
			newClanMemberBean.setRole(Roles.MEMBER);
			newClanMemberBean.setClanBean(bean.getClanBean());

			dao.create(newClanMemberBean);

			if (dao.isError()) {
				AsyncUtils.runTask(() -> player.sendMessage(ChatColor.RED + "Errore interno"));
				return;
			}

			instance
				.getClanMember()
				.put(player.getUniqueId(), newClanMemberBean);

			AsyncUtils.runTask(() -> player.sendMessage(ChatColor.GREEN + "Invito accettato con successo!"));
		});
	}

	private void handleDecline(@NotNull final Player player) {
		this.invites.remove(player.getUniqueId());

		player.sendMessage(ChatColor.DARK_RED + "Invito rifiutato");
	}
}
