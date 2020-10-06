package dev.aura.bungeechat.command;

import com.velocitypowered.api.proxy.Player;
import dev.aura.bungeechat.account.BungeecordAccountManager;
import dev.aura.bungeechat.api.account.BungeeChatAccount;
import dev.aura.bungeechat.message.Messages;
import dev.aura.bungeechat.message.MessagesService;
import dev.aura.bungeechat.module.BungeecordModuleManager;
import dev.aura.bungeechat.module.ClearChatModule;
import dev.aura.bungeechat.permission.Permission;
import dev.aura.bungeechat.permission.PermissionManager;
import dev.aura.bungeechat.util.ServerNameUtil;
import net.kyori.adventure.text.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ClearChatCommand extends BaseCommand {
  // package because only neighboring class needs it
  static final List<String> arg1Completetions = Arrays.asList("local", "global");

  private static final String USAGE = "/clearchat <local [server]|global>";
  private static final Component EMPTY_LINE = Component.newline();

  public ClearChatCommand(ClearChatModule clearChatModule) {
    super(
        "clearchat",
        Permission.COMMAND_CLEAR_CHAT,
        clearChatModule.getModuleSection().getStringList("aliases"));
  }

  @Override
  public void execute(Invocation invocation) {
    if (!PermissionManager.hasPermission(invocation.source(), Permission.COMMAND_CLEAR_CHAT)) return;

    if ((invocation.arguments().length < 1) || (invocation.arguments().length > 3)) {
      MessagesService.sendMessage(invocation.source(), Messages.INCORRECT_USAGE.get(invocation.source(), USAGE));
    } else {

      final int lines =
          BungeecordModuleManager.CLEAR_CHAT_MODULE.getModuleSection().getInt("emptyLines");
      final BungeeChatAccount bungeeChatAccount = BungeecordAccountManager.getAccount(invocation.source()).get();

      if (invocation.arguments()[0].equalsIgnoreCase("local")) {
        boolean serverSpecified = invocation.arguments().length == 2;

          if (!serverSpecified && !(invocation.source() instanceof Player)) {
            MessagesService.sendMessage(
                invocation.source(), Messages.INCORRECT_USAGE.get(bungeeChatAccount, USAGE));
            return;
          }

        Optional<String> optServerName =
            ServerNameUtil.verifyServerName(
                serverSpecified ? invocation.arguments()[1] : bungeeChatAccount.getServerName(), invocation.source());

        if (!optServerName.isPresent()) return;

        String serverName = optServerName.get();

        clearLocalChat(serverName, lines);

        MessagesService.sendToMatchingPlayers(
            Messages.CLEARED_LOCAL.get(invocation.source()), MessagesService.getLocalPredicate(serverName));
      } else if (invocation.arguments()[0].equalsIgnoreCase("global")) {
        clearGlobalChat(lines);

        MessagesService.sendToMatchingPlayers(
            Messages.CLEARED_GLOBAL.get(invocation.source()), MessagesService.getGlobalPredicate());
      } else {
        MessagesService.sendMessage(invocation.source(), Messages.INCORRECT_USAGE.get(invocation.source(), USAGE));
      }
    }
  }

  @Override
  public List<String> suggest(Invocation invocation) {
    if (invocation.arguments().length == 0) {
      return arg1Completetions;
    }

    final String location = invocation.arguments()[0];

    if (invocation.arguments().length == 1 && !arg1Completetions.contains(location)) {
      return arg1Completetions.stream()
          .filter(completion -> completion.startsWith(location))
          .collect(Collectors.toList());
    } else if ((invocation.arguments().length == 2) && ("local".equals(location))) {
      final String serverName = invocation.arguments()[1];

      return ServerNameUtil.getMatchingServerNames(serverName);
    }

    return super.suggest(invocation);
  }

  public static void clearGlobalChat(int emptyLines) {
    clearChat(emptyLines, MessagesService.getGlobalPredicate());
  }

  public static void clearLocalChat(String serverName, int emptyLines) {
    clearChat(emptyLines, MessagesService.getLocalPredicate(serverName));
  }

  private static void clearChat(int emptyLines, Predicate<BungeeChatAccount> predicate) {
    for (int i = 0; i < emptyLines; i++) {
      MessagesService.sendToMatchingPlayers(EMPTY_LINE, predicate);
    }
  }
}
