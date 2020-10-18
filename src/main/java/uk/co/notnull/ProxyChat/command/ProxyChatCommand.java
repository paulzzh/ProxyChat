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

import uk.co.notnull.ProxyChat.ProxyChat;
import uk.co.notnull.ProxyChat.account.ProxyChatAccountManager;
import uk.co.notnull.ProxyChat.api.ProxyChatApi;
import uk.co.notnull.ProxyChat.api.account.ProxyChatAccount;
import uk.co.notnull.ProxyChat.message.MessagesService;
import uk.co.notnull.ProxyChat.module.ProxyChatModuleManager;
import uk.co.notnull.ProxyChat.api.permission.Permission;
import uk.co.notnull.ProxyChat.permission.PermissionManager;
import uk.co.notnull.ProxyChat.util.LoggerHelper;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ProxyChatCommand extends BaseCommand {
  private final String prefix = "&9Proxy Chat &8// ";
  private static final List<String> arg1Completetions =
      Arrays.asList("modules", "reload");

  public ProxyChatCommand() {
    super("proxychat");
  }

  @Override
  public void execute(Invocation invocation) {
    if (invocation.arguments().length != 0) {
      if (invocation.arguments()[0].equalsIgnoreCase("reload")
          && PermissionManager.hasPermission(invocation.source(), Permission.PROXYCHAT_RELOAD)) {
        final ProxyChat instance = ProxyChat.getInstance();

        ProxyChat.getInstance().getProxy()
            .getScheduler()
            .buildTask(
                instance,
                () -> {
                  instance.onDisable();
                  instance.onEnable(false);

                  MessagesService.sendMessage(
                      invocation.source(), prefix + "&aThe plugin has been reloaded!");
                }).schedule();

        return;
      } else if (invocation.arguments()[0].equalsIgnoreCase("modules")
          && PermissionManager.hasPermission(invocation.source(), Permission.PROXYCHAT_MODULES)) {
        MessagesService.sendMessage(
            invocation.source(),
            prefix
                + "&7Active Modules: &a"
                + ProxyChatModuleManager.getActiveModuleString());
        return;
      }
    }

    MessagesService.sendMessage(
        invocation.source(),
        prefix
            + "&7Coded by &6"
            + ProxyChatApi.AUTHOR_BRAINSTONE
            + "&7 and &6"
            + ProxyChatApi.AUTHOR_SHAWN
            + ".");
  }

  @Override
  public List<String> suggest(Invocation invocation) {
    if(invocation.arguments().length == 0) {
      return arg1Completetions;
    }

    final String param1 = invocation.arguments()[0];

    if (invocation.arguments().length == 1 && !arg1Completetions.contains(invocation.arguments()[0])) {
      return arg1Completetions.stream()
          .filter(completion -> completion.startsWith(param1))
          .collect(Collectors.toList());
    } else if ((invocation.arguments().length == 2) && ("setprefix".equals(param1) || "setsuffix".equals(param1))) {
      return ProxyChatAccountManager.getAccountsForPartialName(invocation.arguments()[1], invocation.source()).stream()
          .map(ProxyChatAccount::getName)
          .collect(Collectors.toList());
    }

    return super.suggest(invocation);
  }

  private String getUnquotedString(String str) {
    if ((str == null) || !(str.startsWith("\"") && str.endsWith("\""))) return str;

    new StreamTokenizer(new StringReader(str));
    StreamTokenizer parser = new StreamTokenizer(new StringReader(str));
    String result;

    try {
      parser.nextToken();
      if (parser.ttype == '"') {
        result = parser.sval;
      } else {
        result = "ERROR!";
      }
    } catch (IOException e) {
      result = null;

      LoggerHelper.info("Encountered an IOException while parsing the input string", e);
    }

    return result;
  }
}
