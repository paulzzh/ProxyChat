package dev.aura.bungeechat.module;

import dev.aura.bungeechat.BungeeChat;
import dev.aura.bungeechat.command.LocalToCommand;

public class LocalToModule extends Module {

  private LocalToCommand localToCommand;

  @Override
  public String getName() {
    return "LocalTo";
  }

  @Override
  public void onEnable() {
    localToCommand = new LocalToCommand(this);
    localToCommand.register();
  }

  @Override
  public void onDisable() {
    localToCommand.unregister();
  }
}
