package dev.aura.bungeechat.command;

import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import dev.aura.bungeechat.BungeeChat;
import dev.aura.bungeechat.permission.Permission;

import java.util.Collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    this(name, permission, new String[0]);
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

  public BaseCommand(String name, Permission permission, List<String> aliases) {
    this(name, permission, stringListToArray(aliases));
  }

  public BaseCommand(String name, String permission, List<String> aliases) {
    this(name, permission, stringListToArray(aliases));
  }

  public BaseCommand(String name, Permission permission, String[] aliases) {
    this(name, permission.getStringedPermission(), aliases);
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

  public List<String> suggest(CommandSource sender, String[] args) {
    return Collections.emptyList();
  }
}
