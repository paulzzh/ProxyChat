package dev.aura.bungeechat.command;

import com.velocitypowered.api.command.CommandSource;
import dev.aura.bungeechat.account.BungeecordAccountManager;
import dev.aura.bungeechat.api.account.AccountManager;
import dev.aura.bungeechat.api.account.BungeeChatAccount;
import dev.aura.bungeechat.message.Messages;
import dev.aura.bungeechat.message.MessagesService;
import dev.aura.bungeechat.module.MutingModule;
import dev.aura.bungeechat.permission.Permission;
import dev.aura.bungeechat.permission.PermissionManager;
import java.util.Optional;

public class MuteCommand extends BaseCommand {
  public MuteCommand(MutingModule mutingModule) {
    super("mute", mutingModule.getModuleSection().getStringList("aliases.mute"));
  }

  @Override
  public void execute(CommandSource sender, String[] args) {
    if (PermissionManager.hasPermission(sender, Permission.COMMAND_MUTE)) {
      if (args.length < 1) {
        MessagesService.sendMessage(sender, Messages.INCORRECT_USAGE.get(sender, "/mute <player>"));
      } else {
        Optional<BungeeChatAccount> targetAccount = AccountManager.getAccount(args[0]);

        if (!targetAccount.isPresent()) {
          MessagesService.sendMessage(sender, Messages.PLAYER_NOT_FOUND.get());
          return;
        }

        CommandSource target = BungeecordAccountManager.getCommandSource(targetAccount.get()).get();

        if (targetAccount.get().isMuted()) {
          MessagesService.sendMessage(sender, Messages.MUTE_IS_MUTED.get());
          return;
        }

        targetAccount.get().mutePermanetly();
        MessagesService.sendMessage(sender, Messages.MUTE.get(target));
      }
    }
  }
}
