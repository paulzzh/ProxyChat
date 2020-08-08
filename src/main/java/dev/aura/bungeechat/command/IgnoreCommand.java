package dev.aura.bungeechat.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import dev.aura.bungeechat.account.BungeecordAccountManager;
import dev.aura.bungeechat.api.account.AccountManager;
import dev.aura.bungeechat.api.account.BungeeChatAccount;
import dev.aura.bungeechat.message.Messages;
import dev.aura.bungeechat.message.MessagesService;
import dev.aura.bungeechat.module.IgnoringModule;
import dev.aura.bungeechat.permission.Permission;
import dev.aura.bungeechat.permission.PermissionManager;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class IgnoreCommand extends BaseCommand {
  private static final List<String> arg1Completetions = Arrays.asList("list", "add", "remove");

  public IgnoreCommand(IgnoringModule ignoringModule) {
    super(
        "ignore",
        Permission.COMMAND_IGNORE,
        ignoringModule.getModuleSection().getStringList("aliases"));
  }

  @Override
  public void execute(Invocation invocation) {
    if (!(invocation.source() instanceof Player)) {
      MessagesService.sendMessage(invocation.source(), Messages.NOT_A_PLAYER.get());
      return;
    }

    if (!PermissionManager.hasPermission(invocation.source(), Permission.COMMAND_IGNORE)) return;

    if (invocation.arguments().length < 1) {
      MessagesService.sendMessage(
          invocation.source(), Messages.INCORRECT_USAGE
                      .get(invocation.source(), "/ignore <list|add|remove> [player]"));
      return;
    }

    BungeeChatAccount player = BungeecordAccountManager.getAccount(invocation.source()).get();

    if (invocation.arguments()[0].equalsIgnoreCase("list")) {
      List<Optional<BungeeChatAccount>> ignored =
          player.getIgnored().stream()
              .map(AccountManager::getAccount)
              .filter(Optional::isPresent)
              .collect(Collectors.toList());

      if (ignored.size() <= 0) {
        MessagesService.sendMessage(invocation.source(), Messages.IGNORE_NOBODY.get(player));
      } else {
        String list =
            ignored.stream()
                .map(account -> account.get().getName())
                .collect(Collectors.joining(", "));

        MessagesService.sendMessage(invocation.source(), Messages.IGNORE_LIST.get(player, list));
      }
    } else if (invocation.arguments()[0].equalsIgnoreCase("add")) {
      if (invocation.arguments().length < 2) {
        MessagesService.sendMessage(
            invocation.source(), Messages.INCORRECT_USAGE.get(invocation.source(), "/ignore add <player>"));
        return;
      }

      Optional<BungeeChatAccount> targetAccount = AccountManager.getAccount(invocation.arguments()[1]);

      if (!targetAccount.isPresent()
          || (targetAccount.get().isVanished()
              && !PermissionManager.hasPermission(invocation.source(), Permission.COMMAND_VANISH_VIEW))) {
        MessagesService.sendMessage(invocation.source(), Messages.PLAYER_NOT_FOUND.get());
        return;
      }

      CommandSource target = BungeecordAccountManager.getCommandSource(targetAccount.get()).get();

      if (target == invocation.source()) {
        MessagesService.sendMessage(invocation.source(), Messages.IGNORE_YOURSELF.get());
        return;
      }

      if (player.hasIgnored(targetAccount.get().getUniqueId())) {
        MessagesService.sendMessage(invocation.source(), Messages.ALREADY_IGNORED.get());
        return;
      }

      player.addIgnore(targetAccount.get().getUniqueId());
      MessagesService.sendMessage(invocation.source(), Messages.ADD_IGNORE.get(target));
    } else if (invocation.arguments()[0].equalsIgnoreCase("remove")) {
      if (invocation.arguments().length < 2) {
        MessagesService.sendMessage(
            invocation.source(), Messages.INCORRECT_USAGE.get(invocation.source(), "/ignore remove <player>"));
        return;
      }

      Optional<BungeeChatAccount> targetAccount = AccountManager.getAccount(invocation.arguments()[1]);

      if (!targetAccount.isPresent()
          || (targetAccount.get().isVanished()
              && !PermissionManager.hasPermission(invocation.source(), Permission.COMMAND_VANISH_VIEW))) {
        MessagesService.sendMessage(invocation.source(), Messages.PLAYER_NOT_FOUND.get());
        return;
      }

      CommandSource target = BungeecordAccountManager.getCommandSource(targetAccount.get()).get();

      if (target == invocation.source()) {
        MessagesService.sendMessage(invocation.source(), Messages.UNIGNORE_YOURSELF.get());
        return;
      }

      if (!player.hasIgnored(targetAccount.get().getUniqueId())) {
        MessagesService.sendMessage(invocation.source(), Messages.NOT_IGNORED.get());
        return;
      }

      player.removeIgnore(targetAccount.get().getUniqueId());
      MessagesService.sendMessage(invocation.source(), Messages.REMOVE_IGNORE.get(target));
    } else {
      MessagesService.sendMessage(
          invocation.source(), Messages.INCORRECT_USAGE
                      .get(invocation.source(), "/ignore <list|add|remove> [player]"));
    }
  }

  @Override
  public List<String> suggest(Invocation invocation) {
    if(invocation.arguments().length == 0) {
      return arg1Completetions;
    }

    final String param1 = invocation.arguments()[0];

    if (invocation.arguments().length == 1 && !arg1Completetions.contains(invocation.arguments()[0])) {
      return arg1Completetions.stream()
              .filter(completion -> completion.startsWith(invocation.arguments()[0]))
              .collect(Collectors.toList());
    }

    if(invocation.arguments().length == 2 && ("add".equals(param1) || "remove".equals(param1))) {
      final BungeeChatAccount senderAccount = BungeecordAccountManager.getAccount(invocation.source()).get();

        return BungeecordAccountManager.getAccountsForPartialName(invocation.arguments()[1], invocation.source()).stream()
                .filter(account -> !senderAccount.equals(account))
                .map(BungeeChatAccount::getName)
                .collect(Collectors.toList());
    }

    return super.suggest(invocation.source(), invocation.arguments());
  }
}
