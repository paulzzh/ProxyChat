package dev.aura.bungeechat.command;

import com.velocitypowered.api.command.CommandSource;
import dev.aura.bungeechat.api.account.AccountManager;
import dev.aura.bungeechat.api.account.BungeeChatAccount;
import dev.aura.bungeechat.message.Messages;
import dev.aura.bungeechat.message.MessagesService;
import dev.aura.bungeechat.module.MutingModule;
import dev.aura.bungeechat.permission.Permission;
import dev.aura.bungeechat.permission.PermissionManager;
import java.util.Optional;

public class UnmuteCommand extends BaseCommand {
  public UnmuteCommand(MutingModule mutingModule) {
    super("unmute", mutingModule.getModuleSection().getStringList("aliases.unmute"));
  }

  @Override
  public void execute(CommandSource sender, String[] args) {
    if (PermissionManager.hasPermission(sender, Permission.COMMAND_UNMUTE)) {
      if (args.length < 1) {
        MessagesService.sendMessage(
            sender, Messages.INCORRECT_USAGE.get(sender, "/unmute <player>"));
      } else {
        Optional<BungeeChatAccount> targetAccount = AccountManager.getAccount(args[0]);

        if (!targetAccount.isPresent()) {
          MessagesService.sendMessage(sender, Messages.PLAYER_NOT_FOUND.get());
          return;
        }

        if (!targetAccount.get().isMuted()) {
          MessagesService.sendMessage(sender, Messages.UNMUTE_NOT_MUTED.get());
          return;
        }

        targetAccount.get().unmute();
        MessagesService.sendMessage(sender, Messages.UNMUTE.get(sender));
      }
    }
  }
}
