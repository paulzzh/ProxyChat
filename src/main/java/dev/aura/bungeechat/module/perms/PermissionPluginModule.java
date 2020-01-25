package dev.aura.bungeechat.module.perms;

import dev.aura.bungeechat.BungeeChat;
import dev.aura.bungeechat.api.module.BungeeChatModule;

public abstract class PermissionPluginModule implements BungeeChatModule {
  @Override
  public boolean isEnabled() {
    return isPluginPresent(getName());
  }

  protected boolean isPluginPresent(String pluginName) {
    return BungeeChat.getInstance().getProxy().getPluginManager().getPlugin(pluginName).isPresent();
  }
}
