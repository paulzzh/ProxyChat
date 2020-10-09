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

import uk.co.notnull.ProxyChat.message.Messages;
import uk.co.notnull.ProxyChat.message.MessagesService;
import uk.co.notnull.ProxyChat.module.HelpOpModule;
import uk.co.notnull.ProxyChat.permission.Permission;
import uk.co.notnull.ProxyChat.permission.PermissionManager;

public class HelpOpCommand extends BaseCommand {
  public HelpOpCommand(HelpOpModule helpOpModule) {
    super(
			"helpop",
			Permission.COMMAND_HELPOP,
			helpOpModule.getModuleSection().getStringList("aliases"));
  }

  @Override
  public void execute(Invocation invocation) {
    if (PermissionManager.hasPermission(invocation.source(), Permission.COMMAND_HELPOP)) {
      if (invocation.arguments().length < 1) {
        MessagesService.sendMessage(
				invocation.source(), Messages.INCORRECT_USAGE.get(invocation.source(), "/helpop <message>"));
      } else {
        String finalMessage = String.join(" ", invocation.arguments());

        MessagesService.sendHelpMessage(invocation.source(), finalMessage);
      }
    }
  }
}
