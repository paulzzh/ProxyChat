//package dev.aura.bungeechat.command;
//
//import com.velocitypowered.api.proxy.Player;
//import dev.aura.bungeechat.account.BungeecordAccountManager;
//import dev.aura.bungeechat.api.account.BungeeChatAccount;
//import dev.aura.bungeechat.message.Messages;
//import dev.aura.bungeechat.message.MessagesService;
//import dev.aura.bungeechat.module.BungeecordModuleManager;
//import dev.aura.bungeechat.module.ChatLockModule;
//import dev.aura.bungeechat.permission.Permission;
//import dev.aura.bungeechat.permission.PermissionManager;
//import dev.aura.bungeechat.util.ServerNameUtil;
//import java.util.Collections;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//public class ChatLockCommand extends BaseCommand {
//  private static final String USAGE = "/chatlock <local [server]|global> [clear]";
//  private static final String CLEAR = "clear";
//
//  public ChatLockCommand(ChatLockModule chatLockModule) {
//    super(
//        "chatlock",
//        Permission.COMMAND_CHAT_LOCK,
//        chatLockModule.getModuleSection().getStringList("aliases"));
//  }
//
//  @Override
//  public void execute(Invocation invocation) {
//    // The permission check sends the no permission message
//    if (!PermissionManager.hasPermission(invocation.source(), Permission.COMMAND_CHAT_LOCK)) return;
//
//    BungeeChatAccount player = BungeecordAccountManager.getAccount(invocation.source()).get();
//
//    if ((invocation.arguments().length < 1) || (invocation.arguments().length > 2)) {
//      MessagesService.sendMessage(invocation.source(), Messages.INCORRECT_USAGE.get(player, USAGE));
//      return;
//    }
//
//    final ChatLockModule chatLock = BungeecordModuleManager.CHAT_LOCK_MODULE;
//    final boolean clear = (invocation.arguments().length >= 2)
//            && invocation.arguments()[invocation.arguments().length - 1].equalsIgnoreCase("clear");
//    final int emptyLines = clear ? chatLock.getModuleSection().getInt("emptyLinesOnClear") : 0;
//
//    if (invocation.arguments()[0].equalsIgnoreCase("global")) {
//      if (chatLock.isGlobalChatLockEnabled()) {
//        chatLock.disableGlobalChatLock();
//        MessagesService.sendMessage(invocation.source(), Messages.DISABLE_CHATLOCK.get(player));
//      } else {
//        chatLock.enableGlobalChatLock();
//
//        if (clear) {
//          ClearChatCommand.clearGlobalChat(emptyLines);
//        }
//
//        MessagesService.sendToMatchingPlayers(
//            Messages.ENABLE_CHATLOCK.get(player), MessagesService.getGlobalPredicate());
//      }
//    } else if (invocation.arguments()[0].equalsIgnoreCase("local")) {
//      boolean serverSpecified = invocation.arguments().length == (clear ? 3 : 2);
//
//      if (!serverSpecified && !(invocation.source() instanceof Player)) {
//        MessagesService.sendMessage(invocation.source(), Messages.INCORRECT_USAGE.get(player, USAGE));
//        return;
//      }
//
//      Optional<String> optServerName =
//          ServerNameUtil.verifyServerName(
//              serverSpecified ? invocation.arguments()[1] : player.getServerName(), invocation.source());
//
//      if (!optServerName.isPresent()) return;
//
//      String serverName = optServerName.get();
//
//      if (chatLock.isLocalChatLockEnabled(serverName)) {
//        chatLock.disableLocalChatLock(serverName);
//        MessagesService.sendMessage(invocation.source(), Messages.DISABLE_CHATLOCK.get(player));
//      } else {
//        chatLock.enableLocalChatLock(serverName);
//
//        if (clear) {
//          ClearChatCommand.clearLocalChat(serverName, emptyLines);
//        }
//
//        MessagesService.sendToMatchingPlayers(
//            Messages.ENABLE_CHATLOCK.get(player), MessagesService.getLocalPredicate(serverName));
//      }
//    } else {
//      MessagesService.sendMessage(invocation.source(), Messages.INCORRECT_USAGE.get(player, USAGE));
//    }
//  }
//
//  @Override
//  public List<String> suggest(Invocation invocation) {
//    if(invocation.arguments().length == 0) {
//      return ClearChatCommand.arg1Completetions;
//    }
//
//    final String location = invocation.arguments()[0];
//
//    if (invocation.arguments().length == 1 && !ClearChatCommand.arg1Completetions.contains(location)) {
//      return ClearChatCommand.arg1Completetions.stream()
//          .filter(completion -> completion.startsWith(location))
//          .collect(Collectors.toList());
//    } else if ((invocation.arguments().length == 2) && ClearChatCommand.arg1Completetions.contains(location)) {
//      final String param2 = invocation.arguments()[1];
//      final List<String> suggestions = new LinkedList<>();
//
//      if (CLEAR.startsWith(param2)) {
//        suggestions.add(CLEAR);
//      }
//
//      if ("local".equals(location)) {
//        suggestions.addAll(ServerNameUtil.getMatchingServerNames(param2));
//      }
//
//      return suggestions;
//    } else if ((invocation.arguments().length == 3)
//        && "local".equals(location)
//        && !CLEAR.equals(invocation.arguments()[1])
//        && CLEAR.startsWith(invocation.arguments()[2])) {
//      return Collections.singletonList(CLEAR);
//    }
//
//    return super.suggest(invocation);
//  }
//}
