package dev.aura.bungeechat.util;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import dev.aura.bungeechat.BungeeChat;
import com.typesafe.config.Config;
import dev.aura.bungeechat.config.Configuration;
import dev.aura.bungeechat.message.Messages;
import dev.aura.bungeechat.message.MessagesService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;

@UtilityClass
public class ServerNameUtil {
  private static Map<String, String> aliasMapping = new HashMap<>();
  private static Map<RegisteredServer, Component> serverComponents = new HashMap<>();

  public static Optional<ServerInfo> getServerInfo(String serverName) {
    Optional<RegisteredServer> server = BungeeChat.getInstance().getProxy().getAllServers().stream()
        .filter(s -> serverName.equalsIgnoreCase(s.getServerInfo().getName()))
        .findAny();

    return server.map(RegisteredServer::getServerInfo);
  }

  public static Optional<RegisteredServer> verifyServerName(String serverName) {
    return BungeeChat.getInstance().getProxy().getServer(serverName);
  }

  public static Optional<RegisteredServer> verifyServerName(String serverName, CommandSource sender) {
    final Optional<RegisteredServer> verifiedServer = verifyServerName(serverName);

    if(verifiedServer.isEmpty()) {
      MessagesService.sendMessage(sender, Messages.UNKNOWN_SERVER.get(sender, serverName));
    }

    return verifiedServer;
  }

  public static List<String> getServerNames() {
    return BungeeChat.getInstance().getProxy().getAllServers().stream().map(
            server -> server.getServerInfo().getName()).collect(Collectors.toList());
  }

  public static List<String> getMatchingServerNames(String partialName) {
    return getServerNames().stream()
        .filter(serverName -> serverName.startsWith(partialName))
        .collect(Collectors.toList());
  }

  public static String getServerAlias(ServerInfo server) {
    return getServerAlias(server.getName());
  }

  public static String getServerAlias(RegisteredServer server) {
    return aliasMapping.getOrDefault(server.getServerInfo().getName(), server.getServerInfo().getName());
  }

  public static String getServerAlias(String name) {
    return aliasMapping.getOrDefault(name, name);
  }

  public static Component getServerComponent(RegisteredServer server) {
    return serverComponents.getOrDefault(server, Component.empty());
  }

  public static void init() {
    Config section = Configuration.get().getConfig("ServerAlias");

    aliasMapping =
        section.root().entrySet().stream()
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey, entry -> entry.getValue().unwrapped().toString()));

    serverComponents = BungeeChat.getInstance().getProxy().getAllServers().stream()
            .collect(Collectors.toMap(server -> server, server -> {
              String name = server.getServerInfo().getName();
              return Component.text().content(name)
                      .hoverEvent(
                                  Component.text().content(getServerAlias(name))
                                          .append(Component.newline())
                                          .append(Component.text("Click to join")
                                                          .color(NamedTextColor.YELLOW)).build()
                      )
                      .clickEvent(ClickEvent.runCommand("/server " + name))
                      .build();
            }));
  }
}
