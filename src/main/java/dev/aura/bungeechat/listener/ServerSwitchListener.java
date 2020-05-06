package dev.aura.bungeechat.listener;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import dev.aura.bungeechat.event.BungeeChatServerSwitchEvent;
import dev.aura.bungeechat.message.MessagesService;
import dev.aura.bungeechat.permission.Permission;
import dev.aura.bungeechat.permission.PermissionManager;

public class ServerSwitchListener {
  @Subscribe(order = PostOrder.LATE)
  public void onPlayerServerSwitch(BungeeChatServerSwitchEvent e) {
    Player player = e.getPlayer();

    if (PermissionManager.hasPermission(player, Permission.MESSAGE_SWITCH)) {
      MessagesService.sendSwitchMessage(player, e.getFrom());
    }
  }
}
