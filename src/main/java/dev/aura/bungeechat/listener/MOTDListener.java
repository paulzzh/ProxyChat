package dev.aura.bungeechat.listener;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.Player;
import dev.aura.bungeechat.account.BungeecordAccountManager;
import dev.aura.bungeechat.api.account.BungeeChatAccount;
import dev.aura.bungeechat.api.placeholder.BungeeChatContext;
import dev.aura.bungeechat.event.BungeeChatJoinEvent;
import dev.aura.bungeechat.message.Format;
import dev.aura.bungeechat.message.MessagesService;
import dev.aura.bungeechat.permission.Permission;
import dev.aura.bungeechat.permission.PermissionManager;

public class MOTDListener {
  @Subscribe(order = PostOrder.LATE)
  public void onPlayerJoin(BungeeChatJoinEvent e) {
    Player player = e.getPlayer();

    if (!PermissionManager.hasPermission(player, Permission.MESSAGE_MOTD)) return;

    BungeeChatAccount bungeeChatAccount = BungeecordAccountManager.getAccount(player).get();

    MessagesService.sendMessage(player, Format.MOTD.get(new BungeeChatContext(bungeeChatAccount)));
  }
}
