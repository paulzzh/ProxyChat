package dev.aura.bungeechat.module;

import dev.aura.bungeechat.BungeeChat;
import dev.aura.bungeechat.account.Account;
import dev.aura.bungeechat.api.enums.ChannelType;
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

    if (getModuleSection().getBoolean("default")
        || !BungeecordModuleManager.LOCAL_CHAT_MODULE.isEnabled()) {
      Account.staticSetDefaultChannelType(ChannelType.GLOBAL);
    }
  }

  @Override
  public void onDisable() {
    globalChatCommand.unregister();
    BungeeChat.getInstance().getProxy()
        .getEventManager()
        .unregisterListener(BungeeChat.getInstance(), globalChatListener);

    Account.staticSetDefaultChannelType(ChannelType.LOCAL);
  }
}
