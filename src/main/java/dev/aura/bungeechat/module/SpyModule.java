package dev.aura.bungeechat.module;

import dev.aura.bungeechat.BungeeChat;
import dev.aura.bungeechat.command.LocalSpyCommand;
import dev.aura.bungeechat.command.SocialSpyCommand;

public class SpyModule extends Module {
  private SocialSpyCommand socialSpyCommand;
  private LocalSpyCommand localSpyCommand;

  @Override
  public String getName() {
    return "Spy";
  }

  @Override
  public void onEnable() {
    socialSpyCommand = new SocialSpyCommand(this);
    localSpyCommand = new LocalSpyCommand(this);

    socialSpyCommand.register();
    localSpyCommand.register();
  }

  @Override
  public void onDisable() {
    socialSpyCommand.unregister();
    localSpyCommand.unregister();
  }
}
