package dev.aura.bungeechat.module;

import dev.aura.bungeechat.BungeeChat;
import dev.aura.bungeechat.listener.MulticastChatListener;

public class MulticastChatModule extends Module {
  private MulticastChatListener multicastListener;

  @Override
  public String getName() {
    return "MulticastChat";
  }

  @Override
  public void onEnable() {
    multicastListener = new MulticastChatListener();

    BungeeChat.getInstance().getProxy()
        .getEventManager()
        .register(BungeeChat.getInstance(), multicastListener);
  }

  @Override
  public void onDisable() {
    BungeeChat.getInstance().getProxy().getEventManager().unregisterListener(BungeeChat.getInstance(), multicastListener);
  }
}
