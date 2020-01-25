package dev.aura.bungeechat.module;

import dev.aura.bungeechat.BungeeChat;
import dev.aura.bungeechat.listener.ServerSwitchListener;

public class ServerSwitchModule extends Module {
  private ServerSwitchListener serverSwitchListener;

  @Override
  public String getName() {
    return "ServerSwitchMessages";
  }

  @Override
  public void onEnable() {
    serverSwitchListener = new ServerSwitchListener();

    BungeeChat.getInstance().getProxy()
        .getEventManager()
        .register(BungeeChat.getInstance(), serverSwitchListener);
  }

  @Override
  public void onDisable() {
    BungeeChat.getInstance().getProxy().getEventManager().unregisterListener(BungeeChat.getInstance(), serverSwitchListener);
  }
}
