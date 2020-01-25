package dev.aura.bungeechat.module;

import dev.aura.bungeechat.BungeeChat;
import dev.aura.bungeechat.listener.WelcomeMessageListener;

public class WelcomeMessageModule extends Module {
  private WelcomeMessageListener welcomeMessageListener;

  @Override
  public String getName() {
    return "WelcomeMessage";
  }

  @Override
  public void onEnable() {
    welcomeMessageListener = new WelcomeMessageListener();

    BungeeChat.getInstance().getProxy()
        .getEventManager()
        .register(BungeeChat.getInstance(), welcomeMessageListener);
  }

  @Override
  public void onDisable() {
    BungeeChat.getInstance().getProxy().getEventManager()
            .unregisterListener(BungeeChat.getInstance(), welcomeMessageListener);
  }
}
