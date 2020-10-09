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

import static org.junit.Assert.assertEquals;

import uk.co.notnull.ProxyChat.api.filter.ProxyChatFilter;
import uk.co.notnull.ProxyChat.api.filter.FilterManager;
import uk.co.notnull.ProxyChat.message.Messages;
import org.junit.Test;

public class CapslockFilterTest {
  private static ProxyChatFilter FILTER = new CapslockFilter(8, 50, true);
  private static final FilterHelper filterHelper = new FilterHelper(Messages.ANTI_CAPSLOCK);

  @Test
  public void complexTest() {
    filterHelper.assertNoException(FILTER, "abcdefg");
    filterHelper.assertNoException(FILTER, "abcdefgh");
    filterHelper.assertNoException(FILTER, "abcdefghijklmnop");
    filterHelper.assertNoException(FILTER, "ABCDEFG");
    filterHelper.assertException(FILTER, "ABCDEFGH");
    filterHelper.assertException(FILTER, "ABCDEFGHIJKLMNOP");

    filterHelper.assertNoException(FILTER, "ABCdefgh");
    filterHelper.assertNoException(FILTER, "ABCDefgh");
    filterHelper.assertException(FILTER, "ABCDEfgh");
    filterHelper.assertNoException(FILTER, "ABCDefghi");
    filterHelper.assertException(FILTER, "ABCDEfghi");
  }

  @Test
  public void consoleTest() {
    final ProxyChatFilter filter = new CapslockFilter(8, 50);

    filterHelper.assertNoException(filter, "test");
  }

  @Test
  public void priorityTest() {
    assertEquals(
        "Returned priority is not as expected.",
        FilterManager.CAPSLOCK_FILTER_PRIORITY,
        FILTER.getPriority());
  }

  @Test
  public void simpleTest() {
    filterHelper.assertNoException(FILTER, "hello world!");
    filterHelper.assertException(FILTER, "HELLO WORLD!");
  }
}
