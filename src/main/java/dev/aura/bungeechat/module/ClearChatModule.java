package dev.aura.bungeechat.module;

import dev.aura.bungeechat.command.ClearChatCommand;

public class ClearChatModule extends Module {
  private ClearChatCommand clearChatCommand;

  @Override
  public String getName() {
    return "ClearChat";
  }

  @Override
  public void onEnable() {
    clearChatCommand = new ClearChatCommand(this);
    clearChatCommand.register();
  }

  @Override
  public void onDisable() {
    clearChatCommand.unregister();
  }
}
