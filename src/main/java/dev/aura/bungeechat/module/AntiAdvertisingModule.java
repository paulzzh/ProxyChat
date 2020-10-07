package dev.aura.bungeechat.module;

import dev.aura.bungeechat.api.filter.FilterManager;
import dev.aura.bungeechat.filter.AdvertisingFilter;

public class AntiAdvertisingModule extends Module {
  @Override
  public String getName() {
    return "AntiAdvertising";
  }

  @Override
  public void onEnable() {
    FilterManager.addPreParseFilter(
        getName(), new AdvertisingFilter(getModuleSection().getStringList("whitelisted")));
  }

  @Override
  public void onDisable() {
    FilterManager.removePreParseFilter(getName());
  }
}
