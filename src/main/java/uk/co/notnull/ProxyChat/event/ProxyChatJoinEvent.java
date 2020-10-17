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

package uk.co.notnull.ProxyChat.event;

import com.velocitypowered.api.proxy.Player;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Event called as soon as a connection has a {@link Player} and is ready to be connected to
 * a server.
 *
 * <p>Used by ProxyChat internally to make sure people joining while they are online don't cause
 * issues.
 */
@Data
@ToString()
@EqualsAndHashCode()
public class ProxyChatJoinEvent {
  /** The player involved with this event. */
  private final Player player;
}
