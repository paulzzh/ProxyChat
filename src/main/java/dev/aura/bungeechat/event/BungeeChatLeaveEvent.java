package dev.aura.bungeechat.event;

import com.velocitypowered.api.proxy.Player;
import lombok.ToString;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Called when a player has left the proxy, it is not safe to call any methods that perform an
 * action on the passed player instance.
 *
 * <p>Used by BungeeChat internally to make sure people joining while they are online don't cause
 * issues.
 */
@Data
@ToString(callSuper = false)
@EqualsAndHashCode(callSuper = false)
public class BungeeChatLeaveEvent {
  /** Player disconnecting. */
  private final Player player;
}
