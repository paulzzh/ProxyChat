package dev.aura.bungeechat.module;

import dev.aura.bungeechat.BungeeChat;
import dev.aura.bungeechat.command.StaffChatCommand;
import dev.aura.bungeechat.listener.StaffChatListener;

public class StaffChatModule extends Module {
  private StaffChatCommand staffChatCommand;
  private StaffChatListener staffChatListener;

  @Override
  public String getName() {
    return "StaffChat";
  }

  @Override
  public void onEnable() {
    staffChatCommand = new StaffChatCommand(this);
    staffChatListener = new StaffChatListener();

    staffChatCommand.register();
    BungeeChat.getInstance().getProxy()
        .getEventManager()
        .register(BungeeChat.getInstance(), staffChatListener);
  }

  @Override
  public void onDisable() {
    staffChatCommand.unregister();
    BungeeChat.getInstance().getProxy().getEventManager().unregisterListener(BungeeChat.getInstance(), staffChatListener);
  }
}
