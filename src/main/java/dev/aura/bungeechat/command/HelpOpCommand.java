package dev.aura.bungeechat.command;

import dev.aura.bungeechat.message.Messages;
import dev.aura.bungeechat.message.MessagesService;
import dev.aura.bungeechat.module.HelpOpModule;
import dev.aura.bungeechat.permission.Permission;
import dev.aura.bungeechat.permission.PermissionManager;

public class HelpOpCommand extends BaseCommand {
  public HelpOpCommand(HelpOpModule helpOpModule) {
    super(
        "helpop",
        Permission.COMMAND_HELPOP,
        helpOpModule.getModuleSection().getStringList("aliases"));
  }

  @Override
  public void execute(Invocation invocation) {
    if (PermissionManager.hasPermission(invocation.source(), Permission.COMMAND_HELPOP)) {
      if (invocation.arguments().length < 1) {
        MessagesService.sendMessage(
            invocation.source(), Messages.INCORRECT_USAGE.get(invocation.source(), "/helpop <message>"));
      } else {
        String finalMessage = String.join(" ", invocation.arguments());

        MessagesService.sendHelpMessage(invocation.source(), finalMessage);
      }
    }
  }
}
