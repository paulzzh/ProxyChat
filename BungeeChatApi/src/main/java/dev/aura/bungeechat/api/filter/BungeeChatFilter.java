package dev.aura.bungeechat.api.filter;

import dev.aura.bungeechat.api.account.BungeeChatAccount;
import net.kyori.adventure.text.Component;

public interface BungeeChatFilter<T> extends Comparable<BungeeChatFilter<T>> {
  T applyFilter(BungeeChatAccount sender, T message) throws BlockMessageException;

  int getPriority();

  @Override
  default int compareTo(BungeeChatFilter other) {
    return getPriority() - other.getPriority();
  }
}
