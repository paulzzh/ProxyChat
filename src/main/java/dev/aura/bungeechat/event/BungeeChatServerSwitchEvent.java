package dev.aura.bungeechat.event;

import com.google.common.base.Preconditions;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

public class BungeeChatServerSwitchEvent {
  private final Player player;
  private final RegisteredServer server;

  public BungeeChatServerSwitchEvent(Player player) {
    this.player = Preconditions.checkNotNull(player, "player");
    this.server = null;
  }

  public BungeeChatServerSwitchEvent(Player player, RegisteredServer server) {
    this.player = Preconditions.checkNotNull(player, "player");
    this.server = Preconditions.checkNotNull(server, "server");
  }

  public Player getPlayer() {
    return player;
  }

  public RegisteredServer getServer() {
    return server;
  }

  @Override
  public String toString() {
    return "BungeeChatServerSwitchEvent{"
        + "player=" + player
        + ", server=" + server
        + '}';
  }
}
