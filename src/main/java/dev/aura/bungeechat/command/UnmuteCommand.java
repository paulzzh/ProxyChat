package dev.aura.bungeechat.command;

import dev.aura.bungeechat.account.BungeecordAccountManager;
import dev.aura.bungeechat.api.account.AccountManager;
import dev.aura.bungeechat.api.account.BungeeChatAccount;
import dev.aura.bungeechat.api.enums.AccountType;
import dev.aura.bungeechat.message.Messages;
import dev.aura.bungeechat.message.MessagesService;
import dev.aura.bungeechat.module.MutingModule;
import dev.aura.bungeechat.permission.Permission;
import dev.aura.bungeechat.permission.PermissionManager;

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
      Optional<BungeeChatAccount> targetAccount = AccountManager.getAccount(invocation.arguments()[0]);

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
    }

    return super.suggest(invocation);
  }
}
