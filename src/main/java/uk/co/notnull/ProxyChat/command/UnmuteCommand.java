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
import uk.co.notnull.ProxyChat.api.account.AccountManager;
import uk.co.notnull.ProxyChat.api.account.ProxyChatAccount;
import uk.co.notnull.ProxyChat.api.enums.AccountType;
import uk.co.notnull.ProxyChat.message.Messages;
import uk.co.notnull.ProxyChat.message.MessagesService;
import uk.co.notnull.ProxyChat.module.MutingModule;
import uk.co.notnull.ProxyChat.permission.Permission;
import uk.co.notnull.ProxyChat.permission.PermissionManager;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UnmuteCommand extends BaseCommand {
  public UnmuteCommand(MutingModule mutingModule) {
    super(
			"unmute",
			Permission.COMMAND_UNMUTE,
			mutingModule.getModuleSection().getStringList("aliases.unmute"));
  }

  @Override
  public void execute(Invocation invocation) {
    if (!PermissionManager.hasPermission(invocation.source(), Permission.COMMAND_UNMUTE)) return;

    if (invocation.arguments().length < 1) {
      MessagesService.sendMessage(invocation.source(), Messages.INCORRECT_USAGE.get(invocation.source(), "/unmute <player>"));
    } else {
      Optional<ProxyChatAccount> targetAccount = AccountManager.getAccount(invocation.arguments()[0]);

      if (targetAccount.isEmpty()) {
        MessagesService.sendMessage(invocation.source(), Messages.PLAYER_NOT_FOUND.get());
        return;
      }

      if (!targetAccount.get().isMuted()) {
        MessagesService.sendMessage(invocation.source(), Messages.UNMUTE_NOT_MUTED.get());
        return;
      }

      targetAccount.get().unmute();
      MessagesService.sendMessage(invocation.source(), Messages.UNMUTE.get(targetAccount.get()));
    }
  }

  @Override
  public List<String> suggest(Invocation invocation) {
    if(invocation.arguments().length == 0) {
      return ProxyChatAccountManager.getAccounts().stream()
          .filter(account -> account.getAccountType() == AccountType.PLAYER)
          .map(ProxyChatAccount::getName)
          .collect(Collectors.toList());
    }

    if (invocation.arguments().length == 1) {
      return ProxyChatAccountManager.getAccountsForPartialName(invocation.arguments()[0], invocation.source()).stream()
          .filter(account -> account.getAccountType() == AccountType.PLAYER)
          .map(ProxyChatAccount::getName)
          .collect(Collectors.toList());
    }

    return super.suggest(invocation);
  }
}
