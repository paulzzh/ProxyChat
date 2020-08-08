package dev.aura.bungeechat.command;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.SimpleCommand;
import dev.aura.bungeechat.BungeeChat;
import dev.aura.bungeechat.permission.Permission;
import org.slf4j.Logger;

import java.util.*;

public abstract class BaseCommand implements SimpleCommand {
  private final String name;
  private final ArrayList<String> aliases;
  private final String permission;

  public BaseCommand(String name) {
    this(name, null, Collections.emptyList());
  }

  public BaseCommand(String name, Permission permission, List<String> aliases) {
    this.name = name;
    this.aliases = new ArrayList<>(aliases);
    this.permission = permission != null ? permission.getStringedPermission() : null;
  }

  public void register() {
    String[] aliases = new String[this.aliases.size()];
    this.aliases.toArray(aliases);

    CommandManager commandManager = BungeeChat.getInstance().getProxy().getCommandManager();
    CommandMeta meta = commandManager.metaBuilder(name).aliases(aliases).build();

    commandManager.register(meta, this);
  }

  public void unregister() {
    BungeeChat.getInstance().getProxy().getCommandManager().unregister(name);
  }

  public List<String> suggest(Invocation invocation) {
    return Collections.emptyList();
  }

  @Override
  public boolean hasPermission(Invocation invocation) {
    return permission == null || invocation.source().hasPermission(permission);
  }
}
