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
import uk.co.notnull.ProxyChat.api.utils.TimeUtil;
import uk.co.notnull.ProxyChat.message.Messages;
import uk.co.notnull.ProxyChat.message.MessagesService;
import uk.co.notnull.ProxyChat.module.MutingModule;
import uk.co.notnull.ProxyChat.permission.Permission;
import uk.co.notnull.ProxyChat.permission.PermissionManager;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TempMuteCommand extends BaseCommand {
  private static final List<String> timeUnits = Arrays.asList("s", "m", "h", "d", "w", "mo", "y");
  private static final Pattern digitsAndUnit =
      Pattern.compile("(\\d+(?:\\.\\d*)?)[a-z]*", Pattern.CASE_INSENSITIVE);

  public TempMuteCommand(MutingModule mutingModule) {
    super(
			"tempmute",
			Permission.COMMAND_TEMPMUTE,
			mutingModule.getModuleSection().getStringList("aliases.tempmute"));
  }

  @Override
  public void execute(Invocation invocation) {
    if (!PermissionManager.hasPermission(invocation.source(), Permission.COMMAND_TEMPMUTE)) return;

    if (invocation.arguments().length < 2) {
      MessagesService.sendMessage(
			  invocation.source(), Messages.INCORRECT_USAGE.get(invocation.source(), "/tempmute <player> <time>"));
      return;
    }

    Optional<ProxyChatAccount> targetAccount = AccountManager.getAccount(invocation.arguments()[0]);

    if (!targetAccount.isPresent()) {
      MessagesService.sendMessage(invocation.source(), Messages.PLAYER_NOT_FOUND.get());
      return;
    }

    if (targetAccount.get().isMuted()) {
      MessagesService.sendMessage(invocation.source(), Messages.MUTE_IS_MUTED.get());
      return;
    }

    final double timeAmount = TimeUtil.convertStringTimeToDouble(invocation.arguments()[1]);
    final double currentTime = System.currentTimeMillis();
    final java.sql.Timestamp timeStamp = new java.sql.Timestamp((long) (currentTime + timeAmount));
    targetAccount.get().setMutedUntil(timeStamp);
    MessagesService.sendMessage(invocation.source(), Messages.TEMPMUTE.get(targetAccount.get()));
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
    } else if (invocation.arguments().length == 2) {
      final String time = invocation.arguments()[1];
      String digits = null;

      Matcher match = digitsAndUnit.matcher(time);

      if (time.isEmpty()) {
        digits = "<duration>";
      } else if (match.matches()) {
        digits = match.group(1);
      }

      if (digits != null) {
        final String finalDigits = digits;

        return timeUnits.stream()
            .map(unit -> finalDigits + unit)
            .filter(timeStr -> timeStr.startsWith(time))
            .collect(Collectors.toList());
      }
    }

    return super.suggest(invocation);
  }
}
