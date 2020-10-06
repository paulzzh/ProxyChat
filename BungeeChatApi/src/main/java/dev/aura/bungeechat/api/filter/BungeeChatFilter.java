package dev.aura.bungeechat.api.filter;

import dev.aura.bungeechat.api.account.BungeeChatAccount;
import net.kyori.adventure.text.Component;

public interface BungeeChatFilter extends Comparable<BungeeChatFilter> {
  public Component applyFilter(BungeeChatAccount sender, Component message) throws BlockMessageException;

  public int getPriority();

  @Override
  default int compareTo(BungeeChatFilter other) {
    return getPriority() - other.getPriority();
  }
}
