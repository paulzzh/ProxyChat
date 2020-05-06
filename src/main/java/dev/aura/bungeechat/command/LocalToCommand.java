package dev.aura.bungeechat.command;

import com.velocitypowered.api.command.CommandSource;
import dev.aura.bungeechat.message.Context;
import dev.aura.bungeechat.message.Messages;
import dev.aura.bungeechat.message.MessagesService;
import dev.aura.bungeechat.module.LocalToModule;
import dev.aura.bungeechat.permission.Permission;
import dev.aura.bungeechat.permission.PermissionManager;
import dev.aura.bungeechat.util.ServerNameHelper;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class LocalToCommand extends BaseCommand {
  public LocalToCommand(LocalToModule localToModule) {
    super("localto", localToModule.getModuleSection().getStringList("aliases"));
  }

  @Override
  public void execute(CommandSource sender, String[] args) {
    if (PermissionManager.hasPermission(sender, Permission.COMMAND_LOCALTO)) {

      if (args.length < 2) {
        MessagesService.sendMessage(
            sender, Messages.INCORRECT_USAGE.get(sender, "/localto <server> <message>"));
        return;
      }

      Optional<String> optServerName = ServerNameHelper.verifyServerName(args[0], sender);

      if (!optServerName.isPresent()) return;

      String serverName = optServerName.get();

      String finalMessage = Arrays.stream(args, 1, args.length).collect(Collectors.joining(" "));
      MessagesService.sendLocalMessage(new Context(sender, finalMessage, serverName));
    }
  }
}
