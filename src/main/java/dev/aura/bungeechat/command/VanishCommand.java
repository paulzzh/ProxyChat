package dev.aura.bungeechat.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import dev.aura.bungeechat.account.BungeecordAccountManager;
import dev.aura.bungeechat.api.account.BungeeChatAccount;
import dev.aura.bungeechat.message.Messages;
import dev.aura.bungeechat.message.MessagesService;
import dev.aura.bungeechat.module.VanishModule;
import dev.aura.bungeechat.permission.Permission;
import dev.aura.bungeechat.permission.PermissionManager;

public class VanishCommand extends BaseCommand {
  public VanishCommand(VanishModule vanisherModule) {
    super("bvanish", vanisherModule.getModuleSection().getStringList("aliases"));
  }

  @Override
  public void execute(CommandSource sender, String[] args) {
    if (PermissionManager.hasPermission(sender, Permission.COMMAND_VANISH)) {
      if (!(sender instanceof Player)) {
        MessagesService.sendMessage(sender, Messages.NOT_A_PLAYER.get());
      } else {
        BungeeChatAccount player = BungeecordAccountManager.getAccount(sender).get();
        if (args.length > 0) {
          if (args[0].equalsIgnoreCase("on")) {
            player.setVanished(true);
          } else if (args[0].equalsIgnoreCase("off")) {
            player.setVanished(false);
          } else {
            player.toggleVanished();
          }
        } else {
          player.toggleVanished();
        }

        if (player.isVanished()) {
          MessagesService.sendMessage(sender, Messages.ENABLE_VANISH.get());
        } else {
          MessagesService.sendMessage(sender, Messages.DISABLE_VANISH.get());
        }
      }
    }
  }
}
