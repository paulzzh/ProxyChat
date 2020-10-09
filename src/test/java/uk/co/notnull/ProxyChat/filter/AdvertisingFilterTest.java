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
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class AdvertisingFilterTest {
  private static final ProxyChatFilter FILTER =
      new AdvertisingFilter(Arrays.asList("www.google.com", "*.net"), true);
  private static final FilterHelper filterHelper = new FilterHelper(Messages.ANTI_ADVERTISE);

  @Test
  public void consoleTest() {
    final ProxyChatFilter filter = new AdvertisingFilter(Arrays.asList());

    filterHelper.assertNoException(filter, "test");
  }

  @Test
  public void priorityTest() {
    Assert.assertEquals(
			"Returned priority is not as expected.",
			FilterManager.ADVERTISING_FILTER_PRIORITY,
			FILTER.getPriority());
  }

  @Test
  public void urlFormatTest() {
    filterHelper.assertException(FILTER, "web.de");
    filterHelper.assertException(FILTER, "http://web.de");
    filterHelper.assertException(FILTER, "https://web.de");
    filterHelper.assertException(FILTER, "www.web.de");
    filterHelper.assertException(FILTER, "http://www.web.de");
    filterHelper.assertException(FILTER, "https://www.web.de");
    filterHelper.assertException(FILTER, "web.de/testUrl.php?bla=baum&foo=bar");
    filterHelper.assertException(FILTER, "http://web.de/testUrl.php?bla=baum&foo=bar");
    filterHelper.assertException(FILTER, "https://web.de/testUrl.php?bla=baum&foo=bar");
    filterHelper.assertException(FILTER, "www.web.de/testUrl.php?bla=baum&foo=bar");
    filterHelper.assertException(FILTER, "http://www.web.de/testUrl.php?bla=baum&foo=bar");
    filterHelper.assertException(FILTER, "https://www.web.de/testUrl.php?bla=baum&foo=bar");
    filterHelper.assertException(FILTER, "text web.de");
    filterHelper.assertException(FILTER, "text http://web.de");
    filterHelper.assertException(FILTER, "text https://web.de");
    filterHelper.assertException(FILTER, "text www.web.de");
    filterHelper.assertException(FILTER, "text http://www.web.de");
    filterHelper.assertException(FILTER, "text https://www.web.de");
    filterHelper.assertException(FILTER, "text web.de/testUrl.php?bla=baum&foo=bar");
    filterHelper.assertException(FILTER, "text http://web.de/testUrl.php?bla=baum&foo=bar");
    filterHelper.assertException(FILTER, "text https://web.de/testUrl.php?bla=baum&foo=bar");
    filterHelper.assertException(FILTER, "text www.web.de/testUrl.php?bla=baum&foo=bar");
    filterHelper.assertException(FILTER, "text http://www.web.de/testUrl.php?bla=baum&foo=bar");
    filterHelper.assertException(FILTER, "text https://www.web.de/testUrl.php?bla=baum&foo=bar");
    filterHelper.assertException(FILTER, "web.de foobar");
    filterHelper.assertException(FILTER, "http://web.de foobar");
    filterHelper.assertException(FILTER, "https://web.de foobar");
    filterHelper.assertException(FILTER, "www.web.de foobar");
    filterHelper.assertException(FILTER, "http://www.web.de foobar");
    filterHelper.assertException(FILTER, "https://www.web.de foobar");
    filterHelper.assertException(FILTER, "web.de/testUrl.php?bla=baum&foo=bar foobar");
    filterHelper.assertException(FILTER, "http://web.de/testUrl.php?bla=baum&foo=bar foobar");
    filterHelper.assertException(FILTER, "https://web.de/testUrl.php?bla=baum&foo=bar foobar");
    filterHelper.assertException(FILTER, "www.web.de/testUrl.php?bla=baum&foo=bar foobar");
    filterHelper.assertException(FILTER, "http://www.web.de/testUrl.php?bla=baum&foo=bar foobar");
    filterHelper.assertException(FILTER, "https://www.web.de/testUrl.php?bla=baum&foo=bar foobar");
    filterHelper.assertException(FILTER, "text web.de foobar");
    filterHelper.assertException(FILTER, "text http://web.de foobar");
    filterHelper.assertException(FILTER, "text https://web.de foobar");
    filterHelper.assertException(FILTER, "text www.web.de foobar");
    filterHelper.assertException(FILTER, "text http://www.web.de foobar");
    filterHelper.assertException(FILTER, "text https://www.web.de foobar");
    filterHelper.assertException(FILTER, "text web.de/testUrl.php?bla=baum&foo=bar foobar");
    filterHelper.assertException(FILTER, "text http://web.de/testUrl.php?bla=baum&foo=bar foobar");
    filterHelper.assertException(FILTER, "text https://web.de/testUrl.php?bla=baum&foo=bar foobar");
    filterHelper.assertException(FILTER, "text www.web.de/testUrl.php?bla=baum&foo=bar foobar");
    filterHelper.assertException(
        FILTER, "text http://www.web.de/testUrl.php?bla=baum&foo=bar foobar");
    filterHelper.assertException(
        FILTER, "text https://www.web.de/testUrl.php?bla=baum&foo=bar foobar");
  }

  @Test
  public void whitelistTest() {
    filterHelper.assertException(FILTER, "web.de");
    filterHelper.assertException(FILTER, "www.web.de");
    filterHelper.assertException(FILTER, "google.com");
    filterHelper.assertNoException(FILTER, "www.google.com");
    filterHelper.assertNoException(FILTER, "foobar.net");
    filterHelper.assertNoException(FILTER, "www.foobar.net");
    filterHelper.assertException(FILTER, "http://web.de");
    filterHelper.assertException(FILTER, "http://www.web.de");
    filterHelper.assertException(FILTER, "http://google.com");
    filterHelper.assertNoException(FILTER, "http://www.google.com");
    filterHelper.assertNoException(FILTER, "http://foobar.net");
    filterHelper.assertNoException(FILTER, "http://www.foobar.net");
    filterHelper.assertException(FILTER, "https://web.de");
    filterHelper.assertException(FILTER, "https://www.web.de");
    filterHelper.assertException(FILTER, "https://google.com");
    filterHelper.assertNoException(FILTER, "https://www.google.com");
    filterHelper.assertNoException(FILTER, "https://foobar.net");
    filterHelper.assertNoException(FILTER, "https://www.foobar.net");
    filterHelper.assertException(FILTER, "web.de/testUrl.php?bla=baum&foo=bar");
    filterHelper.assertException(FILTER, "www.web.de/testUrl.php?bla=baum&foo=bar");
    filterHelper.assertException(FILTER, "google.com/testUrl.php?bla=baum&foo=bar");
    filterHelper.assertNoException(FILTER, "www.google.com/testUrl.php?bla=baum&foo=bar");
    filterHelper.assertNoException(FILTER, "foobar.net/testUrl.php?bla=baum&foo=bar");
    filterHelper.assertNoException(FILTER, "www.foobar.net/testUrl.php?bla=baum&foo=bar");
    filterHelper.assertException(FILTER, "http://web.de/testUrl.php?bla=baum&foo=bar");
    filterHelper.assertException(FILTER, "http://www.web.de/testUrl.php?bla=baum&foo=bar");
    filterHelper.assertException(FILTER, "http://google.com/testUrl.php?bla=baum&foo=bar");
    filterHelper.assertNoException(FILTER, "http://www.google.com/testUrl.php?bla=baum&foo=bar");
    filterHelper.assertNoException(FILTER, "http://foobar.net/testUrl.php?bla=baum&foo=bar");
    filterHelper.assertNoException(FILTER, "http://www.foobar.net/testUrl.php?bla=baum&foo=bar");
    filterHelper.assertException(FILTER, "https://web.de/testUrl.php?bla=baum&foo=bar");
    filterHelper.assertException(FILTER, "https://www.web.de/testUrl.php?bla=baum&foo=bar");
    filterHelper.assertException(FILTER, "https://google.com/testUrl.php?bla=baum&foo=bar");
    filterHelper.assertNoException(FILTER, "https://www.google.com/testUrl.php?bla=baum&foo=bar");
    filterHelper.assertNoException(FILTER, "https://foobar.net/testUrl.php?bla=baum&foo=bar");
    filterHelper.assertNoException(FILTER, "https://www.foobar.net/testUrl.php?bla=baum&foo=bar");
  }
}
