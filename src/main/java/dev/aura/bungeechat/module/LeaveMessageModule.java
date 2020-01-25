package dev.aura.bungeechat.module;

import dev.aura.bungeechat.BungeeChat;
import dev.aura.bungeechat.listener.LeaveMessageListener;

public class LeaveMessageModule extends Module {
  private LeaveMessageListener leaveMessageListener;

  @Override
  public String getName() {
    return "LeaveMessage";
  }

  @Override
  public void onEnable() {
    leaveMessageListener = new LeaveMessageListener();

    BungeeChat.getInstance().getProxy()
        .getEventManager()
        .register(BungeeChat.getInstance(), leaveMessageListener);
  }

  @Override
  public void onDisable() {
    BungeeChat.getInstance().getProxy().getEventManager().unregisterListener(BungeeChat.getInstance(), leaveMessageListener);
  }
}
