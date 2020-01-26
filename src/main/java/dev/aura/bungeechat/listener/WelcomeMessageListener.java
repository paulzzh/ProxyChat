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

public class WelcomeMessageListener {
  @Subscribe(order = PostOrder.LATE)
  public void onPlayerJoin(BungeeChatJoinEvent e) {
    Player player = e.getPlayer();

    BungeeChatAccount bungeeChatAccount = BungeecordAccountManager.getAccount(player).get();

    if (!BungeecordAccountManager.isNew(bungeeChatAccount.getUniqueId())) return;

    MessagesService.sendToMatchingPlayers(
        Format.WELCOME_MESSAGE.get(new BungeeChatContext(bungeeChatAccount)));
  }
}
