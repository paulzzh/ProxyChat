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

package uk.co.notnull.ProxyChat.command;

import net.kyori.adventure.identity.Identity;
import uk.co.notnull.ProxyChat.module.EmoteModule;
import uk.co.notnull.ProxyChat.api.permission.Permission;
import uk.co.notnull.ProxyChat.permission.PermissionManager;
import uk.co.notnull.ProxyChat.util.ServerNameUtil;

import java.util.Collections;
import java.util.List;

public class EmotesCommand extends BaseCommand {
  private final EmoteModule emoteModule;

  public EmotesCommand(EmoteModule emoteModule) {
    super("emotes", Permission.COMMAND_EMOTES, Collections.emptyList());
    this.emoteModule = emoteModule;
  }

  @Override
  public void execute(Invocation invocation) {
    if (!PermissionManager.hasPermission(invocation.source(), Permission.COMMAND_EMOTES)) return;

    invocation.source().sendMessage(Identity.nil(), emoteModule.getEmotesListComponent());
  }

  @Override
  public List<String> suggest(Invocation invocation) {
    if(invocation.arguments().length == 0) {
      return ServerNameUtil.getServerNames();
    }

    return super.suggest(invocation);
  }
}
