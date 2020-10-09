/*
 * ProxyChat, a Velocity chat solution
 * Copyright (C) 2020 James Lyne
 *
 * Based on BungeeChat2 (https://github.com/AuraDevelopmentTeam/BungeeChat2)
 * Copyright (C) 2020 Aura Development Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package uk.co.notnull.ProxyChat.command;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.SimpleCommand;
import uk.co.notnull.ProxyChat.ProxyChat;
import uk.co.notnull.ProxyChat.permission.Permission;

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

    CommandManager commandManager = ProxyChat.getInstance().getProxy().getCommandManager();
    CommandMeta meta = commandManager.metaBuilder(name).aliases(aliases).build();

    commandManager.register(meta, this);
  }

  public void unregister() {
    ProxyChat.getInstance().getProxy().getCommandManager().unregister(name);
  }

  public List<String> suggest(Invocation invocation) {
    return Collections.emptyList();
  }

  @Override
  public boolean hasPermission(Invocation invocation) {
    return permission == null || invocation.source().hasPermission(permission);
  }
}
