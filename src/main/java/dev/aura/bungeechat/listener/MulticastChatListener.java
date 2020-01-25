package dev.aura.bungeechat.listener;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigList;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import dev.aura.bungeechat.account.BungeecordAccountManager;
import dev.aura.bungeechat.api.account.BungeeChatAccount;
import dev.aura.bungeechat.api.enums.ChannelType;
import dev.aura.bungeechat.api.utils.ChatUtils;
import dev.aura.bungeechat.message.Context;
import dev.aura.bungeechat.message.MessagesService;
import dev.aura.bungeechat.module.BungeecordModuleManager;
import java.util.List;
import java.util.stream.Collectors;

public class MulticastChatListener {
  private final Config serverListsSection =
      BungeecordModuleManager.MULTICAST_CHAT_MODULE.getModuleSection().getConfig("serverLists");
  private final boolean serverListsEnabled = serverListsSection.getBoolean("enabled");
  private final ConfigList serverLists = serverListsSection.getList("lists");

  @SuppressWarnings("unchecked")
  private final List<List<String>> serverGroups =
      serverLists.stream()
          .map(configValue -> (List<String>) configValue.unwrapped())
          .collect(Collectors.toList());

  @Subscribe
  public void onPlayerChat(PlayerChatEvent e) {
    if (!e.getResult().isAllowed()) return;
    if (e.getPlayer() == null) return;

    Player sender = e.getPlayer();
    BungeeChatAccount account = BungeecordAccountManager.getAccount(sender).get();
    String message = e.getMessage();

    if (ChatUtils.isCommand(message)) return;

    if (account.getChannelType() == ChannelType.LOCAL && serverListsEnabled) {
      sendMessageToServers(sender, account, message);
    }
  }

  private void sendMessageToServers(
      Player sender, BungeeChatAccount account, String message) {
    for (List<String> group : serverGroups) {
      if (group.contains(account.getServerName())) {
        MessagesService.sendMulticastMessage(new Context(sender, message), group);
        return;
      }
    }
  }
}
