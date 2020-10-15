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

package uk.co.notnull.ProxyChat.filter;

import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextDecoration;
import uk.co.notnull.ProxyChat.ProxyChat;
import uk.co.notnull.ProxyChat.api.account.ProxyChatAccount;
import uk.co.notnull.ProxyChat.api.filter.ProxyChatPostParseFilter;
import uk.co.notnull.ProxyChat.api.filter.FilterManager;
import uk.co.notnull.ProxyChat.permission.Permission;
import uk.co.notnull.ProxyChat.permission.PermissionManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class EmoteFilter implements ProxyChatPostParseFilter {
	private final static Pattern emotePattern = Pattern.compile(":(\\w+):");
	private Pattern incompleteEmotePattern = Pattern.compile("(.*):(\\w+)$");

	private final TreeMap<String, Emote> emotesByName;
	private final Map<String, Emote> emotesByCharacter;
	private final Map<String, List<Emote>> emotesByCategory;
	private Component emotesList;

	private final boolean noPermissions;
	private final Pattern characterPattern;

	public EmoteFilter(Map<String, Map<String, List<String>>> categories) {
		this(categories, false);
	}

	public EmoteFilter(Map<String, Map<String, List<String>>> categories, boolean noPermissions) {
		StringBuilder characterRegex = new StringBuilder("[");
		emotesByName = new TreeMap<>();
		emotesByCharacter = new HashMap<>();
		emotesByCategory = new HashMap<>();

		categories.forEach((String category, Map<String, List<String>> emotes) -> {
			ArrayList<Emote> categoryEmotes = new ArrayList<>();

			emotes.forEach((String character, List<String> names) -> {
				characterRegex.append(character);
				Emote emote = new Emote(character, names, category);

				names.forEach(name -> this.emotesByName.put(name, emote));
				emotesByCharacter.put(character, emote);
				categoryEmotes.add(emote);
			});

			emotesByCategory.put(category, categoryEmotes);
		});

		characterRegex.append("]");
		characterPattern = Pattern.compile(characterRegex.toString());

		this.noPermissions = noPermissions;
	}

	@Override
	public Component applyFilter(ProxyChatAccount sender, Component message) {
		if(!noPermissions && PermissionManager.hasPermission(sender, Permission.USE_EMOTES)) {
			return message;
		}

		message = message.replaceText(emotePattern, (TextComponent.Builder result) -> {
			String content = result.content();
			String emoteName = content.substring(1, content.length() - 1).toLowerCase();

			Emote emote = emotesByName.get(emoteName);

			if(emote != null) {
				return emote.getComponent();
			}

			return result;
		});

		return message.replaceText(characterPattern, (TextComponent.Builder result) ->
				emotesByCharacter.get(result.content()).getComponent());
	}

	public List<String> getEmoteSuggestions(SimpleCommand.Invocation invocation) {
		String lastWord = invocation.arguments()[invocation.arguments().length - 1].toLowerCase();
		Matcher matcher = incompleteEmotePattern.matcher(lastWord);

		if(!matcher.find()) {
		  	return Collections.emptyList();
		}

		String prefix = matcher.group(1);

		List<String> results = searchEmotes(matcher.group(2))
				.stream()
				.map(emote -> prefix + emote.getCharacter())
				.collect(Collectors.toList());

		return results;
	}

	public List<Emote> searchEmotes(String search) {
		Map<String, Emote> results = emotesByName.subMap(search, search + Character.MAX_VALUE);

		return results.values().stream().distinct().collect(Collectors.toList());
	}

	public Component getEmotesListComponent() {
		if(emotesList != null) {
			return emotesList;
		}

		TextComponent.Builder list = Component.text().append(
				Component.text().content("Available Emotes")
						.color(NamedTextColor.YELLOW)
						.decoration(TextDecoration.UNDERLINED, TextDecoration.State.TRUE)
						.decoration(TextDecoration.BOLD, TextDecoration.State.TRUE))
				.append(Component.newline());

		emotesByCategory.forEach((String category, List<Emote> emotes) -> {
			if(emotes.isEmpty()) {
				return;
			}

			list.append(Component.text().content(category).color(NamedTextColor.BLUE));
			list.append(Component.newline());
			list.append(Component.space());

			emotes.forEach(emote -> list.append(emote.getComponent()).append(Component.space()));
			list.append(Component.newline());
		});

		emotesList = list.build();

		return emotesList;
	}

	@Override
	public int getPriority() {
		return FilterManager.EMOTE_FILTER_PRIORITY;
	}

	public static class Emote {
		private final List<String> names;
		private final String category;
		private final String character;
		private Component component;

		Emote(String character, List<String> names, String category) {
			this.character = character;
			this.names = names;
			this.category = category;
		}

		public List<String> getNames() {
			return names;
		}

		public String getCategory() {
			return category;
		}

		public String getCharacter() {
			return character;
		}

		private Component getComponent() {
			if(component != null) {
				return component;
			}

			this.component = Component.text().content(character)
					.hoverEvent(Component.text()
										.content(character + " " + names.get(0))
										.append(Component.newline())
										.append(Component.text(category,
											 Style.style().color(NamedTextColor.BLUE).build())
										)
										.append(Component.newline())
										.append(Component.text()
														.content(String.join(", ", names))
														.color(NamedTextColor.GRAY)
														.decoration(TextDecoration.ITALIC, TextDecoration.State.TRUE)
														.build())
										.append(Component.newline())
										.append(Component.text("Click to copy",
											 Style.style().color(NamedTextColor.YELLOW).build())
										)
										.append(Component.newline())
										.append(Component.text("Shift + Click to use",
											 Style.style().color(NamedTextColor.YELLOW).build())
										)
										.build()
					)
					.clickEvent(ClickEvent.copyToClipboard(character))
					.insertion(character)
					.build();

			return component;
		}
	}
}
