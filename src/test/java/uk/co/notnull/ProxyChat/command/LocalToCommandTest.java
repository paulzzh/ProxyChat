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

package uk.co.notnull.ProxyChat.command;

import static org.junit.Assert.assertEquals;

import uk.co.notnull.ProxyChat.testhelpers.ServerInfoTest;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.mockito.Mockito;

public class LocalToCommandTest extends ServerInfoTest {
  private static final LocalToCommand handler =
      Mockito.mock(LocalToCommand.class, Mockito.CALLS_REAL_METHODS);

  private static Collection<String> tabComplete(String... args) {
    return handler.tabComplete(null, args);
  }

  @Test
  public void tabCompletefirstArgumentTest() {
    assertEquals(Arrays.asList("main", "hub1", "hub2", "test"), tabComplete(""));
    assertEquals(Arrays.asList("hub1", "hub2"), tabComplete("h"));
    assertEquals(Arrays.asList("test"), tabComplete("tes"));
    assertEquals(Arrays.asList("main"), tabComplete("main"));
    assertEquals(Arrays.asList(), tabComplete("xxx"));
  }

  @Test
  public void tabCompleteExtraArgumentsTest() {
    assertEquals(Arrays.asList(), tabComplete("main", ""));
    assertEquals(Arrays.asList(), tabComplete("main", "test"));

    assertEquals(Arrays.asList(), tabComplete("main", "test", ""));
    assertEquals(Arrays.asList(), tabComplete("main", "test", "test"));
  }
}
