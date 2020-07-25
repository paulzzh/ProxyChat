package dev.aura.bungeechat.listener;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import com.typesafe.config.Config;
import dev.aura.bungeechat.account.BungeecordAccountManager;
import dev.aura.bungeechat.api.account.BungeeChatAccount;
import dev.aura.bungeechat.api.enums.ChannelType;
import dev.aura.bungeechat.api.utils.ChatUtils;
import dev.aura.bungeechat.message.Messages;
import dev.aura.bungeechat.message.MessagesService;
import dev.aura.bungeechat.module.BungeecordModuleManager;
import dev.aura.bungeechat.permission.Permission;
import dev.aura.bungeechat.permission.PermissionManager;

public class StaffChatListener {
  private final boolean passToBackendServer =
      BungeecordModuleManager.STAFF_CHAT_MODULE
          .getModuleSection()
          .getBoolean("passToBackendServer");
  private final Config symbolSection =
      BungeecordModuleManager.STAFF_CHAT_MODULE.getModuleSection().getConfig("symbol");

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
        if (!(PermissionManager.hasPermission(sender, Permission.COMMAND_STAFFCHAT))) {
          e.setResult(PlayerChatEvent.ChatResult.denied());
          return;
        }

        if (message.equals(symbol)) {
          MessagesService.sendMessage(sender, Messages.MESSAGE_BLANK.get());
          e.setResult(PlayerChatEvent.ChatResult.denied());
          return;
        }

        e.setResult(passToBackendServer ? PlayerChatEvent.ChatResult.allowed() : PlayerChatEvent.ChatResult.denied());
        MessagesService.sendStaffMessage(sender, message.substring(1));
      }
    }

    if (account.getChannelType() == ChannelType.STAFF) {
      e.setResult(passToBackendServer ? PlayerChatEvent.ChatResult.allowed() : PlayerChatEvent.ChatResult.denied());
      MessagesService.sendStaffMessage(sender, message);
    }
  }
}
