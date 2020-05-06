package dev.aura.bungeechat.event;

import com.velocitypowered.api.proxy.Player;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Event called as soon as a connection has a {@link Player} and is ready to be connected to
 * a server.
 *
 * <p>Used by BungeeChat internally to make sure people joining while they are online don't cause
 * issues.
 */
@Data
@ToString(callSuper = false)
@EqualsAndHashCode(callSuper = false)
public class BungeeChatJoinEvent {
  /** The player involved with this event. */
  private final Player player;
}
