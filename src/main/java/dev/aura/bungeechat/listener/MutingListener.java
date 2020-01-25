package dev.aura.bungeechat.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import dev.aura.bungeechat.account.BungeecordAccountManager;
import dev.aura.bungeechat.api.account.BungeeChatAccount;
import dev.aura.bungeechat.api.enums.ChannelType;
import dev.aura.bungeechat.api.utils.ChatUtils;
import dev.aura.bungeechat.message.Messages;
import dev.aura.bungeechat.message.MessagesService;
import dev.aura.bungeechat.module.BungeecordModuleManager;
import java.util.List;

public class MutingListener {
  @Subscribe
    public void onPlayerChat(PlayerChatEvent e) {
    if (!e.getResult().isAllowed()) return;
    if (e.getPlayer() == null) return;

    Player sender = e.getPlayer();
    BungeeChatAccount account = BungeecordAccountManager.getAccount(sender).get();

    if (!account.isMuted()) return;

    final String message = e.getMessage();

    if (ChatUtils.isCommand(message)) {
      List<String> blockCommand =
          BungeecordModuleManager.MUTING_MODULE.getModuleSection().getStringList("blockedcommands");

      for (String s : blockCommand) {
        if (message.startsWith("/" + s + " ")) {
          MessagesService.sendMessage(sender, Messages.MUTED.get(account));
          e.setResult(PlayerChatEvent.ChatResult.denied());

          return;
        }
      }
    } else {
      final ChannelType channel = account.getChannelType();

      if (channel == ChannelType.STAFF) return;

      e.setResult(PlayerChatEvent.ChatResult.denied());
      MessagesService.sendMessage(sender, Messages.MUTED.get(account));
    }
  }
}
