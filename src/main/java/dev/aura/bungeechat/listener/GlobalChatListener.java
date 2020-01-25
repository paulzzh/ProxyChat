package dev.aura.bungeechat.listener;

import com.typesafe.config.Config;
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

public class GlobalChatListener {
  private final boolean passToClientServer =
      BungeecordModuleManager.GLOBAL_CHAT_MODULE
          .getModuleSection()
          .getBoolean("passToClientServer");

  @Subscribe
  public void onPlayerChat(PlayerChatEvent e) {
    if (!e.getResult().isAllowed()) return;
    if (e.getPlayer() == null) return;

    Player sender = e.getPlayer();
    String message = e.getMessage();
    BungeeChatAccount account = BungeecordAccountManager.getAccount(sender).get();

    if (ChatUtils.isCommand(message)) return;

    if (account.getChannelType() == ChannelType.STAFF) return;

    if (BungeecordModuleManager.GLOBAL_CHAT_MODULE.getModuleSection().getBoolean("default")) {
      if (MessagesService.getGlobalPredicate().test(account)) {
        e.setResult(passToClientServer ? PlayerChatEvent.ChatResult.allowed() : PlayerChatEvent.ChatResult.denied());
        MessagesService.sendGlobalMessage(sender, message);
        return;
      }
    }

    if (BungeecordAccountManager.getAccount(sender).get().getChannelType() == ChannelType.GLOBAL) {
      if (!MessagesService.getGlobalPredicate().test(account)) {
        MessagesService.sendMessage(sender, Messages.NOT_IN_GLOBAL_SERVER.get());

        return;
      }

      e.setResult(passToClientServer ? PlayerChatEvent.ChatResult.allowed() : PlayerChatEvent.ChatResult.denied());
      MessagesService.sendGlobalMessage(sender, message);

      return;
    }

    Config section =
        BungeecordModuleManager.GLOBAL_CHAT_MODULE.getModuleSection().getConfig("symbol");

    if (section.getBoolean("enabled")) {
      String symbol = section.getString("symbol");

      if (message.startsWith(symbol) && !symbol.equals("/")) {
        if (!MessagesService.getGlobalPredicate().test(account)) {
          MessagesService.sendMessage(sender, Messages.NOT_IN_GLOBAL_SERVER.get());

          return;
        }

        e.setResult(passToClientServer ? PlayerChatEvent.ChatResult.allowed() : PlayerChatEvent.ChatResult.denied());
        MessagesService.sendGlobalMessage(sender, message.replaceFirst(symbol, ""));
      }
    }
  }
}
