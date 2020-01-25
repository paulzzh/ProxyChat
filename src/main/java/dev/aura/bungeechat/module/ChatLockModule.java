package dev.aura.bungeechat.module;

import dev.aura.bungeechat.api.filter.BungeeChatFilter;
import dev.aura.bungeechat.api.filter.FilterManager;
import dev.aura.bungeechat.command.ChatLockCommand;
import dev.aura.bungeechat.filter.ChatLockFilter;
import lombok.experimental.Delegate;

public class ChatLockModule extends Module {
  private ChatLockCommand chatLockCommand;

  @Delegate(excludes = BungeeChatFilter.class)
  private ChatLockFilter chatLockFilter;

  @Override
  public String getName() {
    return "ChatLock";
  }

  @Override
  public void onEnable() {
    chatLockCommand = new ChatLockCommand(this);
    chatLockFilter = new ChatLockFilter();

    chatLockCommand.register();
    FilterManager.addFilter(getName(), chatLockFilter);
  }

  @Override
  public void onDisable() {
    chatLockCommand.unregister();
    FilterManager.removeFilter(getName());
  }
}
