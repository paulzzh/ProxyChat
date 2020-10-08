package dev.aura.bungeechat.command;

import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.aura.bungeechat.api.placeholder.BungeeChatContext;
import dev.aura.bungeechat.message.Context;
import dev.aura.bungeechat.message.Messages;
import dev.aura.bungeechat.message.MessagesService;
import dev.aura.bungeechat.module.LocalToModule;
import dev.aura.bungeechat.permission.Permission;
import dev.aura.bungeechat.permission.PermissionManager;
import dev.aura.bungeechat.util.ServerNameUtil;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LocalToCommand extends BaseCommand {
  public LocalToCommand(LocalToModule localToModule) {
    super(
        "localto",
        Permission.COMMAND_LOCALTO,
        localToModule.getModuleSection().getStringList("aliases"));
  }

  @Override
  public void execute(Invocation invocation) {
    if (!PermissionManager.hasPermission(invocation.source(), Permission.COMMAND_LOCALTO)) return;

    if (invocation.arguments().length < 2) {
      MessagesService.sendMessage(
          invocation.source(), Messages.INCORRECT_USAGE.get(invocation.source(), "/localto <server> <message>"));
      return;
    }

    Optional<RegisteredServer> server = ServerNameUtil.verifyServerName(invocation.arguments()[0], invocation.source());

    if (server.isEmpty()) return;

    String finalMessage = Arrays.stream(invocation.arguments(), 1, invocation.arguments().length)
            .collect(Collectors.joining(" "));
    BungeeChatContext context = new Context(invocation.source(), finalMessage, server.get());
    MessagesService.parseMessage(context, false);

    MessagesService.sendLocalMessage(context);
  }

  @Override
  public List<String> suggest(Invocation invocation) {
    if(invocation.arguments().length == 0) {
      return ServerNameUtil.getServerNames();
    }

    if (invocation.arguments().length == 1) {
      return ServerNameUtil.getMatchingServerNames(invocation.arguments()[0]);
    }

    return super.suggest(invocation);
  }
}
