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
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Test;

public class DuplicationFilterTest {
  private static DuplicationFilter FILTER = new DuplicationFilter(2, 1, true);
  private static final FilterHelper filterHelper = new FilterHelper(Messages.ANTI_DUPLICATION);

  @Before
  public void clearFilter() {
    FILTER.clear();
  }

  @Test
  public void complexTest() throws InterruptedException {
    filterHelper.assertNoException(FILTER, "test1");
    filterHelper.assertNoException(FILTER, "test2");
    filterHelper.assertException(FILTER, "test1");
    filterHelper.assertException(FILTER, "test2");
    filterHelper.assertNoException(FILTER, "test3");
    filterHelper.assertException(FILTER, "test2");
    filterHelper.assertNoException(FILTER, "test1");
    filterHelper.assertNoException(FILTER, "test2");

    filterHelper.assertException(FILTER, "test1");
    filterHelper.assertException(FILTER, "test2");

    TimeUnit.SECONDS.sleep(1);

    filterHelper.assertNoException(FILTER, "test1");
    filterHelper.assertNoException(FILTER, "test2");
    filterHelper.assertException(FILTER, "test1");
    filterHelper.assertException(FILTER, "test2");
  }

  @Test
  public void consoleTest() {
    final ProxyChatFilter filter = new DuplicationFilter(0, 0);

    filterHelper.assertNoException(filter, "test");
  }

  @Test
  public void priorityTest() {
    assertEquals(
        "Returned priority is not as expected.",
        FilterManager.DUPLICATION_FILTER_PRIORITY,
        FILTER.getPriority());
  }

  @Test
  public void simpleTest() {
    filterHelper.assertNoException(FILTER, "test");
    filterHelper.assertException(FILTER, "test");
  }
}
