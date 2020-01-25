package dev.aura.bungeechat.command;

import com.velocitypowered.api.command.CommandSource;
import dev.aura.bungeechat.message.Messages;
import dev.aura.bungeechat.message.MessagesService;
import dev.aura.bungeechat.module.HelpOpModule;
import dev.aura.bungeechat.permission.Permission;
import dev.aura.bungeechat.permission.PermissionManager;

public class HelpOpCommand extends BaseCommand {
  public HelpOpCommand(HelpOpModule helpOpModule) {
    super("helpop", helpOpModule.getModuleSection().getStringList("aliases"));
  }

  @Override
  public void execute(CommandSource sender, String[] args) {
    if (PermissionManager.hasPermission(sender, Permission.COMMAND_HELPOP)) {
      if (args.length < 1) {
        MessagesService.sendMessage(
            sender, Messages.INCORRECT_USAGE.get(sender, "/helpop <message>"));
      } else {
        String finalMessage = String.join(" ", args);

        MessagesService.sendHelpMessage(sender, finalMessage);
      }
    }
  }
}
