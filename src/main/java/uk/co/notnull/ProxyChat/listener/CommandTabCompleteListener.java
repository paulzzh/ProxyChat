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

package uk.co.notnull.ProxyChat.listener;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.TabCompleteEvent;
import uk.co.notnull.ProxyChat.api.utils.ChatUtils;
import uk.co.notnull.ProxyChat.command.BaseCommand;
import uk.co.notnull.ProxyChat.util.LoggerHelper;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class CommandTabCompleteListener {
  private Map<String, BaseCommand> proxyChatCommands = null;

  @Subscribe
  public void onTabComplete(TabCompleteEvent event) {
    final String message = event.getPartialMessage();

    if (!ChatUtils.isCommand(message)) return;

    final String[] allArgs = event.getPartialMessage().split(" ", -1);
    final String command = allArgs[0].substring(1);

    if (allArgs.length == 1) return;
    if (!proxyChatCommands.containsKey(command)) return;

    final BaseCommand commandHandler = proxyChatCommands.get(command);
    CommandSource sender = event.getPlayer();

    if (!commandHandler.hasPermission(sender, allArgs)) return;

    String[] args = Arrays.copyOfRange(allArgs, 1, allArgs.length);
    Collection<String> suggestions = null;

    try {
      suggestions = commandHandler.suggest(sender, args);
    } catch (RuntimeException e) {
      LoggerHelper.warning("Uncaught error during tabcomplete of /" + command, e);
    }

    if (suggestions != null) event.getSuggestions().addAll(suggestions);
  }

  public void updateProxyChatCommands() {
    proxyChatCommands = getProxyChatCommands();
  }

  private static Map<String, BaseCommand> getProxyChatCommands() {
    return Collections.emptyMap();
  }
}
