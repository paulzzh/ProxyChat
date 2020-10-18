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

package uk.co.notnull.ProxyChat.filter;

import com.google.common.annotations.VisibleForTesting;
import uk.co.notnull.ProxyChat.api.account.ProxyChatAccount;
import uk.co.notnull.ProxyChat.api.filter.BlockMessageException;
import uk.co.notnull.ProxyChat.api.filter.ProxyChatPreParseFilter;
import uk.co.notnull.ProxyChat.api.filter.FilterManager;
import uk.co.notnull.ProxyChat.message.Messages;
import uk.co.notnull.ProxyChat.api.permission.Permission;
import uk.co.notnull.ProxyChat.permission.PermissionManager;

public class CapslockFilter implements ProxyChatPreParseFilter {
  private final int minimumLetterCount;
  private final int maximumCapsPercentage;
  private final boolean noPermissions;

  public CapslockFilter(int minimumLetterCount, int maximumCapsPercentage) {
    this(minimumLetterCount, maximumCapsPercentage, false);
  }

  @VisibleForTesting
  CapslockFilter(int minimumLetterCount, int maximumCapsPercentage, boolean noPermissions) {
    this.minimumLetterCount = minimumLetterCount;
    this.maximumCapsPercentage = maximumCapsPercentage;
    this.noPermissions = noPermissions;
  }

  @Override
  public String applyFilter(ProxyChatAccount sender, String message) throws BlockMessageException {
    if (!noPermissions && sender.hasPermission(Permission.BYPASS_ANTI_CAPSLOCK))
      return message;

    int uppercase = 0;
    int lowercase = 0;

    for (char c : message.toCharArray()) {
      if (Character.isUpperCase(c)) {
        uppercase++;
      } else if (Character.isLowerCase(c)) {
        lowercase++;
      }
    }

    int total = uppercase + lowercase;

    if (total < minimumLetterCount) return message;

    if (((uppercase * 100) / total) > maximumCapsPercentage)
      throw new ExtendedBlockMessageException(Messages.ANTI_CAPSLOCK, sender);

    return message;
  }

  @Override
  public int getPriority() {
    return FilterManager.CAPSLOCK_FILTER_PRIORITY;
  }
}
