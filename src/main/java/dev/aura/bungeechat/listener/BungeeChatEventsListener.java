package dev.aura.bungeechat.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import dev.aura.bungeechat.BungeeChat;
import dev.aura.bungeechat.event.BungeeChatJoinEvent;
import dev.aura.bungeechat.event.BungeeChatLeaveEvent;
import dev.aura.bungeechat.event.BungeeChatServerSwitchEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class BungeeChatEventsListener {
  private static final List<UUID> joinedPlayers = new LinkedList<>();
  private static final List<UUID> duplicatePlayers = new LinkedList<>();

  @Subscribe
  public void onPlayerJoin(PostLoginEvent e) {
    UUID uuid = e.getPlayer().getUniqueId();

    if (!joinedPlayers.contains(uuid)) return;

    duplicatePlayers.add(uuid);
  }

  @Subscribe
  public void onPlayerServerSwitch(ServerConnectedEvent e) {
    Player player = e.getPlayer();
    UUID uuid = player.getUniqueId();

    if (!duplicatePlayers.contains(uuid)) {
      if (joinedPlayers.contains(uuid)) {
        BungeeChat.getInstance().getProxy().getEventManager().fireAndForget(new BungeeChatServerSwitchEvent(player));
      } else {
        joinedPlayers.add(uuid);

        BungeeChat.getInstance().getProxy().getEventManager().fireAndForget(new BungeeChatJoinEvent(player));
      }
    }
  }

  @Subscribe
  public void onPlayerLeave(DisconnectEvent e) {
    Player player = e.getPlayer();
    UUID uuid = player.getUniqueId();

    if (!joinedPlayers.contains(uuid)) return;

    if (duplicatePlayers.contains(uuid)) {
      duplicatePlayers.remove(uuid);
    } else {
      joinedPlayers.remove(uuid);

      BungeeChat.getInstance().getProxy().getEventManager().fireAndForget(new BungeeChatLeaveEvent(player));
    }
  }
}
