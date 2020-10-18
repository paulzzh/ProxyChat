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
import uk.co.notnull.ProxyChat.api.account.ProxyChatAccount;
import uk.co.notnull.ProxyChat.message.Messages;
import uk.co.notnull.ProxyChat.message.MessagesService;
import uk.co.notnull.ProxyChat.module.MessengerModule;
import uk.co.notnull.ProxyChat.module.ProxyChatModuleManager;
import uk.co.notnull.ProxyChat.api.permission.Permission;
import uk.co.notnull.ProxyChat.permission.PermissionManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class ReplyCommand extends BaseCommand {
  private static HashMap<CommandSource, CommandSource> replies;

  public ReplyCommand(MessengerModule messengerModule) {
    super(
			"reply",
			Permission.COMMAND_MESSAGE,
			messengerModule.getModuleSection().getStringList("aliases.reply"));

    if (replies == null) {
      replies = new HashMap<>();
    } else {
      replies.clear();
    }
  }

  protected static void setReply(CommandSource sender, CommandSource target) {
    replies.put(sender, target);
    replies.put(target, sender);
  }

  private static CommandSource getReplier(CommandSource player) {
    return replies.getOrDefault(player, null);
  }

  @Override
  public void execute(Invocation invocation) {
    if (!PermissionManager.hasPermission(invocation.source(), Permission.COMMAND_MESSAGE)) return;

    if (invocation.arguments().length < 1) {
      MessagesService.sendMessage(invocation.source(), Messages.INCORRECT_USAGE
              .get(invocation.source(), "/reply <message>"));
      return;
    }

    Optional<ProxyChatAccount> targetAccount =
        ProxyChatAccountManager.getAccount(getReplier(invocation.source()));

    if (targetAccount.isEmpty()
        || (targetAccount.get().isVanished()
            && !PermissionManager.hasPermission(invocation.source(), Permission.COMMAND_VANISH_VIEW))) {
      MessagesService.sendMessage(invocation.source(), Messages.NO_REPLY.get());
      return;
    }

    CommandSource target = ProxyChatAccountManager.getCommandSource(targetAccount.get()).get();

    if (!targetAccount.get().hasMessangerEnabled()
        && !PermissionManager.hasPermission(invocation.source(), Permission.BYPASS_TOGGLE_MESSAGE)) {
      MessagesService.sendMessage(invocation.source(), Messages.HAS_MESSAGER_DISABLED.get(target));
      return;
    }

    String finalMessage = String.join(" ", invocation.arguments());

    MessagesService.sendPrivateMessage(invocation.source(), target, finalMessage);
    ReplyCommand.setReply(invocation.source(), target);
  }

  @Override
  public List<String> suggest(Invocation invocation) {
    if(invocation.arguments().length == 0) {
      return Collections.emptyList();
    }

    return ProxyChatModuleManager.EMOTE_MODULE.getEmoteSuggestions(invocation);
  }
}
