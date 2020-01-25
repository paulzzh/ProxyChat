package dev.aura.bungeechat.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import dev.aura.bungeechat.account.BungeecordAccountManager;
import dev.aura.bungeechat.api.account.BungeeChatAccount;
import dev.aura.bungeechat.message.Messages;
import dev.aura.bungeechat.message.MessagesService;
import dev.aura.bungeechat.module.MessengerModule;
import dev.aura.bungeechat.permission.Permission;
import dev.aura.bungeechat.permission.PermissionManager;

public class ToggleCommand extends BaseCommand {
  public ToggleCommand(MessengerModule messengerModule) {
    super("msgtoggle", messengerModule.getModuleSection().getStringList("aliases.msgtoggle"));
  }

  @Override
  public void execute(CommandSource sender, String[] args) {
    if (PermissionManager.hasPermission(sender, Permission.COMMAND_TOGGLE_MESSAGE)) {
      if (!(sender instanceof Player)) {
        MessagesService.sendMessage(sender, Messages.NOT_A_PLAYER.get());
      } else {
        BungeeChatAccount player = BungeecordAccountManager.getAccount(sender).get();
        player.toggleMessanger();

        if (player.hasMessangerEnabled()) {
          MessagesService.sendMessage(sender, Messages.ENABLE_MESSAGER.get());
        } else {
          MessagesService.sendMessage(sender, Messages.DISABLE_MESSAGER.get());
        }
      }
    }
  }
}
