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

package uk.co.notnull.ProxyChat.listener;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.proxy.Player;
import uk.co.notnull.ProxyChat.ProxyChat;
import uk.co.notnull.ProxyChat.event.ProxyChatJoinEvent;
import uk.co.notnull.ProxyChat.event.ProxyChatLeaveEvent;
import uk.co.notnull.ProxyChat.event.ProxyChatServerSwitchEvent;

public class ProxyChatEventsListener {
  @Subscribe(order = PostOrder.EARLY)
  public void onPlayerServerSwitch(PlayerChooseInitialServerEvent e) {
    Player player = e.getPlayer();

    ProxyChat.getInstance().getProxy().getEventManager().fireAndForget(new ProxyChatJoinEvent(player));
  }

  @Subscribe(order = PostOrder.LATE)
  public void onPlayerServerSwitch(ServerPostConnectEvent e) {
    Player player = e.getPlayer();

    ProxyChat.getInstance().getProxy().getEventManager().fireAndForget(
            new ProxyChatServerSwitchEvent(player, player.getCurrentServer().get().getServer()));
  }

  @Subscribe(order = PostOrder.LATE)
  public void onPlayerLeave(DisconnectEvent e) {
    Player player = e.getPlayer();

    if(e.getLoginStatus() != DisconnectEvent.LoginStatus.SUCCESSFUL_LOGIN) {
      ProxyChat.getInstance().getProxy().getEventManager().fireAndForget(new ProxyChatLeaveEvent(player));
    }
  }
}
