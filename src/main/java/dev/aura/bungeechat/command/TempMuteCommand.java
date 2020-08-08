package dev.aura.bungeechat.command;

import dev.aura.bungeechat.account.BungeecordAccountManager;
import dev.aura.bungeechat.api.account.AccountManager;
import dev.aura.bungeechat.api.account.BungeeChatAccount;
import dev.aura.bungeechat.api.enums.AccountType;
import dev.aura.bungeechat.api.utils.TimeUtil;
import dev.aura.bungeechat.message.Messages;
import dev.aura.bungeechat.message.MessagesService;
import dev.aura.bungeechat.module.MutingModule;
import dev.aura.bungeechat.permission.Permission;
import dev.aura.bungeechat.permission.PermissionManager;
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

    Optional<BungeeChatAccount> targetAccount = AccountManager.getAccount(invocation.arguments()[0]);

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
      return BungeecordAccountManager.getAccounts().stream()
          .filter(account -> account.getAccountType() == AccountType.PLAYER)
          .map(BungeeChatAccount::getName)
          .collect(Collectors.toList());
    }

    if (invocation.arguments().length == 1) {
      return BungeecordAccountManager.getAccountsForPartialName(invocation.arguments()[0], invocation.source()).stream()
          .filter(account -> account.getAccountType() == AccountType.PLAYER)
          .map(BungeeChatAccount::getName)
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
