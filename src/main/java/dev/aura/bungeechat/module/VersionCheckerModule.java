package dev.aura.bungeechat.module;

import dev.aura.bungeechat.BungeeChat;

public class VersionCheckerModule extends Module {
  private VersionCheckerListener versionCheckerListener;

  @Override
  public String getName() {
    return "VersionChecker";
  }

  @Override
  public void onEnable() {
    versionCheckerListener =
        new VersionCheckerListener(getModuleSection().getBoolean("checkOnAdminLogin"));

    BungeeChat.getInstance().getProxy()
        .getEventManager()
        .register(BungeeChat.getInstance(), versionCheckerListener);
  }

  @Override
  public void onDisable() {
    BungeeChat.getInstance().getProxy().getEventManager().unregisterListener(BungeeChat.getInstance(), versionCheckerListener);
  }
}
