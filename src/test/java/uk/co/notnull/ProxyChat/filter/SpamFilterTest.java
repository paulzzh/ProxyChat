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

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SpamFilterTest {
  private static SpamFilter FILTER = new SpamFilter(3, true);
  private static final FilterHelper filterHelper = new FilterHelper(Messages.ANTI_SPAM);

  @BeforeClass
  public static void setupFilter() {
    SpamFilter.expiryTimer = TimeUnit.SECONDS.toNanos(5);
  }

  @Before
  public void clearFilter() {
    FILTER.clear();
  }

  @Test
  public void complexTest() throws InterruptedException {
    filterHelper.assertNoException(FILTER, "hello world!");

    TimeUnit.SECONDS.sleep(1);

    filterHelper.assertNoException(FILTER, "hello world!");

    TimeUnit.SECONDS.sleep(1);

    filterHelper.assertNoException(FILTER, "hello world!");

    TimeUnit.SECONDS.sleep(1);

    filterHelper.assertException(FILTER, "hello world!");

    TimeUnit.SECONDS.sleep(1);

    filterHelper.assertException(FILTER, "hello world!");

    TimeUnit.SECONDS.sleep(1);

    filterHelper.assertNoException(FILTER, "hello world!");
    filterHelper.assertException(FILTER, "hello world!");

    TimeUnit.SECONDS.sleep(2);

    filterHelper.assertNoException(FILTER, "hello world!");
    filterHelper.assertNoException(FILTER, "hello world!");
    filterHelper.assertException(FILTER, "hello world!");

    TimeUnit.SECONDS.sleep(5);

    filterHelper.assertNoException(FILTER, "hello world!");
    filterHelper.assertNoException(FILTER, "hello world!");
    filterHelper.assertNoException(FILTER, "hello world!");
    filterHelper.assertException(FILTER, "hello world!");
    filterHelper.assertException(FILTER, "hello world!");
    filterHelper.assertException(FILTER, "hello world!");
  }

  @Test
  public void consoleTest() {
    final ProxyChatFilter filter = new SpamFilter(3);

    filterHelper.assertNoException(filter, "test");
  }

  @Test
  public void priorityTest() {
    Assert.assertEquals(
			"Returned priority is not as expected.",
			FilterManager.SPAM_FILTER_PRIORITY,
			FILTER.getPriority());
  }

  @Test
  public void simpleTest() {
    filterHelper.assertNoException(FILTER, "hello world!");
    filterHelper.assertNoException(FILTER, "hello world!");
    filterHelper.assertNoException(FILTER, "hello world!");
    filterHelper.assertException(FILTER, "hello world!");
    filterHelper.assertException(FILTER, "hello world!");
    filterHelper.assertException(FILTER, "hello world!");
  }
}
