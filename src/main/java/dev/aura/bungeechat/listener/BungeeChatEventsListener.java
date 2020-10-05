package dev.aura.bungeechat.listener;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import dev.aura.bungeechat.BungeeChat;
import dev.aura.bungeechat.event.BungeeChatJoinEvent;
import dev.aura.bungeechat.event.BungeeChatLeaveEvent;
import dev.aura.bungeechat.event.BungeeChatServerSwitchEvent;

public class BungeeChatEventsListener {
  @Subscribe(order = PostOrder.EARLY)
  public void onPlayerServerSwitch(PlayerChooseInitialServerEvent e) {
    Player player = e.getPlayer();

    BungeeChat.getInstance().getProxy().getEventManager().fireAndForget(new BungeeChatJoinEvent(player));
  }

  @Subscribe(order = PostOrder.LATE)
  public void onPlayerServerSwitch(ServerConnectedEvent e) {
    Player player = e.getPlayer();

    if(e.getPlayer().getCurrentServer().isPresent()) {
      BungeeChat.getInstance().getProxy().getEventManager().fireAndForget(new BungeeChatServerSwitchEvent(player, e.getPlayer().getCurrentServer().get().getServer()));
    }
  }

  @Subscribe(order = PostOrder.LATE)
  public void onPlayerLeave(DisconnectEvent e) {
    Player player = e.getPlayer();

    if(e.getPlayer().getCurrentServer().isPresent()) {
      BungeeChat.getInstance().getProxy().getEventManager().fireAndForget(new BungeeChatLeaveEvent(player));
    }
  }
}
