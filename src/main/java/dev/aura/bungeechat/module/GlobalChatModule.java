package dev.aura.bungeechat.module;

import dev.aura.bungeechat.BungeeChat;
import dev.aura.bungeechat.command.GlobalChatCommand;
import dev.aura.bungeechat.listener.GlobalChatListener;

public class GlobalChatModule extends Module {
  private GlobalChatCommand globalChatCommand;
  private GlobalChatListener globalChatListener;

  @Override
  public String getName() {
    return "GlobalChat";
  }

  @Override
  public void onEnable() {
    globalChatCommand = new GlobalChatCommand(this);
    globalChatListener = new GlobalChatListener();

    globalChatCommand.register();
    BungeeChat.getInstance().getProxy()
        .getEventManager()
        .register(BungeeChat.getInstance(), globalChatListener);
  }

  @Override
  public void onDisable() {
    globalChatCommand.unregister();
    BungeeChat.getInstance().getProxy().getEventManager().unregisterListener(BungeeChat.getInstance(), globalChatListener);
  }
}
