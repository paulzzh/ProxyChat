package dev.aura.bungeechat.command;

import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import dev.aura.bungeechat.BungeeChat;
import dev.aura.bungeechat.api.account.AccountManager;
import dev.aura.bungeechat.api.account.BungeeChatAccount;
import dev.aura.bungeechat.api.module.ModuleManager;
import dev.aura.bungeechat.module.BungeecordModuleManager;
import dev.aura.bungeechat.permission.Permission;
import dev.aura.bungeechat.permission.PermissionManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public abstract class BaseCommand implements Command {
  private final ArrayList<String> aliases;
  private final String permission;

  protected static String[] stringListToArray(List<String> list) {
    return list.toArray(new String[list.size()]);
  }

  public BaseCommand(String name) {
    this(name, "");
  }

  public BaseCommand(String name, Permission permission) {
    this(name, permission.getStringedPermission(), new String[0]);
  }

  public BaseCommand(String name, String permission) {
    this(name, permission, new String[0]);
  }

  public BaseCommand(String name, List<String> aliases) {
    this(name, "", aliases);
  }

  public BaseCommand(String name, String[] aliases) {
    this(name, "", aliases);
  }

  public BaseCommand(String name, String permission, List<String> aliases) {
    this(name, permission, stringListToArray(aliases));
  }

  public BaseCommand(String name, String permission, String[] aliases) {
    this.aliases = new ArrayList<>();
    this.aliases.add(name);
    this.aliases.addAll(Arrays.asList(aliases));

    this.permission = permission;
  }

  public void register() {
    String[] aliases = new String[this.aliases.size()];
    this.aliases.toArray(aliases);

    BungeeChat.getInstance().getProxy().getCommandManager().register(this, aliases);
  }

  public void unregister() {

  }

  @Override
  public List<String> suggest(CommandSource source, String[] currentArgs) {
    List<String> suggestions = new ArrayList<>();
    String partialPlayerName = "";

    if (!PermissionManager.hasPermission(source, Permission.USE_TAB_COMPLETE)) {
      return Collections.emptyList();
    }

    if(currentArgs.length > 0) {
      partialPlayerName = currentArgs[currentArgs.length - 1];
    }

    Stream<BungeeChatAccount> stream = AccountManager.getAccountsForPartialName(partialPlayerName).stream();

    if (ModuleManager.isModuleActive(BungeecordModuleManager.VANISHER_MODULE)
        && !PermissionManager.hasPermission(source, Permission.COMMAND_VANISH_VIEW)) {
      stream = stream.filter(account -> !account.isVanished());
    }

    stream.forEach(account -> suggestions.add(account.getName()));

    return suggestions;
  }
}
