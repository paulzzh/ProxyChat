package dev.aura.bungeechat.command;

import dev.aura.bungeechat.api.placeholder.BungeeChatContext;
import dev.aura.bungeechat.message.Context;
import dev.aura.bungeechat.message.Format;
import dev.aura.bungeechat.message.Messages;
import dev.aura.bungeechat.message.MessagesService;
import dev.aura.bungeechat.module.AlertModule;
import dev.aura.bungeechat.permission.Permission;
import dev.aura.bungeechat.permission.PermissionManager;
import net.kyori.adventure.text.Component;

import java.util.Optional;

public class AlertCommand extends BaseCommand {
  public AlertCommand(AlertModule alertModule) {
    super(
        "alert", Permission.COMMAND_ALERT, alertModule.getModuleSection().getStringList("aliases"));
  }

  @Override
  public void execute(Invocation invocation) {
    if (PermissionManager.hasPermission(invocation.source(), Permission.COMMAND_ALERT)) {
      if (invocation.arguments().length < 1) {
        MessagesService.sendMessage(
            invocation.source(), Messages.INCORRECT_USAGE.get(invocation.source(), "/alert <message>"));
      } else {
        BungeeChatContext context = new Context(invocation.source(), String.join(" ", invocation.arguments()));
        MessagesService.parseMessage(context, false);
        Optional<Component> message = MessagesService.preProcessMessage(context, Format.ALERT);

        MessagesService.sendToMatchingPlayers(message, MessagesService.getGlobalPredicate());
      }
    }
  }
}
