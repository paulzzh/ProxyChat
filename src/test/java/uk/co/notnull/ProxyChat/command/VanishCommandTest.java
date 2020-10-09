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

import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.mockito.Mockito;

public class VanishCommandTest {
  private static final VanishCommand handler =
      Mockito.mock(VanishCommand.class, Mockito.CALLS_REAL_METHODS);

  private static Collection<String> tabComplete(String... args) {
    return handler.tabComplete(null, args);
  }

  @Test
  public void tabCompletefirstArgumentTest() {
    assertEquals(Arrays.asList("on", "off"), tabComplete(""));
    assertEquals(Arrays.asList("on", "off"), tabComplete("o"));
    assertEquals(Arrays.asList("on"), tabComplete("on"));
    assertEquals(Arrays.asList("off"), tabComplete("of"));
    assertEquals(Arrays.asList("off"), tabComplete("off"));
    assertEquals(Arrays.asList(), tabComplete("xxx"));
  }

  @Test
  public void tabCompleteExtraArgumentsTest() {
    assertEquals(Arrays.asList(), tabComplete("on", ""));
    assertEquals(Arrays.asList(), tabComplete("on", "p"));
    assertEquals(Arrays.asList(), tabComplete("on", "player1"));
    assertEquals(Arrays.asList(), tabComplete("off", ""));
    assertEquals(Arrays.asList(), tabComplete("off", "p"));
    assertEquals(Arrays.asList(), tabComplete("off", "player1"));

    assertEquals(Arrays.asList(), tabComplete("on", "xxx", ""));
    assertEquals(Arrays.asList(), tabComplete("on", "xxx", "p"));
    assertEquals(Arrays.asList(), tabComplete("on", "xxx", "player1"));
    assertEquals(Arrays.asList(), tabComplete("off", "xxx", ""));
    assertEquals(Arrays.asList(), tabComplete("off", "xxx", "p"));
    assertEquals(Arrays.asList(), tabComplete("off", "xxx", "player1"));
  }
}
