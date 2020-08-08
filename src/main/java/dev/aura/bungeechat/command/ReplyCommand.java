package dev.aura.bungeechat.command;

import com.velocitypowered.api.command.CommandSource;
import dev.aura.bungeechat.account.BungeecordAccountManager;
import dev.aura.bungeechat.api.account.BungeeChatAccount;
import dev.aura.bungeechat.message.Messages;
import dev.aura.bungeechat.message.MessagesService;
import dev.aura.bungeechat.module.MessengerModule;
import dev.aura.bungeechat.permission.Permission;
import dev.aura.bungeechat.permission.PermissionManager;

import java.util.HashMap;
import java.util.Optional;

public class ReplyCommand extends BaseCommand {
  private static HashMap<CommandSource, CommandSource> replies;

  public ReplyCommand(MessengerModule messengerModule) {
    super(
        "reply",
        Permission.COMMAND_MESSAGE,
        messengerModule.getModuleSection().getStringList("aliases.reply"));

    if (replies == null) {
      replies = new HashMap<>();
    } else {
      replies.clear();
    }
  }

  protected static void setReply(CommandSource sender, CommandSource target) {
    replies.put(sender, target);
    replies.put(target, sender);
  }

  private static CommandSource getReplier(CommandSource player) {
    return replies.getOrDefault(player, null);
  }

  @Override
  public void execute(Invocation invocation) {
    if (!PermissionManager.hasPermission(invocation.source(), Permission.COMMAND_MESSAGE)) return;

    if (invocation.arguments().length < 1) {
      MessagesService.sendMessage(invocation.source(), Messages.INCORRECT_USAGE
              .get(invocation.source(), "/reply <message>"));
      return;
    }

    Optional<BungeeChatAccount> targetAccount =
        BungeecordAccountManager.getAccount(getReplier(invocation.source()));

    if (targetAccount.isEmpty()
        || (targetAccount.get().isVanished()
            && !PermissionManager.hasPermission(invocation.source(), Permission.COMMAND_VANISH_VIEW))) {
      MessagesService.sendMessage(invocation.source(), Messages.NO_REPLY.get());
      return;
    }

    CommandSource target = BungeecordAccountManager.getCommandSource(targetAccount.get()).get();

    if (!targetAccount.get().hasMessangerEnabled()
        && !PermissionManager.hasPermission(invocation.source(), Permission.BYPASS_TOGGLE_MESSAGE)) {
      MessagesService.sendMessage(invocation.source(), Messages.HAS_MESSAGER_DISABLED.get(target));
      return;
    }

    String finalMessage = String.join(" ", invocation.arguments());

    MessagesService.sendPrivateMessage(invocation.source(), target, finalMessage);
    ReplyCommand.setReply(invocation.source(), target);
  }
}
