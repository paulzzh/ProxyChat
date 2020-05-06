package dev.aura.bungeechat.listener;

import com.typesafe.config.Config;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import dev.aura.bungeechat.account.BungeecordAccountManager;
import dev.aura.bungeechat.api.account.BungeeChatAccount;
import dev.aura.bungeechat.api.enums.ChannelType;
import dev.aura.bungeechat.api.utils.ChatUtils;
import dev.aura.bungeechat.message.Messages;
import dev.aura.bungeechat.message.MessagesService;
import dev.aura.bungeechat.module.BungeecordModuleManager;
import dev.aura.bungeechat.permission.Permission;
import dev.aura.bungeechat.permission.PermissionManager;

public class GlobalChatListener {
  private final boolean passToBackendServer =
      BungeecordModuleManager.GLOBAL_CHAT_MODULE
          .getModuleSection()
          .getBoolean("passToBackendServer");

  private final Config symbolSection =
      BungeecordModuleManager.GLOBAL_CHAT_MODULE.getModuleSection().getConfig("symbol");

  @Subscribe(order = PostOrder.LAST)
  public void onPlayerChat(PlayerChatEvent e) {
    if (!e.getResult().isAllowed()) return;
    if (e.getPlayer() == null) return;

    Player sender = e.getPlayer();
    String message = e.getMessage();
    BungeeChatAccount account = BungeecordAccountManager.getAccount(sender).get();

    if (ChatUtils.isCommand(message)) return;

    if (symbolSection.getBoolean("enabled")) {
      String symbol = symbolSection.getString("symbol");

      if (message.startsWith(symbol) && !symbol.equals("/")) {
        if (!MessagesService.getGlobalPredicate().test(account)) {
          MessagesService.sendMessage(sender, Messages.NOT_IN_GLOBAL_SERVER.get());
          return;
        }

        if (!(PermissionManager.hasPermission(sender, Permission.COMMAND_GLOBAL))) {
          e.setResult(PlayerChatEvent.ChatResult.denied());
          return;
        }

        if (message.equals(symbol)) {
          MessagesService.sendMessage(sender, Messages.MESSAGE_BLANK.get());
          e.setResult(PlayerChatEvent.ChatResult.denied());
          return;
        }

        e.setResult(passToBackendServer ? PlayerChatEvent.ChatResult.allowed() : PlayerChatEvent.ChatResult.denied());
        MessagesService.sendGlobalMessage(sender, message.replaceFirst(symbol, ""));
      }
    }
  }
}
