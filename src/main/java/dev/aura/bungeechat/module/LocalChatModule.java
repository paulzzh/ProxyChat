package dev.aura.bungeechat.module;

import dev.aura.bungeechat.BungeeChat;
import dev.aura.bungeechat.listener.LocalChatListener;

public class LocalChatModule extends Module {
  private LocalChatListener localChatListener;

  @Override
  public String getName() {
    return "LocalChat";
  }

  @Override
  public void onEnable() {
    localChatListener = new LocalChatListener();

    BungeeChat.getInstance().getProxy()
        .getEventManager()
        .register(BungeeChat.getInstance(), localChatListener);
  }

  @Override
  public void onDisable() {
    BungeeChat.getInstance().getProxy().getEventManager().unregisterListener(BungeeChat.getInstance(), localChatListener);
  }
}
