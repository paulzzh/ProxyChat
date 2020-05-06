package dev.aura.bungeechat.event;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Called when a player has changed servers.
 *
 * <p>Used by BungeeChat internally to make sure people joining while they are online don't cause
 * issues.
 */
@Data
@RequiredArgsConstructor
@ToString(callSuper = false)
@EqualsAndHashCode(callSuper = false)
public class BungeeChatServerSwitchEvent {
  /** Player whom the server is for. */
  private final Player player;
  /** Server the player is switch from. */
  private final RegisteredServer from;
}
