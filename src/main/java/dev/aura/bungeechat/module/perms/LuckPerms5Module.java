package dev.aura.bungeechat.module.perms;

import dev.aura.bungeechat.BungeeChat;
import dev.aura.bungeechat.api.hook.HookManager;
import dev.aura.bungeechat.config.Configuration;
import dev.aura.bungeechat.hook.LuckPerms5Hook;
import dev.aura.bungeechat.util.ClassUtil;

public class LuckPerms5Module extends PermissionPluginModule {
  private LuckPerms5Hook hook;

  @Override
  public String getName() {
    return "LuckPerms5";
  }

  @Override
  public boolean isEnabled() {
    return isPluginPresent("luckperms") && ClassUtil.doesClassExist("net.luckperms.api.LuckPerms");
  }

  @Override
  public void onEnable() {
    final boolean fixContext =
        Configuration.get().getBoolean("PrefixSuffixSettings.fixLuckPermsContext");

    LuckPerms5Hook hook = new LuckPerms5Hook(fixContext);
    BungeeChat.getInstance().getProxy()
        .getEventManager()
        .register(BungeeChat.getInstance(), hook);

    HookManager.addHook(getName(), hook);
  }

  @Override
  public void onDisable() {
    if(hook != null) {
      BungeeChat.getInstance().getProxy()
        .getEventManager()
        .unregisterListener(BungeeChat.getInstance(), hook);
    }
    HookManager.removeHook(getName());
  }
}
