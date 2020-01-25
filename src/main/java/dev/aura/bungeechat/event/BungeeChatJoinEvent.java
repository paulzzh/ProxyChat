package dev.aura.bungeechat.event;

import com.google.common.base.Preconditions;
import com.velocitypowered.api.proxy.Player;

public class BungeeChatJoinEvent {

  private final Player player;

  public BungeeChatJoinEvent(Player player) {
    this.player = Preconditions.checkNotNull(player, "player");
  }

  public Player getPlayer() {
    return player;
  }

  @Override
  public String toString() {
    return "BungeeChatJoinEvent{"
        + "player=" + player
        + '}';
  }
}
