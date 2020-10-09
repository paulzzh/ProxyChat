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

package uk.co.notnull.ProxyChat.module.perms;

import uk.co.notnull.ProxyChat.ProxyChat;
import uk.co.notnull.ProxyChat.api.hook.HookManager;
import uk.co.notnull.ProxyChat.config.Configuration;
import uk.co.notnull.ProxyChat.hook.LuckPerms5Hook;
import uk.co.notnull.ProxyChat.util.ClassUtil;

public class LuckPerms5Module extends PermissionPluginModule {
  private LuckPerms5Hook hook;

  @Override
  public String getName() {
    return "LuckPerms5";
  }

  @Override
  public boolean isEnabled() {
    return isPluginPresent("luckperms") && ClassUtil.doesClassExist("net.luckperms.api.LuckPerms");
  }

  @Override
  public void onEnable() {
    final boolean fixContext =
        Configuration.get().getBoolean("PrefixSuffixSettings.fixLuckPermsContext");

    LuckPerms5Hook hook = new LuckPerms5Hook(fixContext);
    ProxyChat.getInstance().getProxy()
        .getEventManager()
        .register(ProxyChat.getInstance(), hook);

    HookManager.addHook(getName(), hook);
  }

  @Override
  public void onDisable() {
    if(hook != null) {
      ProxyChat.getInstance().getProxy()
        .getEventManager()
        .unregisterListener(ProxyChat.getInstance(), hook);
    }
    HookManager.removeHook(getName());
  }
}
