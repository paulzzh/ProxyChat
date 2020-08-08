package dev.aura.bungeechat.command;

import com.velocitypowered.api.proxy.Player;
import dev.aura.bungeechat.account.BungeecordAccountManager;
import dev.aura.bungeechat.api.account.BungeeChatAccount;
import dev.aura.bungeechat.message.Messages;
import dev.aura.bungeechat.message.MessagesService;
import dev.aura.bungeechat.module.VanishModule;
import dev.aura.bungeechat.permission.Permission;
import dev.aura.bungeechat.permission.PermissionManager;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class VanishCommand extends BaseCommand {
  private static final List<String> arg1Completetions = Arrays.asList("on", "off");

  public VanishCommand(VanishModule vanisherModule) {
    super(
        "bvanish",
        Permission.COMMAND_VANISH,
        vanisherModule.getModuleSection().getStringList("aliases"));
  }

  @Override
  public void execute(Invocation invocation) {
    if (!PermissionManager.hasPermission(invocation.source(), Permission.COMMAND_VANISH)) return;

    if (!(invocation.source() instanceof Player)) {
      MessagesService.sendMessage(invocation.source(), Messages.NOT_A_PLAYER.get());
      return;
    }

    BungeeChatAccount player = BungeecordAccountManager.getAccount(invocation.source()).get();

    if (invocation.arguments().length > 0) {
      if (invocation.arguments()[0].equalsIgnoreCase("on")) {
        player.setVanished(true);
      } else if (invocation.arguments()[0].equalsIgnoreCase("off")) {
        player.setVanished(false);
      } else {
        player.toggleVanished();
      }
    } else {
      player.toggleVanished();
    }

    if (player.isVanished()) {
      MessagesService.sendMessage(invocation.source(), Messages.ENABLE_VANISH.get());
    } else {
      MessagesService.sendMessage(invocation.source(), Messages.DISABLE_VANISH.get());
    }
  }

  @Override
  public List<String> suggest(Invocation invocation) {
    if(invocation.arguments().length == 0) {
      return arg1Completetions;
    }

    if (invocation.arguments().length == 1 && !arg1Completetions.contains(invocation.arguments()[0])) {
      final String param1 = invocation.arguments()[0];

      return arg1Completetions.stream()
          .filter(completion -> completion.startsWith(param1))
          .collect(Collectors.toList());
    }

    return super.suggest(invocation);
  }
}
