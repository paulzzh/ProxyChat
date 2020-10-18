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

import uk.co.notnull.ProxyChat.api.account.ProxyChatAccount;
import uk.co.notnull.ProxyChat.api.filter.BlockMessageException;
import uk.co.notnull.ProxyChat.api.filter.ProxyChatPreParseFilter;
import uk.co.notnull.ProxyChat.api.filter.FilterManager;
import uk.co.notnull.ProxyChat.api.utils.RegexUtil;
import uk.co.notnull.ProxyChat.message.Messages;
import uk.co.notnull.ProxyChat.api.permission.Permission;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdvertisingFilter implements ProxyChatPreParseFilter {
  /**
   * Regex from <a href=
   * "https://gist.github.com/dperini/729294">https://gist.github.com/dperini/729294</a>. <br>
   * Slightly modified. Allowed dropping of the protocol. So <code>google.com</code> still matches
   * and removed the start and end anchors!
   */
  private static final Pattern url =
      Pattern.compile(
          "(?:(?:https?|ftp)://)?(?:\\S+(?::\\S*)?@)?(?:(?!(?:10|127)(?:\\.\\d{1,3}){3})(?!(?:169\\.254|192\\.168)(?:\\.\\d{1,3}){2})(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)(?:\\.(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)*(?:\\.(?:[a-z\\u00a1-\\uffff]{2,}))\\.?)(?::\\d{2,5})?(?:[/?#]\\S*)?",
          Pattern.CASE_INSENSITIVE);

  private final Predicate<String> whitelisted;
  private final boolean noPermissions;

  public AdvertisingFilter(List<String> whitelisted) {
    this(whitelisted, false);
  }

  public AdvertisingFilter(List<String> whitelisted, boolean noPermissions) {
    this.whitelisted =
        whitelisted.stream()
            .map(
                wildcard ->
                    RegexUtil.parseWildcardToPattern(
                            wildcard, Pattern.CASE_INSENSITIVE, true, false, false, false)
                        .asPredicate())
            .reduce(Predicate::or)
            .orElse(x -> false);
    this.noPermissions = noPermissions;
  }

  @Override
  public String applyFilter(ProxyChatAccount sender, String message) throws BlockMessageException {
    if (!noPermissions
        && sender.hasPermission(Permission.BYPASS_ANTI_ADVERTISEMENT))
      return message;

    Matcher matches = url.matcher(message);
    boolean matchOk;
    String match;

    while (matches.find()) {
      match = matches.group();
      matchOk = whitelisted.test(match);

      if (!matchOk)
        throw new ExtendedBlockMessageException(Messages.ANTI_ADVERTISE, sender);
    }

    return message;
  }

  @Override
  public int getPriority() {
    return FilterManager.ADVERTISING_FILTER_PRIORITY;
  }
}
