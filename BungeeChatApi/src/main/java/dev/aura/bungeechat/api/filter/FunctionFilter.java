package dev.aura.bungeechat.api.filter;

import dev.aura.bungeechat.api.account.BungeeChatAccount;
import java.util.function.Function;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;

@RequiredArgsConstructor
public class FunctionFilter implements BungeeChatFilter {
  private final Function<Component, Component> filter;
  @Getter private final int priority;

  public FunctionFilter(Function<Component, Component> filter) {
    this(filter, 0);
  }

  @Override
  public Component applyFilter(BungeeChatAccount sender, Component message) {
    return filter.apply(message);
  }
}
