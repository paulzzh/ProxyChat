package dev.aura.bungeechat.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import dev.aura.bungeechat.account.BungeecordAccountManager;
import dev.aura.bungeechat.api.account.BungeeChatAccount;
import dev.aura.bungeechat.message.Messages;
import dev.aura.bungeechat.message.MessagesService;
import dev.aura.bungeechat.module.SpyModule;
import dev.aura.bungeechat.permission.Permission;
import dev.aura.bungeechat.permission.PermissionManager;

public class SocialSpyCommand extends BaseCommand {
  public SocialSpyCommand(SpyModule socialSpyModule) {
    super("socialspy", socialSpyModule.getModuleSection().getStringList("aliases.socialspy"));
  }

  @Override
  public void execute(CommandSource sender, String[] args) {
    if (PermissionManager.hasPermission(sender, Permission.COMMAND_SOCIALSPY)) {
      if (!(sender instanceof Player)) {
        MessagesService.sendMessage(sender, Messages.NOT_A_PLAYER.get());
      } else {
        BungeeChatAccount player = BungeecordAccountManager.getAccount(sender).get();
        player.toggleSocialSpy();

        if (player.hasSocialSpyEnabled()) {
          MessagesService.sendMessage(sender, Messages.ENABLE_SOCIAL_SPY.get(player));
        } else {
          MessagesService.sendMessage(sender, Messages.DISABLE_SOCIAL_SPY.get(player));
        }
      }
    }
  }
}
