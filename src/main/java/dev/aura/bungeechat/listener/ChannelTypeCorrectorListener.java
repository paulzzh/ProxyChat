package dev.aura.bungeechat.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import dev.aura.bungeechat.api.account.AccountManager;
import dev.aura.bungeechat.api.account.BungeeChatAccount;
import dev.aura.bungeechat.api.enums.ChannelType;
import dev.aura.bungeechat.api.module.ModuleManager;
import dev.aura.bungeechat.message.Messages;
import dev.aura.bungeechat.message.MessagesService;
import dev.aura.bungeechat.module.BungeecordModuleManager;
import dev.aura.bungeechat.permission.Permission;
import dev.aura.bungeechat.permission.PermissionManager;
import java.util.Optional;

public class ChannelTypeCorrectorListener {
  @Subscribe
  public void onPlayerChat(PlayerChatEvent e) {
    if (!e.getResult().isAllowed()) return;
    if (e.getPlayer() == null) return;

    Player player = e.getPlayer();
    Optional<BungeeChatAccount> bungeeChatAccountOptional =
        AccountManager.getAccount(player.getUniqueId());
    ChannelType c = bungeeChatAccountOptional.get().getChannelType();

    if ((c.equals(ChannelType.GLOBAL)
            && (!ModuleManager.isModuleActive(BungeecordModuleManager.GLOBAL_CHAT_MODULE)
                || !PermissionManager.hasPermission(player, Permission.COMMAND_GLOBAL)))
        || (c.equals(ChannelType.STAFF)
            && (!ModuleManager.isModuleActive(BungeecordModuleManager.STAFF_CHAT_MODULE)
                || !PermissionManager.hasPermission(player, Permission.COMMAND_STAFFCHAT)))) {
      e.setResult(PlayerChatEvent.ChatResult.denied());
      bungeeChatAccountOptional.get().setChannelType(ChannelType.LOCAL);
      MessagesService.sendMessage(player, Messages.BACK_TO_LOCAL.get());
    }
  }
}
