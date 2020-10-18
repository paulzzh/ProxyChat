/*
 * ProxyChat, a Velocity chat solution
 * Copyright (C) 2020 James Lyne
 *
 * Based on BungeeChat2 (https://github.com/AuraDevelopmentTeam/BungeeChat2)
 * Copyright (C) 2020 Aura Development Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package uk.co.notnull.ProxyChat.message;

import com.typesafe.config.Config;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.identity.Identity;
import uk.co.notnull.ProxyChat.account.ProxyChatAccountManager;
import uk.co.notnull.ProxyChat.api.account.AccountManager;
import uk.co.notnull.ProxyChat.api.account.ProxyChatAccount;
import uk.co.notnull.ProxyChat.api.enums.ChannelType;
import uk.co.notnull.ProxyChat.api.filter.BlockMessageException;
import uk.co.notnull.ProxyChat.api.filter.FilterManager;
import uk.co.notnull.ProxyChat.api.module.ModuleManager;
import uk.co.notnull.ProxyChat.api.placeholder.ProxyChatContext;
import uk.co.notnull.ProxyChat.api.placeholder.InvalidContextError;
import uk.co.notnull.ProxyChat.chatlog.ChatLoggingManager;
import uk.co.notnull.ProxyChat.module.ProxyChatModuleManager;
import uk.co.notnull.ProxyChat.module.IgnoringModule;
import uk.co.notnull.ProxyChat.api.permission.Permission;
import uk.co.notnull.ProxyChat.permission.PermissionManager;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import lombok.Setter;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

@UtilityClass
public class MessagesService {
	private static final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.builder()
          .extractUrls(
				  Style.style().color(TextColor.fromHexString("#8194e4")).decoration(TextDecoration.UNDERLINED, true).build())
          .character('&').hexColors().useUnusualXRepeatedCharacterHexFormat().build();

	@Setter
	private List<List<String>> multiCastServerGroups = null;

	public void unsetMultiCastServerGroups() {
		setMultiCastServerGroups(null);
	}

	public void sendPrivateMessage(CommandSource sender, CommandSource target, String message) throws InvalidContextError {
		ProxyChatContext context = new Context(sender, target, message);
		boolean allowed = parseMessage(context, true);

		if(allowed) {
			sendPrivateMessage(context);
		}
	}

	public void sendPrivateMessage(ProxyChatContext context) throws InvalidContextError {
		context.require(ProxyChatContext.HAS_SENDER, ProxyChatContext.HAS_TARGET,
						ProxyChatContext.HAS_MESSAGE, ProxyChatContext.IS_PARSED);

		Optional<ProxyChatAccount> account = context.getSender();
		ProxyChatAccount senderAccount = account.get();
		ProxyChatAccount targetAccount = context.getTarget().get();
		CommandSource sender = ProxyChatAccountManager.getCommandSource(senderAccount).get();
		CommandSource target = ProxyChatAccountManager.getCommandSource(targetAccount).get();
		boolean filterPrivateMessages =
				ProxyChatModuleManager.MESSENGER_MODULE
						.getModuleSection()
						.getBoolean("filterPrivateMessages");

		if (targetAccount.hasIgnored(senderAccount)
				&& !PermissionManager.hasPermission(sender, Permission.BYPASS_IGNORE)) {
			sendMessage(sender, Messages.HAS_INGORED.get(context));

			return;
		}

		Optional<Component> messageSender =
				preProcessMessage(context, account, Format.MESSAGE_SENDER, filterPrivateMessages);

		if (messageSender.isPresent()) {
			MessagesService.sendMessage(sender, messageSender.get());

			Component messageTarget =
					preProcessMessage(context, account, Format.MESSAGE_TARGET, filterPrivateMessages, true)
							.get();
			MessagesService.sendMessage(target, messageTarget);

			if (ModuleManager.isModuleActive(ProxyChatModuleManager.SPY_MODULE)
					&& !account.get().hasPermission(Permission.COMMAND_SOCIALSPY_EXEMPT)) {
				Component socialSpyMessage =
						preProcessMessage(context, account, Format.SOCIAL_SPY, false).get();

				sendToMatchingPlayers(
						socialSpyMessage,
						acc ->
								(!acc.getUniqueId().equals(senderAccount.getUniqueId()))
										&& (!acc.getUniqueId().equals(targetAccount.getUniqueId()))
										&& acc.hasSocialSpyEnabled());
			}
		}

		if (ProxyChatModuleManager.CHAT_LOGGING_MODULE
				.getModuleSection()
				.getBoolean("privateMessages")) {
			ChatLoggingManager.logMessage("PM to " + targetAccount.getName(), context);
		}
	}

	public void sendChannelMessage(CommandSource sender, ChannelType channel, String message) throws InvalidContextError {
		ProxyChatContext context = new Context(sender, message);
		boolean allowed = parseMessage(context, true);

		if(allowed) {
			sendChannelMessage(context, channel);
		}
	}

	public void sendChannelMessage(ProxyChatContext context, ChannelType channel)
			throws InvalidContextError {
		context.require(ProxyChatContext.HAS_SENDER, ProxyChatContext.HAS_MESSAGE, ProxyChatContext.IS_PARSED);

		switch (channel) {
			case GLOBAL:
				sendGlobalMessage(context);
				break;
			case LOCAL:
				sendLocalMessage(context);
				break;
			case STAFF:
				sendStaffMessage(context);
				break;
			case HELP:
				sendHelpMessage(context);
				break;
			default:
				// Ignore
				break;
		}
	}

	public void sendGlobalMessage(CommandSource sender, String message) throws InvalidContextError {
		ProxyChatContext context = new Context(sender, message);
		boolean allowed = parseMessage(context, true);

		if(allowed) {
			sendGlobalMessage(context);
		}
	}

	public void sendGlobalMessage(ProxyChatContext context) throws InvalidContextError {
		context.require(ProxyChatContext.HAS_SENDER, ProxyChatContext.HAS_MESSAGE, ProxyChatContext.IS_PARSED);

		Optional<ProxyChatAccount> account = context.getSender();
		Optional<Component> finalMessage = preProcessMessage(context, Format.GLOBAL_CHAT);

		sendToMatchingPlayers(finalMessage, getGlobalPredicate(), getNotIgnoredPredicate(account));

		ChatLoggingManager.logMessage(ChannelType.GLOBAL, context);
	}

	public void sendLocalMessage(CommandSource sender, String message) throws InvalidContextError {
		ProxyChatContext context = new Context(sender, message);
		boolean allowed = parseMessage(context, true);

		if(allowed) {
			sendLocalMessage(context);
		}
	}

	public void sendLocalMessage(ProxyChatContext context) throws InvalidContextError {
		context.require(ProxyChatContext.HAS_SENDER, ProxyChatContext.HAS_MESSAGE, ProxyChatContext.IS_PARSED);

		Optional<ProxyChatAccount> account = context.getSender();
		Optional<Component> finalMessage = preProcessMessage(context, Format.LOCAL_CHAT);
		RegisteredServer localServer = context.getServer().orElse(context.getSender().get().getServer().orElse(null));

		Predicate<ProxyChatAccount> isLocal = getLocalPredicate(localServer);
		Predicate<ProxyChatAccount> notIgnored = getNotIgnoredPredicate(account);

		sendToMatchingPlayers(finalMessage, isLocal, notIgnored);

		ChatLoggingManager.logMessage(ChannelType.LOCAL, context);

		if (ModuleManager.isModuleActive(ProxyChatModuleManager.SPY_MODULE)) {
			Component localSpyMessage = preProcessMessage(context, account, Format.LOCAL_SPY, false).get();
			Predicate<ProxyChatAccount> isNotLocal = isLocal.negate();

			sendToMatchingPlayers(
					localSpyMessage, ProxyChatAccount::hasLocalSpyEnabled, isNotLocal, notIgnored);
		}
	}

	public void sendTransparentMessage(ProxyChatContext context) throws InvalidContextError {
		context.require(ProxyChatContext.HAS_SENDER, ProxyChatContext.HAS_MESSAGE, ProxyChatContext.IS_PARSED);

		Optional<ProxyChatAccount> account = context.getSender();
		RegisteredServer localServer = context.getServer().orElse(context.getSender().get().getServer().orElse(null));
		Predicate<ProxyChatAccount> isLocal = getLocalPredicate(localServer);

		ChatLoggingManager.logMessage(ChannelType.LOCAL, context);

		if (ModuleManager.isModuleActive(ProxyChatModuleManager.SPY_MODULE)
				&& !account.get().hasPermission(Permission.COMMAND_LOCALSPY_EXEMPT)) {
			Component localSpyMessage = preProcessMessage(context, account, Format.LOCAL_SPY, false).get();
			Predicate<ProxyChatAccount> isNotLocal = isLocal.negate();

			sendToMatchingPlayers(localSpyMessage, ProxyChatAccount::hasLocalSpyEnabled, isNotLocal);
		}
	}

	public void sendStaffMessage(CommandSource sender, String message) throws InvalidContextError {
		ProxyChatContext context = new Context(sender, message);
		boolean allowed = parseMessage(context, true);

		if(allowed) {
			sendStaffMessage(context);
		}
	}

	public void sendStaffMessage(ProxyChatContext context) throws InvalidContextError {
		context.require(ProxyChatContext.HAS_SENDER, ProxyChatContext.HAS_MESSAGE, ProxyChatContext.IS_PARSED);

		Optional<Component> finalMessage = preProcessMessage(context, Format.STAFF_CHAT);

		sendToMatchingPlayers(
				finalMessage, pp -> pp.hasPermission(Permission.COMMAND_STAFFCHAT_VIEW));

		ChatLoggingManager.logMessage(ChannelType.STAFF, context);
	}

	public void sendHelpMessage(CommandSource sender, String message) throws InvalidContextError {
		ProxyChatContext context = new Context(sender, message);
		boolean allowed = parseMessage(context, true);

		if(allowed) {
			sendHelpMessage(context);
		}
	}

	public void sendHelpMessage(ProxyChatContext context) throws InvalidContextError {
		context.require(ProxyChatContext.HAS_SENDER, ProxyChatContext.HAS_MESSAGE, ProxyChatContext.IS_PARSED);

		Optional<Component> finalMessage = preProcessMessage(context, Format.HELP_OP);
		ProxyChatAccount sender = context.getSender().get();

		sendToMatchingPlayers(
				finalMessage,
				pp ->
						pp.hasPermission(Permission.COMMAND_HELPOP_VIEW)
								|| sender.equals(pp));

		ChatLoggingManager.logMessage(ChannelType.HELP, context);
	}

	public void sendJoinMessage(CommandSource sender) throws InvalidContextError {
		sendJoinMessage(new Context(sender));
	}

	public void sendJoinMessage(ProxyChatContext context) throws InvalidContextError {
		context.require(ProxyChatContext.HAS_SENDER);

		String message = Format.JOIN_MESSAGE.getRaw(context);
		Predicate<ProxyChatAccount> predicate = getPermissionPredicate(Permission.MESSAGE_JOIN_VIEW);

		// This condition checks if the player is present and vanished
		if (context.getSender().filter(ProxyChatAccount::isVanished).isPresent()) {
			predicate = predicate.and(getPermissionPredicate(Permission.COMMAND_VANISH_VIEW));
		}

		context.setMessage(message);
		MessagesService.parseMessage(context, false);
		sendToMatchingPlayers(context.getParsedMessage(), predicate);

		ChatLoggingManager.logMessage("JOIN", context);
	}

	public void sendLeaveMessage(CommandSource sender) throws InvalidContextError {
		sendLeaveMessage(new Context(sender));
	}

	public void sendLeaveMessage(ProxyChatContext context) throws InvalidContextError {
		context.require(ProxyChatContext.HAS_SENDER);

		String message = Format.LEAVE_MESSAGE.getRaw(context);
		Predicate<ProxyChatAccount> predicate = getPermissionPredicate(Permission.MESSAGE_LEAVE_VIEW);

		// This condition checks if the player is present and vanished
		if (context.getSender().filter(ProxyChatAccount::isVanished).isPresent()) {
			predicate = predicate.and(getPermissionPredicate(Permission.COMMAND_VANISH_VIEW));
		}

		context.setMessage(message);
		MessagesService.parseMessage(context, false);
		sendToMatchingPlayers(context.getParsedMessage(), predicate);

		ChatLoggingManager.logMessage("LEAVE", context);
	}

	public void sendSwitchMessage(CommandSource sender, RegisteredServer server) throws InvalidContextError {
		ProxyChatContext context = new Context(sender);
		context.setServer(server);
		sendSwitchMessage(context);
	}

	public void sendSwitchMessage(ProxyChatContext context) throws InvalidContextError {
		context.require(ProxyChatContext.HAS_SENDER, ProxyChatContext.HAS_SERVER);

		String message = Format.SERVER_SWITCH.getRaw(context);
		Predicate<ProxyChatAccount> predicate = getPermissionPredicate(Permission.MESSAGE_SWITCH_VIEW);

		// This condition checks if the player is present and vanished
		if (context.getSender().filter(ProxyChatAccount::isVanished).isPresent()) {
			predicate = predicate.and(getPermissionPredicate(Permission.COMMAND_VANISH_VIEW));
		}

		context.setMessage(message);
		MessagesService.parseMessage(context, false);
		sendToMatchingPlayers(context.getParsedMessage(), predicate);

		ChatLoggingManager.logMessage("SWITCH", context);
	}

	public Optional<Component> preProcessMessage(ProxyChatContext context, Format format)
			throws InvalidContextError {
		return preProcessMessage(context, context.getSender(), format, true);
	}

	public Optional<Component> preProcessMessage(
			ProxyChatContext context,
			Optional<ProxyChatAccount> account,
			Format format,
			boolean runFilters) {
		return preProcessMessage(context, account, format, runFilters, false);
	}

	public Optional<Component> preProcessMessage(
			ProxyChatContext context,
			Optional<ProxyChatAccount> account,
			Format format,
			boolean runFilters,
			boolean ignoreBlockMessageExceptions)
			throws InvalidContextError {
		context.require(ProxyChatContext.HAS_MESSAGE);

		ProxyChatAccount playerAccount = account.get();
		CommandSource player = ProxyChatAccountManager.getCommandSource(playerAccount).get();
		Component message = PlaceHolderUtil.filterFormatting(context.getParsedMessage().get(), account.get());

		if (runFilters) {
			try {
				message = FilterManager.applyFilters(playerAccount, message);
			} catch (BlockMessageException e) {
				if (!ignoreBlockMessageExceptions) {
					MessagesService.sendMessage(player, e.getMessage());

					return Optional.empty();
				}
			}
		}

		context.setParsedMessage(message);

		return Optional.of(PlaceHolderUtil.getFullFormatMessage(format, context));
	}

	public boolean parseMessage(ProxyChatContext context, boolean runFilters) {
		context.require(ProxyChatContext.HAS_MESSAGE, ProxyChatContext.HAS_SENDER);

		ProxyChatAccount playerAccount = context.getSender().get();
		CommandSource player = ProxyChatAccountManager.getCommandSource(playerAccount).get();
		String message = context.getMessage().get();

		if(runFilters) {
			try {
				message = FilterManager.applyFilters(playerAccount, message);
			} catch (BlockMessageException e) {
				MessagesService.sendMessage(player, e.getMessage());

				return false;
			}
		}

		context.setParsedMessage(PlaceHolderUtil.filterFormatting(legacySerializer.deserialize(message), playerAccount));

		return true;
	}

	@SafeVarargs
	public void sendToMatchingPlayers(Optional<Component> finalMessage, Predicate<ProxyChatAccount>... playerFilters) {
		finalMessage.ifPresent(s -> sendToMatchingPlayers(s, playerFilters));
	}

	@SafeVarargs
	public void sendToMatchingPlayers(Component finalMessage, Predicate<ProxyChatAccount>... playerFilters) {
		Predicate<ProxyChatAccount> playerFiler =
				Arrays.stream(playerFilters).reduce(Predicate::and).orElse(acc -> true);

		AccountManager.getPlayerAccounts().stream()
				.filter(playerFiler)
				.forEach(account ->
								 ProxyChatAccountManager.getCommandSource(account).ifPresent(commandSource ->
																									  MessagesService.sendMessage(
																											  commandSource,
																											  finalMessage)));
	}

	public Predicate<ProxyChatAccount> getServerListPredicate(Config section) {
		if (!section.getBoolean("enabled")) return account -> true;
		else {
			// TODO: Use wildcard string
			List<String> allowedServers = section.getStringList("list");

			return account -> allowedServers.contains(account.getServerName());
		}
	}

	public Predicate<ProxyChatAccount> getGlobalPredicate() {
		return getServerListPredicate(
				ProxyChatModuleManager.GLOBAL_CHAT_MODULE.getModuleSection().getConfig("serverList"));
	}

	public Predicate<ProxyChatAccount> getServerPredicate(List<RegisteredServer> servers) {
		return account -> servers.contains(account.getServer().orElse(null));
	}

	public Predicate<ProxyChatAccount> getLocalPredicate(RegisteredServer server) {
		if (multiCastServerGroups == null) {
			return account -> server.equals(account.getServer().orElse(null));
		} else {
			return account -> {
				final RegisteredServer accountServer = account.getServer().orElse(null);
				final String accountServerName = account.getServerName();

				for (List<String> group : multiCastServerGroups) {
					if (group.contains(accountServerName)) {
						return group.contains(server.getServerInfo().getName());
					}
				}

				return server.equals(accountServer);
			};
		}
	}

	public Predicate<ProxyChatAccount> getLocalPredicate() {
		final Config serverList =
				ProxyChatModuleManager.LOCAL_CHAT_MODULE.getModuleSection().getConfig("serverList");
		final Config passThruServerList =
				ProxyChatModuleManager.LOCAL_CHAT_MODULE
						.getModuleSection()
						.getConfig("passThruServerList");

		return Stream.of(serverList, passThruServerList)
				.flatMap(MessagesService::serverListToPredicate)
				.reduce(Predicate::or).orElse(account -> true);
	}

	private Stream<Predicate<ProxyChatAccount>> serverListToPredicate(Config section) {
		if (section.getBoolean("enabled")) {
			// TODO: Use wildcard string
			List<String> allowedServers = section.getStringList("list");

			return Stream.of(account -> allowedServers.contains(account.getServerName()));
		} else {
			return Stream.empty();
		}
	}

	public Predicate<ProxyChatAccount> getPermissionPredicate(Permission permission) {
		return account -> account.hasPermission(permission);
	}

	public Predicate<ProxyChatAccount> getNotIgnoredPredicate(
			Optional<ProxyChatAccount> sender) {
		return getNotIgnoredPredicate(sender.get());
	}

	public Predicate<ProxyChatAccount> getNotIgnoredPredicate(ProxyChatAccount sender) {
		final IgnoringModule ignoringModule = ProxyChatModuleManager.IGNORING_MODULE;

		return (ignoringModule.isEnabled()
				&& ignoringModule.getModuleSection().getBoolean("ignoreChatMessages")
				&& !sender.hasPermission(Permission.BYPASS_IGNORE))
				? account -> !account.hasIgnored(sender)
				: account -> true;
	}

	public void sendMessage(CommandSource recipient, String message) {
		if ((message == null) || message.isEmpty()) return;

		recipient.sendMessage(Identity.nil(), LegacyComponentSerializer.builder().extractUrls().hexColors().build()
									  .deserialize(message));
	}

	public void sendMessage(CommandSource recipient, Component message) {
		if ((message == null)) return;

		recipient.sendMessage(Identity.nil(), message);
	}
}
