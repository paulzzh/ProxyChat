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

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class SpamFilter implements ProxyChatPreParseFilter {
  @VisibleForTesting static long expiryTimer = TimeUnit.MINUTES.toNanos(1);

  private final ConcurrentMap<UUID, Queue<Long>> playerMessageTimepointStorage;
  private final int messagesPerMinute;
  private final boolean noPermissions;

  public SpamFilter(int messagesPerMinute) {
    this(messagesPerMinute, false);
  }

  @VisibleForTesting
  SpamFilter(int messagesPerMinute, boolean noPermissions) {
    playerMessageTimepointStorage = new ConcurrentHashMap<>();
    this.messagesPerMinute = messagesPerMinute;
    this.noPermissions = noPermissions;
  }

  @Override
  public String applyFilter(ProxyChatAccount sender, String message) throws BlockMessageException {
    if (!noPermissions && sender.hasPermission(Permission.BYPASS_ANTI_SPAM))
      return message;

    final UUID uuid = sender.getUniqueId();

    if (!playerMessageTimepointStorage.containsKey(uuid)) {
      playerMessageTimepointStorage.put(uuid, new ArrayDeque<>(messagesPerMinute));
    }

    final Queue<Long> timePoints = playerMessageTimepointStorage.get(uuid);
    final long now = System.nanoTime();
    final long expiry = now - expiryTimer;

    while (!timePoints.isEmpty() && (timePoints.peek() < expiry)) {
      timePoints.poll();
    }

    if (timePoints.size() >= messagesPerMinute)
      throw new ExtendedBlockMessageException(Messages.ANTI_SPAM, sender);

    timePoints.add(now);

    return message;
  }

  @Override
  public int getPriority() {
    return FilterManager.SPAM_FILTER_PRIORITY;
  }

  @VisibleForTesting
  void clear() {
    playerMessageTimepointStorage.clear();
  }
}
