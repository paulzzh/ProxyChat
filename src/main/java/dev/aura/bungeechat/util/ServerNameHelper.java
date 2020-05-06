package dev.aura.bungeechat.util;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import dev.aura.bungeechat.BungeeChat;
import dev.aura.bungeechat.message.Messages;
import dev.aura.bungeechat.message.MessagesService;
import java.util.Optional;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ServerNameHelper {
  public static Optional<ServerInfo> getServerInfo(String serverName) {
    Optional<RegisteredServer> server = BungeeChat.getInstance().getProxy().getAllServers().stream()
        .filter(s -> serverName.equalsIgnoreCase(s.getServerInfo().getName()))
        .findAny();

    return server.map(RegisteredServer::getServerInfo);
  }

  public static Optional<String> verifyServerName(String serverName) {
    return getServerInfo(serverName).map(ServerInfo::getName);
  }

  public static Optional<String> verifyServerName(String serverName, CommandSource sender) {
    final Optional<String> verifiedServerName = verifyServerName(serverName);

    if (!verifiedServerName.isPresent()) {
      MessagesService.sendMessage(sender, Messages.UNKNOWN_SERVER.get(sender, serverName));
    }

    return verifiedServerName;
  }
}
