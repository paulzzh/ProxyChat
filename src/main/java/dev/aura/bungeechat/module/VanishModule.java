package dev.aura.bungeechat.module;

import dev.aura.bungeechat.BungeeChat;
import dev.aura.bungeechat.command.VanishCommand;

public class VanishModule extends Module {
  private VanishCommand vanishCommand;

  @Override
  public String getName() {
    return "Vanish";
  }

  @Override
  public void onEnable() {
    vanishCommand = new VanishCommand(this);
    vanishCommand.register();
  }

  @Override
  public void onDisable() {
    vanishCommand.unregister();
  }
}
