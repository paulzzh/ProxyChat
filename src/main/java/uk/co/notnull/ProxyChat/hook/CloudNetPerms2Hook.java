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

package uk.co.notnull.ProxyChat.hook;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;
import de.dytanic.cloudnet.lib.player.permission.PermissionPool;
import uk.co.notnull.ProxyChat.api.account.ProxyChatAccount;
import uk.co.notnull.ProxyChat.api.hook.ProxyChatHook;
import uk.co.notnull.ProxyChat.api.hook.HookManager;

import java.util.Objects;
import java.util.Optional;

public class CloudNetPerms2Hook implements ProxyChatHook {
  private final CloudAPI api;

  public CloudNetPerms2Hook() {
    api = CloudAPI.getInstance();
  }

  @Override
  public Optional<String> getPrefix(ProxyChatAccount account) {
    return getPermissionGroup(account).map(PermissionGroup::getPrefix).filter(Objects::nonNull);
  }

  @Override
  public Optional<String> getSuffix(ProxyChatAccount account) {
    return getPermissionGroup(account).map(PermissionGroup::getSuffix).filter(Objects::nonNull);
  }

  private Optional<PermissionGroup> getPermissionGroup(ProxyChatAccount account) {
    CloudPlayer player = api.getOnlinePlayer(account.getUniqueId());
    PermissionGroup permissionGroup =
        player.getPermissionEntity().getHighestPermissionGroup(api.getPermissionPool());

    return Optional.ofNullable(permissionGroup);
  }

  @Override
  public int getPriority() {
    return HookManager.PERMISSION_PLUGIN_PREFIX_PRIORITY;
  }

  public boolean permissionsEnabled() {
    PermissionPool permissionPool = api.getPermissionPool();

    return (permissionPool != null) && permissionPool.isAvailable();
  }
}
