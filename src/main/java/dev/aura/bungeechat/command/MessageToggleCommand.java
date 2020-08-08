package dev.aura.bungeechat.command;

import com.velocitypowered.api.proxy.Player;
import dev.aura.bungeechat.account.BungeecordAccountManager;
import dev.aura.bungeechat.api.account.AccountManager;
import dev.aura.bungeechat.api.account.BungeeChatAccount;
import dev.aura.bungeechat.api.enums.AccountType;
import dev.aura.bungeechat.message.Messages;
import dev.aura.bungeechat.message.MessagesService;
import dev.aura.bungeechat.module.MessengerModule;
import dev.aura.bungeechat.permission.Permission;
import dev.aura.bungeechat.permission.PermissionManager;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MessageToggleCommand extends BaseCommand {
  public MessageToggleCommand(MessengerModule messengerModule) {
    super(
        "msgtoggle",
        Permission.COMMAND_TOGGLE_MESSAGE,
        messengerModule.getModuleSection().getStringList("aliases.msgtoggle"));
  }

  @Override
  public void execute(Invocation invocation) {
    if (!PermissionManager.hasPermission(invocation.source(), Permission.COMMAND_TOGGLE_MESSAGE)) return;

    if (invocation.arguments().length == 0) {
      if (!(invocation.source() instanceof Player)) {
        MessagesService.sendMessage(invocation.source(), Messages.NOT_A_PLAYER.get());
        return;
      }

      BungeeChatAccount player = BungeecordAccountManager.getAccount(invocation.source()).get();
      player.toggleMessanger();

      if (player.hasMessangerEnabled()) {
        MessagesService.sendMessage(invocation.source(), Messages.ENABLE_MESSAGER.get());
      } else {
        MessagesService.sendMessage(invocation.source(), Messages.DISABLE_MESSAGER.get());
      }
    } else if (invocation.arguments().length == 1) {
      if (!PermissionManager.hasPermission(invocation.source(), Permission.COMMAND_TOGGLE_MESSAGE_OTHERS))
        return;

      Optional<BungeeChatAccount> targetAccount = AccountManager.getAccount(invocation.arguments()[0]);

      if (targetAccount.map(target -> target.getAccountType() != AccountType.PLAYER).orElse(true)) {
        MessagesService.sendMessage(invocation.source(), Messages.PLAYER_NOT_FOUND.get());
        return;
      }

      targetAccount.get().toggleMessanger();

      if (targetAccount.get().hasMessangerEnabled()) {
        MessagesService.sendMessage(
            invocation.source(), Messages.ENABLE_MESSAGER_OTHERS.get(targetAccount.get()));
      } else {
        MessagesService.sendMessage(
            invocation.source(), Messages.DISABLE_MESSAGER_OTHERS.get(targetAccount.get()));
      }
    } else {
      MessagesService.sendMessage(
          invocation.source(), Messages.INCORRECT_USAGE.get(invocation.source(), "/msgtoggle [player]"));
    }
  }

  @Override
  public List<String> suggest(Invocation invocation) {
    if (invocation.arguments().length == 1) {
      final BungeeChatAccount senderAccount = BungeecordAccountManager.getAccount(invocation.source()).get();

      return BungeecordAccountManager.getAccountsForPartialName(invocation.arguments()[0], invocation.source()).stream()
          .filter(account -> account.getAccountType() == AccountType.PLAYER)
          .filter(account -> !senderAccount.equals(account))
          .map(BungeeChatAccount::getName)
          .collect(Collectors.toList());
    }

    return super.suggest(invocation);
  }
}
