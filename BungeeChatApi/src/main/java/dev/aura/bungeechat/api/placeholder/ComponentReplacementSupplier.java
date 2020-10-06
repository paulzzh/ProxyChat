package dev.aura.bungeechat.api.placeholder;

import net.kyori.adventure.text.Component;

public interface ComponentReplacementSupplier {
  public Component get(BungeeChatContext context);
}
