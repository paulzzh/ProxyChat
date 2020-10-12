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

import uk.co.notnull.ProxyChat.account.ProxyChatAccountManager;
import uk.co.notnull.ProxyChat.api.account.ProxyChatAccount;
import uk.co.notnull.ProxyChat.api.enums.ChannelType;
import uk.co.notnull.ProxyChat.message.Messages;
import uk.co.notnull.ProxyChat.message.MessagesService;
import uk.co.notnull.ProxyChat.module.ProxyChatModuleManager;
import uk.co.notnull.ProxyChat.module.StaffChatModule;
import uk.co.notnull.ProxyChat.permission.Permission;
import uk.co.notnull.ProxyChat.permission.PermissionManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class StaffChatCommand extends BaseCommand {
  public StaffChatCommand(StaffChatModule staffChatModule) {
    super(
			"staffchat",
			Permission.COMMAND_STAFFCHAT,
			staffChatModule.getModuleSection().getStringList("aliases"));
  }

  @Override
  public void execute(Invocation invocation) {
    if (!PermissionManager.hasPermission(invocation.source(), Permission.COMMAND_STAFFCHAT)) return;

    if (invocation.arguments().length == 0) {
      ProxyChatAccount player = ProxyChatAccountManager.getAccount(invocation.source()).get();

      if (player.getChannelType() == ChannelType.STAFF) {
        ChannelType defaultChannelType = player.getDefaultChannelType();
        player.setChannelType(defaultChannelType);

        if (defaultChannelType == ChannelType.LOCAL) {
          MessagesService.sendMessage(invocation.source(), Messages.ENABLE_LOCAL.get());
        } else {
          MessagesService.sendMessage(invocation.source(), Messages.ENABLE_GLOBAL.get());
        }
      } else {
        player.setChannelType(ChannelType.STAFF);
        MessagesService.sendMessage(invocation.source(), Messages.ENABLE_STAFFCHAT.get());
      }
    } else {
      String finalMessage = Arrays.stream(invocation.arguments()).collect(Collectors.joining(" "));

      MessagesService.sendStaffMessage(invocation.source(), finalMessage);
    }
  }

  @Override
  public List<String> suggest(Invocation invocation) {
    if(invocation.arguments().length == 0) {
      return Collections.emptyList();
    }

    return ProxyChatModuleManager.EMOTE_MODULE.getEmoteSuggestions(invocation);
  }
}
