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
import uk.co.notnull.ProxyChat.api.filter.FilterManager;
import uk.co.notnull.ProxyChat.api.filter.ProxyChatPreParseFilter;
import uk.co.notnull.ProxyChat.api.permission.Permission;
import uk.co.notnull.ProxyChat.module.EmoteModule;

import java.util.Locale;

public class EmoteFilter implements ProxyChatPreParseFilter {
	private final EmoteModule module;
	private final boolean noPermissions;

	public EmoteFilter(EmoteModule module) {
		this(module, false);
	}

	public EmoteFilter(EmoteModule module, boolean noPermissions) {
		this.module = module;
		this.noPermissions = noPermissions;
	}

	@Override
	public String applyFilter(ProxyChatAccount sender, String message) {
		if(!noPermissions && sender.hasPermission(Permission.USE_EMOTES)) {
			return message;
		}

		message = module.getEmotePattern().matcher(message).replaceAll(matcher -> {
			String match = matcher.group(1).toLowerCase(Locale.ROOT);
			return module.getEmoteByName(match).map(emote -> ":" + emote.getPrimaryName() + ":").orElse(matcher.group());
		});

		return module.getCharacterPattern().matcher(message)
				.replaceAll(matcher -> module.getEmoteByCharacter(matcher.group())
						.map(emote -> ":" + emote.getPrimaryName() + ":")
						.orElse(matcher.group()));
	}

	@Override
	public int getPriority() {
		return FilterManager.EMOTE_FILTER_PRIORITY;
	}
}
