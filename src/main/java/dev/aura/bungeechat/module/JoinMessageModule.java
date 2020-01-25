package dev.aura.bungeechat.module;

import dev.aura.bungeechat.BungeeChat;
import dev.aura.bungeechat.listener.JoinMessageListener;

public class JoinMessageModule extends Module {
  private JoinMessageListener joinMessageListener;

  @Override
  public String getName() {
    return "JoinMessage";
  }

  @Override
  public void onEnable() {
    joinMessageListener = new JoinMessageListener();

    BungeeChat.getInstance().getProxy()
        .getEventManager()
        .register(BungeeChat.getInstance(), joinMessageListener);
  }

  @Override
  public void onDisable() {
    BungeeChat.getInstance().getProxy().getEventManager().unregisterListener(BungeeChat.getInstance(), joinMessageListener);
  }
}
