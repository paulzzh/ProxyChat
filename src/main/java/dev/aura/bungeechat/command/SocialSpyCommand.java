package dev.aura.bungeechat.command;

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
    super(
        "socialspy",
        Permission.COMMAND_SOCIALSPY,
        socialSpyModule.getModuleSection().getStringList("aliases.socialspy"));
  }

  @Override
  public void execute(Invocation invocation) {
    if (!PermissionManager.hasPermission(invocation.source(), Permission.COMMAND_SOCIALSPY)) return;

    if (!(invocation.source() instanceof Player)) {
      MessagesService.sendMessage(invocation.source(), Messages.NOT_A_PLAYER.get());
      return;
    }

    BungeeChatAccount player = BungeecordAccountManager.getAccount(invocation.source()).get();
    player.toggleSocialSpy();

    if (player.hasSocialSpyEnabled()) {
      MessagesService.sendMessage(invocation.source(), Messages.ENABLE_SOCIAL_SPY.get(player));
    } else {
      MessagesService.sendMessage(invocation.source(), Messages.DISABLE_SOCIAL_SPY.get(player));
    }
  }
}
