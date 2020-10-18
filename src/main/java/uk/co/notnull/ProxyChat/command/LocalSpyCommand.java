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

import com.velocitypowered.api.proxy.Player;
import uk.co.notnull.ProxyChat.account.ProxyChatAccountManager;
import uk.co.notnull.ProxyChat.api.account.ProxyChatAccount;
import uk.co.notnull.ProxyChat.message.Messages;
import uk.co.notnull.ProxyChat.message.MessagesService;
import uk.co.notnull.ProxyChat.module.SpyModule;
import uk.co.notnull.ProxyChat.api.permission.Permission;
import uk.co.notnull.ProxyChat.permission.PermissionManager;

public class LocalSpyCommand extends BaseCommand {
  public LocalSpyCommand(SpyModule socialSpyModule) {
    super(
        "localspy",
        Permission.COMMAND_LOCALSPY,
        socialSpyModule.getModuleSection().getStringList("aliases.localspy"));
  }

  @Override
  public void execute(Invocation invocation) {
    if (PermissionManager.hasPermission(invocation.source(), Permission.COMMAND_LOCALSPY)) {
      if (!(invocation.source() instanceof Player)) {
        MessagesService.sendMessage(invocation.source(), Messages.NOT_A_PLAYER.get());
      } else {
        ProxyChatAccount player = ProxyChatAccountManager.getAccount(invocation.source()).get();
        player.toggleLocalSpy();

        if (player.hasLocalSpyEnabled()) {
          MessagesService.sendMessage(invocation.source(), Messages.ENABLE_LOCAL_SPY.get(player));
        } else {
          MessagesService.sendMessage(invocation.source(), Messages.DISABLE_LOCAL_SPY.get(player));
        }
      }
    }
  }
}
