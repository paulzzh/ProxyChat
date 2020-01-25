package dev.aura.bungeechat.module;

import dev.aura.bungeechat.BungeeChat;
import dev.aura.bungeechat.listener.MOTDListener;

public class MOTDModule extends Module {
  private MOTDListener motdListener;

  @Override
  public String getName() {
    return "MOTD";
  }

  @Override
  public void onEnable() {
    motdListener = new MOTDListener();

    BungeeChat.getInstance().getProxy()
        .getEventManager()
        .register(BungeeChat.getInstance(), motdListener);
  }

  @Override
  public void onDisable() {
    BungeeChat.getInstance().getProxy().getEventManager().unregisterListener(BungeeChat.getInstance(), motdListener);
  }
}
