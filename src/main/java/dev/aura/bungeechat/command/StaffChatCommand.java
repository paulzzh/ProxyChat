package dev.aura.bungeechat.command;

import dev.aura.bungeechat.account.BungeecordAccountManager;
import dev.aura.bungeechat.api.account.BungeeChatAccount;
import dev.aura.bungeechat.api.enums.ChannelType;
import dev.aura.bungeechat.message.Messages;
import dev.aura.bungeechat.message.MessagesService;
import dev.aura.bungeechat.module.StaffChatModule;
import dev.aura.bungeechat.permission.Permission;
import dev.aura.bungeechat.permission.PermissionManager;
import java.util.Arrays;
import java.util.stream.Collectors;

public class StaffChatCommand extends BaseCommand {
  public StaffChatCommand(StaffChatModule staffChatModule) {
    super(
        "staffchat",
        Permission.COMMAND_STAFFCHAT,
        staffChatModule.getModuleSection().getStringList("aliases"));
  }

  @Override
  public void execute(Invocation invocation) {
    if (!PermissionManager.hasPermission(invocation.source(), Permission.COMMAND_STAFFCHAT)) return;

    if (invocation.arguments().length == 0) {
      BungeeChatAccount player = BungeecordAccountManager.getAccount(invocation.source()).get();

      if (player.getChannelType() == ChannelType.STAFF) {
        ChannelType defaultChannelType = player.getDefaultChannelType();
        player.setChannelType(defaultChannelType);

        if (defaultChannelType == ChannelType.LOCAL) {
          MessagesService.sendMessage(invocation.source(), Messages.ENABLE_LOCAL.get());
        } else {
          MessagesService.sendMessage(invocation.source(), Messages.ENABLE_GLOBAL.get());
        }
      } else {
        player.setChannelType(ChannelType.STAFF);
        MessagesService.sendMessage(invocation.source(), Messages.ENABLE_STAFFCHAT.get());
      }
    } else {
      String finalMessage = Arrays.stream(invocation.arguments()).collect(Collectors.joining(" "));

      MessagesService.sendStaffMessage(invocation.source(), finalMessage);
    }
  }
}
