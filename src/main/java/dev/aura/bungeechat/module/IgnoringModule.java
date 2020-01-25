package dev.aura.bungeechat.module;

import dev.aura.bungeechat.BungeeChat;
import dev.aura.bungeechat.command.IgnoreCommand;

public class IgnoringModule extends Module {
  private IgnoreCommand ignoreCommand;

  @Override
  public String getName() {
    return "Ignoring";
  }

  @Override
  public void onEnable() {
    ignoreCommand = new IgnoreCommand(this);
    ignoreCommand.register();
  }

  @Override
  public void onDisable() {
    ignoreCommand.unregister();
  }
}
