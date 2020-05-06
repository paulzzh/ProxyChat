package dev.aura.bungeechat.permission;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import dev.aura.bungeechat.account.BungeecordAccountManager;
import dev.aura.bungeechat.api.account.BungeeChatAccount;
import dev.aura.bungeechat.message.Messages;
import dev.aura.bungeechat.message.MessagesService;
import lombok.experimental.UtilityClass;

import java.util.Optional;

@UtilityClass
public class PermissionManager {
  public static boolean hasPermissionNoMessage(Player player, Permission permission) {
    return player.hasPermission(permission.getStringedPermission());
  }

  public static boolean hasPermissionNoMessage(CommandSource sender, Permission permission) {
    return !(sender instanceof Player)
    || hasPermissionNoMessage((Player) sender, permission);
  }

  public static boolean hasPermissionNoMessage(BungeeChatAccount account, Permission permission) {
    return hasPermissionNoMessage(
      BungeecordAccountManager.getCommandSource(account).get(), permission);
  }

  public static boolean hasPermission(Player player, Permission permission) {
    if (hasPermissionNoMessage(player, permission)) return true;
    else {
      if (permission.getWarnOnLackingPermission()) {
        MessagesService.sendMessage(player, Messages.NO_PERMISSION.get(player));
      }

      return false;
    }
  }

  public static boolean hasPermission(CommandSource sender, Permission permission) {
    return !(sender instanceof Player) || hasPermission((Player) sender, permission);
  }

  public static boolean hasPermission(BungeeChatAccount account, Permission permission) {
    Optional<CommandSource> player = BungeecordAccountManager.getCommandSource(account);
    return player.filter(value -> hasPermission(value, permission)).isPresent();
  }
}
