package dev.aura.bungeechat.listener;

import com.velocitypowered.api.event.PostOrder;
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
  @Subscribe(order = PostOrder.LATE)
  public void onPlayerChat(PlayerChatEvent e) {
    if (!e.getResult().isAllowed()) return;
    if (e.getPlayer() == null) return;

    Player sender = e.getPlayer();
    BungeeChatAccount player = AccountManager.getAccount(sender.getUniqueId()).get();
    ChannelType channel = player.getChannelType();

    if (((channel == ChannelType.GLOBAL)
            && (!ModuleManager.isModuleActive(BungeecordModuleManager.GLOBAL_CHAT_MODULE)
                || !PermissionManager.hasPermission(sender, Permission.COMMAND_GLOBAL)))
        || ((channel == ChannelType.LOCAL)
            && (!ModuleManager.isModuleActive(BungeecordModuleManager.LOCAL_CHAT_MODULE)
                || !PermissionManager.hasPermission(sender, Permission.COMMAND_LOCAL)))
        || ((channel == ChannelType.STAFF)
            && (!ModuleManager.isModuleActive(BungeecordModuleManager.STAFF_CHAT_MODULE)
                || !PermissionManager.hasPermission(sender, Permission.COMMAND_STAFFCHAT)))) {

      e.setResult(PlayerChatEvent.ChatResult.denied());
      ChannelType defaultChannel = player.getDefaultChannelType();

      if (((defaultChannel == ChannelType.GLOBAL)
              && PermissionManager.hasPermissionNoMessage(sender, Permission.COMMAND_GLOBAL))
          || ((defaultChannel == ChannelType.LOCAL)
              && PermissionManager.hasPermissionNoMessage(sender, Permission.COMMAND_LOCAL))) {
        player.setChannelType(defaultChannel);
        MessagesService.sendMessage(sender, Messages.BACK_TO_DEFAULT.get());
      }
    }
  }
}
