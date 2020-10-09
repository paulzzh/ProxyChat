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

package uk.co.notnull.ProxyChat.module;

import com.velocitypowered.api.plugin.PluginManager;
import com.velocitypowered.api.proxy.ProxyServer;
import uk.co.notnull.ProxyChat.ProxyChat;
import uk.co.notnull.ProxyChat.command.MuteCommand;
import uk.co.notnull.ProxyChat.command.TempMuteCommand;
import uk.co.notnull.ProxyChat.command.UnmuteCommand;
import uk.co.notnull.ProxyChat.listener.MutingListener;

public class MutingModule extends Module {
  private MuteCommand muteCommand;
  private TempMuteCommand tempMuteCommand;
  private UnmuteCommand unmuteCommand;
  private MutingListener mutingListener;
  private final String[] mutePlugins = {
    "AdvancedBan", "BungeeBan", "BungeeSystem", "BungeeAdminTools", "Banmanager"
  };

  @Override
  public String getName() {
    return "Muting";
  }

  @Override
  public boolean isEnabled() {
    if (!super.isEnabled()) return false;

    if (getModuleSection().getBoolean("disableWithOtherMutePlugins")) {
      PluginManager pm = ProxyChat.getInstance().getProxy().getPluginManager();

      for (String mutePlugin : mutePlugins) {
        if (pm.getPlugin(mutePlugin).isPresent()) return false;
      }
    }

    return true;
  }

  @Override
  public void onEnable() {
    muteCommand = new MuteCommand(this);
    tempMuteCommand = new TempMuteCommand(this);
    unmuteCommand = new UnmuteCommand(this);
    mutingListener = new MutingListener();

    ProxyChat plugin = ProxyChat.getInstance();
    ProxyServer proxy = plugin.getProxy();

    muteCommand.register();
    tempMuteCommand.register();
    unmuteCommand.register();
    proxy.getEventManager()
        .register(ProxyChat.getInstance(), mutingListener);
  }

  @Override
  public void onDisable() {
    muteCommand.unregister();
    tempMuteCommand.unregister();
    unmuteCommand.unregister();
    ProxyChat.getInstance().getProxy().getEventManager().unregisterListener(ProxyChat.getInstance(), mutingListener);
  }
}
