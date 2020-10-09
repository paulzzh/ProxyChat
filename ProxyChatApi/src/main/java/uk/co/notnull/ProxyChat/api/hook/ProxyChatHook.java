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

package uk.co.notnull.ProxyChat.api.hook;

import uk.co.notnull.ProxyChat.api.account.ProxyChatAccount;

import java.util.Optional;

/** This interface is used to get prefixes and suffixes for users from Permission plugins. */
public interface ProxyChatHook extends Comparable<ProxyChatHook> {
  /**
   * Get the active prefix for the passed account.
   *
   * @param account Account to get the prefix for
   * @return Prefix of the Account, if it exists, else {@link Optional#empty}
   */
  public Optional<String> getPrefix(ProxyChatAccount account);

  /**
   * Get the active suffix for the passed account.
   *
   * @param account Account to get the suffix for
   * @return Suffix of the Account, if it exists, else {@link Optional#empty}
   */
  public Optional<String> getSuffix(ProxyChatAccount account);

  /**
   * Retrieves the priority of this hook. Higher priority means the hook is checked earlier.
   *
   * @return Numeric priority of this hook
   */
  public int getPriority();

  @Override
  default int compareTo(ProxyChatHook other) {
    return getPriority() - other.getPriority();
  }
}
