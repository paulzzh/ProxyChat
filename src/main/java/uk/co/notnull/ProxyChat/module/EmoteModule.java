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

package uk.co.notnull.ProxyChat.module;

import lombok.experimental.Delegate;
import uk.co.notnull.ProxyChat.api.filter.FilterManager;
import uk.co.notnull.ProxyChat.api.filter.ProxyChatFilter;
import uk.co.notnull.ProxyChat.command.EmotesCommand;
import uk.co.notnull.ProxyChat.filter.EmoteFilter;

import java.util.List;
import java.util.Map;

public class EmoteModule extends Module {
	@Delegate(excludes = ProxyChatFilter.class)
  	private EmoteFilter emoteFilter;

	private EmotesCommand emotesCommand;

	@Override
	public String getName() {
		return "Emotes";
	}

	@Override
	public void onEnable() {
		emotesCommand = new EmotesCommand(this);

		emoteFilter = new EmoteFilter(
				(Map<String, Map<String, List<String>>>) getModuleSection().getAnyRef("emotes"));

		FilterManager.addPostParseFilter(getName(), emoteFilter);

		emotesCommand.register();
	}

	@Override
	public void onDisable() {
		emotesCommand.unregister();
		FilterManager.removePostParseFilter(getName());
	}
}
