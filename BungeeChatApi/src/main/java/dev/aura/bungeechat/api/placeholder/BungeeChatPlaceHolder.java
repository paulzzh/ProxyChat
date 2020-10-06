package dev.aura.bungeechat.api.placeholder;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

public interface BungeeChatPlaceHolder {
  public boolean isContextApplicable(BungeeChatContext context);

  public default boolean matchesName(String name) {
    return getName().equals(name);
  }

  public Component getReplacementComponent(String name, BungeeChatContext context);
  public String getReplacement(String name, BungeeChatContext context);

  public String getName();
}
