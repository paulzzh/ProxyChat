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
import com.velocitypowered.api.event.player.PlayerChatEvent;
import uk.co.notnull.ProxyChat.account.ProxyChatAccountManager;
import uk.co.notnull.ProxyChat.api.account.ProxyChatAccount;
import uk.co.notnull.ProxyChat.api.utils.ChatUtils;
import uk.co.notnull.ProxyChat.chatlog.ChatLoggingManager;

public class ChatLoggingListener {
  @Subscribe(order = PostOrder.LATE)
  public void onPlayerChat(PlayerChatEvent e) {
    if (!e.getResult().isAllowed()) return;
    if (e.getPlayer() == null) return;

    ProxyChatAccount sender =
        ProxyChatAccountManager.getAccount(e.getPlayer()).get();
    String message = e.getMessage();

    if (ChatUtils.isCommand(message)) {
      ChatLoggingManager.logCommand(sender, message);
    }
  }
}
