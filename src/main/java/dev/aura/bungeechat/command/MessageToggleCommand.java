package dev.aura.bungeechat.command;

import com.velocitypowered.api.command.CommandSource;
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
import java.util.Collection;
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
  public void execute(CommandSource sender, String[] args) {
    if (!PermissionManager.hasPermission(sender, Permission.COMMAND_TOGGLE_MESSAGE)) return;

    if (args.length == 0) {
      if (!(sender instanceof Player)) {
        MessagesService.sendMessage(sender, Messages.NOT_A_PLAYER.get());
        return;
      }

      BungeeChatAccount player = BungeecordAccountManager.getAccount(sender).get();
      player.toggleMessanger();

      if (player.hasMessangerEnabled()) {
        MessagesService.sendMessage(sender, Messages.ENABLE_MESSAGER.get());
      } else {
        MessagesService.sendMessage(sender, Messages.DISABLE_MESSAGER.get());
      }
    } else if (args.length == 1) {
      if (!PermissionManager.hasPermission(sender, Permission.COMMAND_TOGGLE_MESSAGE_OTHERS))
        return;

      Optional<BungeeChatAccount> targetAccount = AccountManager.getAccount(args[0]);

      if (targetAccount.map(target -> target.getAccountType() != AccountType.PLAYER).orElse(true)) {
        MessagesService.sendMessage(sender, Messages.PLAYER_NOT_FOUND.get());
        return;
      }

      targetAccount.get().toggleMessanger();

      if (targetAccount.get().hasMessangerEnabled()) {
        MessagesService.sendMessage(
            sender, Messages.ENABLE_MESSAGER_OTHERS.get(targetAccount.get()));
      } else {
        MessagesService.sendMessage(
            sender, Messages.DISABLE_MESSAGER_OTHERS.get(targetAccount.get()));
      }
    } else {
      MessagesService.sendMessage(
          sender, Messages.INCORRECT_USAGE.get(sender, "/msgtoggle [player]"));
    }
  }

  @Override
  public List<String> suggest(CommandSource sender, String[] args) {
    if (args.length == 1) {
      final BungeeChatAccount senderAccount = BungeecordAccountManager.getAccount(sender).get();

      return BungeecordAccountManager.getAccountsForPartialName(args[0], sender).stream()
          .filter(account -> account.getAccountType() == AccountType.PLAYER)
          .filter(account -> !senderAccount.equals(account))
          .map(BungeeChatAccount::getName)
          .collect(Collectors.toList());
    }

    return super.suggest(sender, args);
  }
}
