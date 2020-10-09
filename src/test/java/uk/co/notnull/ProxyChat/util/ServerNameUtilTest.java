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

package uk.co.notnull.ProxyChat.util;

import static org.junit.Assert.assertEquals;

import uk.co.notnull.ProxyChat.testhelpers.ServerInfoTest;
import java.util.Arrays;
import java.util.Optional;
import org.junit.Test;

public class ServerNameUtilTest extends ServerInfoTest {
  @Test
  public void getServerInfoTest() {
    assertEquals(Optional.empty(), ServerNameUtil.getServerInfo("h"));
    assertEquals(Optional.empty(), ServerNameUtil.getServerInfo("xxx"));
    assertEquals(Optional.empty(), ServerNameUtil.getServerInfo("mai"));
    assertEquals(Optional.of(servers.get("main")), ServerNameUtil.getServerInfo("main"));
  }

  @Test
  public void verifyServerNameStringTest() {
    assertEquals(Optional.empty(), ServerNameUtil.verifyServerName("h"));
    assertEquals(Optional.empty(), ServerNameUtil.verifyServerName("xxx"));
    assertEquals(Optional.empty(), ServerNameUtil.verifyServerName("mai"));
    assertEquals(Optional.of("main"), ServerNameUtil.verifyServerName("main"));
  }

  @Test
  public void getServerNamesTest() {
    assertEquals(Arrays.asList("main", "hub1", "hub2", "test"), ServerNameUtil.getServerNames());
  }

  @Test
  public void getMatchingServerNamesTest() {
    assertEquals(
        Arrays.asList("main", "hub1", "hub2", "test"), ServerNameUtil.getMatchingServerNames(""));
    assertEquals(Arrays.asList("hub1", "hub2"), ServerNameUtil.getMatchingServerNames("h"));
    assertEquals(Arrays.asList("hub1", "hub2"), ServerNameUtil.getMatchingServerNames("hub"));
    assertEquals(Arrays.asList("hub1"), ServerNameUtil.getMatchingServerNames("hub1"));
    assertEquals(Arrays.asList(), ServerNameUtil.getMatchingServerNames("hub3"));
    assertEquals(Arrays.asList("main"), ServerNameUtil.getMatchingServerNames("main"));
  }
}
