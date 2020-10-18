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
import uk.co.notnull.ProxyChat.api.filter.ProxyChatPreParseFilter;
import uk.co.notnull.ProxyChat.api.filter.FilterManager;
import uk.co.notnull.ProxyChat.api.utils.RegexUtil;
import uk.co.notnull.ProxyChat.api.permission.Permission;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SwearWordsFilter implements ProxyChatPreParseFilter {
  private final List<Pattern> swearWords;
  private final String replacement;

  public SwearWordsFilter(
      List<String> swearWords,
      String replacement,
      boolean freeMatching,
      boolean leetSpeak,
      boolean ignoreSpaces,
      boolean ignoreDuplicateLetters) {
    this.swearWords =
        swearWords.stream()
            .map(
                word ->
                    RegexUtil.parseWildcardToPattern(
                        word,
                        Pattern.CASE_INSENSITIVE,
                        freeMatching,
                        leetSpeak,
                        ignoreSpaces,
                        ignoreDuplicateLetters))
            .collect(Collectors.toList());
    this.replacement = replacement;
  }

  @Override
  public String applyFilter(ProxyChatAccount sender, String message) {
    if (sender.hasPermission(Permission.BYPASS_ANTI_SWEAR)) return message;

    for (Pattern p : swearWords) {
      message = p.matcher(message).replaceAll(replacement);
    }

    return message;
  }

  @Override
  public int getPriority() {
    return FilterManager.SWEAR_FILTER_PRIORITY;
  }
}
