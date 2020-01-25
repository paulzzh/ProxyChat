package dev.aura.bungeechat.event;

import com.google.common.base.Preconditions;
import com.velocitypowered.api.proxy.Player;

public class BungeeChatLeaveEvent {
  private final Player player;

  public BungeeChatLeaveEvent(Player player) {
    this.player = Preconditions.checkNotNull(player, "player");
  }

  public Player getPlayer() {
    return player;
  }

  @Override
  public String toString() {
    return "BungeeChatLeaveEvent{"
            + "player=" + player
            + '}';
  }
}
