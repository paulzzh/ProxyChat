package dev.aura.bungeechat.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import dev.aura.bungeechat.account.BungeecordAccountManager;
import dev.aura.bungeechat.api.account.BungeeChatAccount;
import dev.aura.bungeechat.api.utils.ChatUtils;
import dev.aura.bungeechat.chatlog.ChatLoggingManager;

public class ChatLoggingListener {
  @Subscribe
  public void onPlayerChat(PlayerChatEvent e) {
    if (!e.getResult().isAllowed()) return;
    if (e.getPlayer() == null) return;

    BungeeChatAccount sender =
        BungeecordAccountManager.getAccount(e.getPlayer()).get();
    String message = e.getMessage();

    if (ChatUtils.isCommand(message)) {
      ChatLoggingManager.logCommand(sender, message);
    }
  }
}
