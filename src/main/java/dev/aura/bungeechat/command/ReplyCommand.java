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
    super("reply", messengerModule.getModuleSection().getStringList("aliases.reply"));

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
  public void execute(CommandSource sender, String[] args) {
    if (PermissionManager.hasPermission(sender, Permission.COMMAND_MESSAGE)) {
      if (args.length < 1) {
        MessagesService.sendMessage(
            sender, Messages.INCORRECT_USAGE.get(sender, "/reply <message>"));
      } else {
        Optional<BungeeChatAccount> targetAccount =
            BungeecordAccountManager.getAccount(getReplier(sender));

        if (!targetAccount.isPresent()
            || (targetAccount.get().isVanished()
                && !PermissionManager.hasPermission(sender, Permission.COMMAND_VANISH_VIEW))) {
          MessagesService.sendMessage(sender, Messages.NO_REPLY.get());
          return;
        }

        CommandSource target = BungeecordAccountManager.getCommandSource(targetAccount.get()).get();

        if (!targetAccount.get().hasMessangerEnabled()
            && !PermissionManager.hasPermission(sender, Permission.BYPASS_TOGGLE_MESSAGE)) {
          MessagesService.sendMessage(sender, Messages.HAS_MESSAGER_DISABLED.get(target));
          return;
        }

        String finalMessage = String.join(" ", args);

        MessagesService.sendPrivateMessage(sender, target, finalMessage);
        ReplyCommand.setReply(sender, target);
      }
    }
  }
}
