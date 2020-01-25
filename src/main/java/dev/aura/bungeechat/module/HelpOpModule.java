package dev.aura.bungeechat.module;

import dev.aura.bungeechat.BungeeChat;
import dev.aura.bungeechat.command.HelpOpCommand;

public class HelpOpModule extends Module {
  private HelpOpCommand helpOpCommand;

  @Override
  public String getName() {
    return "HelpOp";
  }

  @Override
  public void onEnable() {
    helpOpCommand = new HelpOpCommand(this);
    helpOpCommand.register();
  }

  @Override
  public void onDisable() {
    helpOpCommand.unregister();
  }
}
