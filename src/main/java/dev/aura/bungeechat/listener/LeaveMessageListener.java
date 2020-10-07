package dev.aura.bungeechat.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.Player;
import dev.aura.bungeechat.event.BungeeChatLeaveEvent;
import dev.aura.bungeechat.message.MessagesService;
import dev.aura.bungeechat.permission.Permission;
import dev.aura.bungeechat.permission.PermissionManager;

public class LeaveMessageListener {
  @Subscribe
  public void onPlayerLeave(BungeeChatLeaveEvent e) {
    Player player = e.getPlayer();

    if (!PermissionManager.hasPermission(player, Permission.MESSAGE_LEAVE)) return;

    MessagesService.sendLeaveMessage(player);
  }
}
