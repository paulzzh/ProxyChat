package dev.aura.bungeechat.listener;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import dev.aura.bungeechat.account.BungeecordAccountManager;
import dev.aura.bungeechat.api.enums.ChannelType;
import dev.aura.bungeechat.api.utils.ChatUtils;
import dev.aura.bungeechat.message.MessagesService;
import dev.aura.bungeechat.module.BungeecordModuleManager;

public class StaffChatListener {
  private final boolean passToClientServer =
      BungeecordModuleManager.STAFF_CHAT_MODULE.getModuleSection().getBoolean("passToClientServer");

  @Subscribe(order = PostOrder.LAST)
  public void onPlayerChat(PlayerChatEvent e) {
    if (!e.getResult().isAllowed()) return;
    if (e.getPlayer() == null) return;

    Player sender = e.getPlayer();
    String message = e.getMessage();

    if (ChatUtils.isCommand(message)) return;

    if (BungeecordAccountManager.getAccount(sender).get().getChannelType() == ChannelType.STAFF) {
      e.setResult(passToClientServer ? PlayerChatEvent.ChatResult.allowed() : PlayerChatEvent.ChatResult.denied());
      MessagesService.sendStaffMessage(sender, message);
    }
  }
}
