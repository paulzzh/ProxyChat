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

import uk.co.notnull.ProxyChat.api.account.ProxyChatAccount;
import uk.co.notnull.ProxyChat.api.filter.ProxyChatPostParseFilter;
import uk.co.notnull.ProxyChat.api.filter.FilterManager;
import uk.co.notnull.ProxyChat.permission.Permission;
import uk.co.notnull.ProxyChat.permission.PermissionManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class EmoteFilter implements ProxyChatPostParseFilter {
	private final static Pattern emotePattern = Pattern.compile(":(\\w+):");
	private final static char emoteCharacter = '\ue110';
	private final List<String> emotes;
	private final boolean noPermissions;
	private final String prefix;

	public EmoteFilter(List<String> emotes, String prefix) {
		this(emotes, prefix, false);
	}

	public EmoteFilter(List<String> emotes, String prefix, boolean noPermissions) {
		this.prefix = prefix;
		this.emotes = emotes.stream().map(String::toLowerCase).collect(Collectors.toList());
		this.noPermissions = noPermissions;
	}

	@Override
	public Component applyFilter(ProxyChatAccount sender, Component message) {
		if(!noPermissions && PermissionManager.hasPermission(sender, Permission.USE_EMOTES)) {
			return message;
		}

		return message.replaceText(emotePattern, (TextComponent.Builder result) -> {
			String content = result.content();
			String emote = content.substring(1, content.length() - 1).replace(prefix, "").toLowerCase();
			int emoteIndex = emotes.indexOf(emote);
			String emoteChar = new String(Character.toChars(emoteCharacter + emoteIndex));

			if(emoteIndex > -1) {
				result.content(emoteChar);
				result.hoverEvent(Component.text(emoteChar + " " + emote)
										  .append(Component.newline())
										  .append(Component.text("Shift + Click to use",
																 Style.style().color(NamedTextColor.YELLOW).build()
										  )));
				result.insertion(":" + emote + ":");
			}

			return result;
		});
	}

	@Override
	public int getPriority() {
		return FilterManager.EMOTE_FILTER_PRIORITY;
	}
}
