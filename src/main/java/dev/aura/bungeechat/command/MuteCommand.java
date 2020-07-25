package dev.aura.bungeechat.command;

import com.velocitypowered.api.command.CommandSource;
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

public class MuteCommand extends BaseCommand {
  public MuteCommand(MutingModule mutingModule) {
    super(
        "mute",
        Permission.COMMAND_MUTE,
        mutingModule.getModuleSection().getStringList("aliases.mute"));
  }

  @Override
  public void execute(CommandSource sender, String[] args) {
    if (!PermissionManager.hasPermission(sender, Permission.COMMAND_MUTE)) return;

    if (args.length < 1) {
      MessagesService.sendMessage(sender, Messages.INCORRECT_USAGE.get(sender, "/mute <player>"));
      return;
    }

    Optional<BungeeChatAccount> targetAccount = AccountManager.getAccount(args[0]);

    if (!targetAccount.isPresent()) {
      MessagesService.sendMessage(sender, Messages.PLAYER_NOT_FOUND.get());
      return;
    }

    if (targetAccount.get().isMuted()) {
      MessagesService.sendMessage(sender, Messages.MUTE_IS_MUTED.get());
      return;
    }

    targetAccount.get().mutePermanetly();
    MessagesService.sendMessage(sender, Messages.MUTE.get(targetAccount.get()));
  }

  @Override
  public List<String> suggest(CommandSource sender, String[] args) {
    if(args.length == 0) {
      return BungeecordAccountManager.getAccounts().stream()
          .filter(account -> account.getAccountType() == AccountType.PLAYER)
          .map(BungeeChatAccount::getName)
          .collect(Collectors.toList());
    }

    if (args.length == 1) {
      return BungeecordAccountManager.getAccountsForPartialName(args[0], sender).stream()
          .filter(account -> account.getAccountType() == AccountType.PLAYER)
          .map(BungeeChatAccount::getName)
          .collect(Collectors.toList());
    }

    return super.suggest(sender, args);
  }
}
