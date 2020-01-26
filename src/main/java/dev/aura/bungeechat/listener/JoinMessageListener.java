package dev.aura.bungeechat.listener;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.Player;
import dev.aura.bungeechat.event.BungeeChatJoinEvent;
import dev.aura.bungeechat.message.MessagesService;
import dev.aura.bungeechat.permission.Permission;
import dev.aura.bungeechat.permission.PermissionManager;

public class JoinMessageListener {
  @Subscribe(order = PostOrder.LATE)
  public void onPlayerJoin(BungeeChatJoinEvent e) {
    Player player = e.getPlayer();

    if (!PermissionManager.hasPermission(player, Permission.MESSAGE_JOIN)) return;

    MessagesService.sendJoinMessage(player);
  }
}
