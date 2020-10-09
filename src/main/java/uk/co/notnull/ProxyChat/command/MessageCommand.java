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

import com.velocitypowered.api.command.CommandSource;
import uk.co.notnull.ProxyChat.account.ProxyChatAccountManager;
import uk.co.notnull.ProxyChat.api.account.AccountManager;
import uk.co.notnull.ProxyChat.api.account.ProxyChatAccount;
import uk.co.notnull.ProxyChat.message.Messages;
import uk.co.notnull.ProxyChat.message.MessagesService;
import uk.co.notnull.ProxyChat.module.MessengerModule;
import uk.co.notnull.ProxyChat.permission.Permission;
import uk.co.notnull.ProxyChat.permission.PermissionManager;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MessageCommand extends BaseCommand {
  public MessageCommand(MessengerModule messengerModule) {
    super(
			"msg",
			Permission.COMMAND_MESSAGE,
			messengerModule.getModuleSection().getStringList("aliases.message"));
  }

  @Override
  public void execute(Invocation invocation) {
    if (!PermissionManager.hasPermission(invocation.source(), Permission.COMMAND_MESSAGE)) return;

    if (invocation.arguments().length < 2) {
      MessagesService.sendMessage(
			  invocation.source(), Messages.INCORRECT_USAGE.get(invocation.source(), "/msg <player> <message>"));
    } else {
      Optional<ProxyChatAccount> targetAccount = AccountManager.getAccount(invocation.arguments()[0]);

      if (!targetAccount.isPresent()
          || (targetAccount.get().isVanished()
              && !PermissionManager.hasPermission(invocation.source(), Permission.COMMAND_VANISH_VIEW))) {
        MessagesService.sendMessage(invocation.source(), Messages.PLAYER_NOT_FOUND.get());
        return;
      }

      CommandSource target = ProxyChatAccountManager.getCommandSource(targetAccount.get()).get();

      if (target == invocation.source()) {
        MessagesService.sendMessage(invocation.source(), Messages.MESSAGE_YOURSELF.get());
        return;
      }
      if (!targetAccount.get().hasMessangerEnabled()
          && !PermissionManager.hasPermission(invocation.source(), Permission.BYPASS_TOGGLE_MESSAGE)) {
        MessagesService.sendMessage(invocation.source(), Messages.HAS_MESSAGER_DISABLED.get(target));
        return;
      }

      String finalMessage = Arrays.stream(invocation.arguments(), 1, invocation.arguments().length)
              .collect(Collectors.joining(" "));

      MessagesService.sendPrivateMessage(invocation.source(), target, finalMessage);
      ReplyCommand.setReply(invocation.source(), target);
    }
  }

  @Override
  public List<String> suggest(Invocation invocation) {
    final ProxyChatAccount senderAccount = ProxyChatAccountManager.getAccount(invocation.source()).get();

    if(invocation.arguments().length == 0) {
      return ProxyChatAccountManager.getAccounts().stream()
          .filter(account -> !senderAccount.equals(account))
          .map(ProxyChatAccount::getName)
          .collect(Collectors.toList());
    }

    if (invocation.arguments().length == 1) {
      return ProxyChatAccountManager.getAccountsForPartialName(invocation.arguments()[0], invocation.source()).stream()
          .filter(account -> !senderAccount.equals(account))
          .map(ProxyChatAccount::getName)
          .collect(Collectors.toList());
    }

    return super.suggest(invocation);
  }
}
