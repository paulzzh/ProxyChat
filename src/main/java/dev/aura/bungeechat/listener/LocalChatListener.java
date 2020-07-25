package dev.aura.bungeechat.listener;

import com.typesafe.config.Config;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import dev.aura.bungeechat.account.BungeecordAccountManager;
import dev.aura.bungeechat.api.account.BungeeChatAccount;
import dev.aura.bungeechat.api.enums.ChannelType;
import dev.aura.bungeechat.api.utils.ChatUtils;
import dev.aura.bungeechat.message.Context;
import dev.aura.bungeechat.message.Messages;
import dev.aura.bungeechat.message.MessagesService;
import dev.aura.bungeechat.module.BungeecordModuleManager;
import java.util.List;

public class LocalChatListener {
   private final boolean passToBackendServer = BungeecordModuleManager.LOCAL_CHAT_MODULE
          .getModuleSection()
          .getBoolean("passToBackendServer");
  private final boolean passTransparently =
      BungeecordModuleManager.LOCAL_CHAT_MODULE.getModuleSection().getBoolean("passTransparently");
  private final boolean logTransparentLocal =
      BungeecordModuleManager.LOCAL_CHAT_MODULE
          .getModuleSection()
          .getBoolean("logTransparentLocal");
  private final Config serverListSection =
      BungeecordModuleManager.LOCAL_CHAT_MODULE.getModuleSection().getConfig("passThruServerList");
  private final boolean serverListDisabled = !serverListSection.getBoolean("enabled");
  private final List<String> passthruServers = serverListSection.getStringList("list");

  @Subscribe(order = PostOrder.LAST)
  public void onPlayerChat(PlayerChatEvent e) {
    if (!e.getResult().isAllowed()) return;
    if (e.getPlayer() == null) return;

    Player sender = e.getPlayer();
    BungeeChatAccount account = BungeecordAccountManager.getAccount(sender).get();
    String message = e.getMessage();

    if (ChatUtils.isCommand(message)) return;

    if (account.getChannelType() == ChannelType.LOCAL) {
      if (!MessagesService.getLocalPredicate().test(account)) {
        MessagesService.sendMessage(sender, Messages.NOT_IN_LOCAL_SERVER.get());

        return;
      }

      // Check we send to this server
      boolean cancel = !(passToBackendServer
              || (serverListDisabled || passthruServers.contains(account.getServerName())));

      e.setResult(cancel ? PlayerChatEvent.ChatResult.denied() : PlayerChatEvent.ChatResult.allowed());

      // Was just cancelled, or we want to process all local chat regardless
      if (cancel || !passTransparently) {
        MessagesService.sendLocalMessage(sender, message);
      }
      // still log and spy after transparently sent messages
      if (passTransparently && logTransparentLocal) {
        MessagesService.sendTransparentMessage(new Context(sender, message));
      }
    }
  }
}
