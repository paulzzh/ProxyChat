package dev.aura.bungeechat.module;

import dev.aura.bungeechat.BungeeChat;
import dev.aura.bungeechat.command.AlertCommand;

public class AlertModule extends Module {
  private AlertCommand alertCommand;

  @Override
  public String getName() {
    return "Alert";
  }

  @Override
  public void onEnable() {
    alertCommand = new AlertCommand(this);
    alertCommand.register();
  }

  @Override
  public void onDisable() {
    alertCommand.unregister();
  }
}
