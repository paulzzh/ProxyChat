package dev.aura.bungeechat.module;

import dev.aura.bungeechat.BungeeChat;
import dev.aura.bungeechat.command.LocalChatCommand;
import dev.aura.bungeechat.listener.LocalChatListener;

public class LocalChatModule extends Module {
  private LocalChatCommand localChatCommand;
  private LocalChatListener localChatListener;

  @Override
  public String getName() {
    return "LocalChat";
  }

  @Override
  public void onEnable() {
    localChatCommand = new LocalChatCommand(this);
    localChatListener = new LocalChatListener();

    localChatCommand.register();
    BungeeChat.getInstance().getProxy()
        .getEventManager()
        .register(BungeeChat.getInstance(), localChatListener);
  }

  @Override
  public void onDisable() {
    localChatCommand.unregister();
    BungeeChat.getInstance().getProxy().getEventManager().unregisterListener(BungeeChat.getInstance(), localChatListener);
  }
}
