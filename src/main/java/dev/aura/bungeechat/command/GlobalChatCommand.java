package dev.aura.bungeechat.command;

import com.velocitypowered.api.proxy.Player;
import dev.aura.bungeechat.account.BungeecordAccountManager;
import dev.aura.bungeechat.api.account.BungeeChatAccount;
import dev.aura.bungeechat.api.enums.AccountType;
import dev.aura.bungeechat.api.enums.ChannelType;
import dev.aura.bungeechat.message.Messages;
import dev.aura.bungeechat.message.MessagesService;
import dev.aura.bungeechat.module.GlobalChatModule;
import dev.aura.bungeechat.permission.Permission;
import dev.aura.bungeechat.permission.PermissionManager;
import java.util.Arrays;
import java.util.stream.Collectors;

public class GlobalChatCommand extends BaseCommand {
  public GlobalChatCommand(GlobalChatModule globalChatModule) {
    super(
        "global",
        Permission.COMMAND_GLOBAL,
        globalChatModule.getModuleSection().getStringList("aliases"));
  }

  @Override
  public void execute(Invocation invocation) {
    if (!PermissionManager.hasPermission(invocation.source(), Permission.COMMAND_GLOBAL)) return;

    BungeeChatAccount account = BungeecordAccountManager.getAccount(invocation.source()).get();

    if (!MessagesService.getGlobalPredicate().test(account)
        && (account.getAccountType() == AccountType.PLAYER)) {
      MessagesService.sendMessage(invocation.source(), Messages.NOT_IN_GLOBAL_SERVER.get());

      return;
    }

    if (invocation.arguments().length < 1) {
      if (!(invocation.source() instanceof Player)) {
        MessagesService.sendMessage(invocation.source(), Messages.NOT_A_PLAYER.get());
        return;
      }

      if (PermissionManager.hasPermission(invocation.source(), Permission.COMMAND_GLOBAL_TOGGLE)) {
        BungeeChatAccount player = BungeecordAccountManager.getAccount(invocation.source()).get();

        if (player.getChannelType() == ChannelType.GLOBAL) {
          ChannelType defaultChannelType = player.getDefaultChannelType();
          player.setChannelType(defaultChannelType);

          if (defaultChannelType == ChannelType.LOCAL) {
            MessagesService.sendMessage(invocation.source(), Messages.ENABLE_LOCAL.get());
          } else {
            MessagesService.sendMessage(invocation.source(), Messages.GLOBAL_IS_DEFAULT.get());
          }
        } else {
          player.setChannelType(ChannelType.GLOBAL);
          MessagesService.sendMessage(invocation.source(), Messages.ENABLE_GLOBAL.get());
        }
      } else {
        MessagesService.sendMessage(
            invocation.source(), Messages.INCORRECT_USAGE.get(invocation.source(), "/global <message>"));
      }
    } else {
      String finalMessage = Arrays.stream(invocation.arguments()).collect(Collectors.joining(" "));

      MessagesService.sendGlobalMessage(invocation.source(), finalMessage);
    }
  }
}
