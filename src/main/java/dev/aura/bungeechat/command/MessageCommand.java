package dev.aura.bungeechat.command;

import com.velocitypowered.api.command.CommandSource;
import dev.aura.bungeechat.account.BungeecordAccountManager;
import dev.aura.bungeechat.api.account.AccountManager;
import dev.aura.bungeechat.api.account.BungeeChatAccount;
import dev.aura.bungeechat.message.Messages;
import dev.aura.bungeechat.message.MessagesService;
import dev.aura.bungeechat.module.MessengerModule;
import dev.aura.bungeechat.permission.Permission;
import dev.aura.bungeechat.permission.PermissionManager;
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
      Optional<BungeeChatAccount> targetAccount = AccountManager.getAccount(invocation.arguments()[0]);

      if (!targetAccount.isPresent()
          || (targetAccount.get().isVanished()
              && !PermissionManager.hasPermission(invocation.source(), Permission.COMMAND_VANISH_VIEW))) {
        MessagesService.sendMessage(invocation.source(), Messages.PLAYER_NOT_FOUND.get());
        return;
      }

      CommandSource target = BungeecordAccountManager.getCommandSource(targetAccount.get()).get();

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
    final BungeeChatAccount senderAccount = BungeecordAccountManager.getAccount(invocation.source()).get();

    if(invocation.arguments().length == 0) {
      return BungeecordAccountManager.getAccounts().stream()
          .filter(account -> !senderAccount.equals(account))
          .map(BungeeChatAccount::getName)
          .collect(Collectors.toList());
    }

    if (invocation.arguments().length == 1) {
      return BungeecordAccountManager.getAccountsForPartialName(invocation.arguments()[0], invocation.source()).stream()
          .filter(account -> !senderAccount.equals(account))
          .map(BungeeChatAccount::getName)
          .collect(Collectors.toList());
    }

    return super.suggest(invocation);
  }
}
