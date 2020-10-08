package dev.aura.bungeechat.filter;

import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.aura.bungeechat.api.account.BungeeChatAccount;
import dev.aura.bungeechat.api.filter.BlockMessageException;
import dev.aura.bungeechat.api.filter.BungeeChatPreParseFilter;
import dev.aura.bungeechat.api.filter.FilterManager;
import dev.aura.bungeechat.message.Messages;
import dev.aura.bungeechat.message.MessagesService;
import dev.aura.bungeechat.permission.Permission;
import dev.aura.bungeechat.permission.PermissionManager;

import java.util.LinkedList;
import java.util.List;

public class ChatLockFilter implements BungeeChatPreParseFilter {
  private boolean globalLock = false;
  private final List<RegisteredServer> lockedServers = new LinkedList<>();

  @Override
  public String applyFilter(BungeeChatAccount sender, String message) throws BlockMessageException {
    if (PermissionManager.hasPermission(sender, Permission.BYPASS_CHAT_LOCK)) {
      return message;
    }

    if(!((globalLock && MessagesService.getGlobalPredicate().test(sender)) ||
            sender.getServer().isPresent() && !lockedServers.contains(sender.getServer().get()))) {
      return message;
    }

    throw new ExtendedBlockMessageException(Messages.CHAT_IS_DISABLED, sender);
  }

  @Override
  public int getPriority() {
    return FilterManager.LOCK_CHAT_FILTER_PRIORITY;
  }

  public void enableGlobalChatLock() {
    globalLock = true;
  }

  public void enableLocalChatLock(RegisteredServer name) {
    lockedServers.add(name);
  }

  public boolean isGlobalChatLockEnabled() {
    return globalLock;
  }

  public boolean isLocalChatLockEnabled(RegisteredServer name) {
    return lockedServers.contains(name);
  }

  public void disableGlobalChatLock() {
    globalLock = false;
  }

  public void disableLocalChatLock(RegisteredServer name) {
    lockedServers.remove(name);
  }
}
