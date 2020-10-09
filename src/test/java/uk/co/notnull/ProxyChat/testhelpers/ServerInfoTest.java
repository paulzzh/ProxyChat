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

package uk.co.notnull.ProxyChat.testhelpers;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.BeforeClass;
import org.mockito.Mockito;

public abstract class ServerInfoTest {
  @SuppressFBWarnings(
      value = {"MS_PKGPROTECT", "MS_CANNOT_BE_FINAL"},
      justification = "Child classes need access to it.")
  protected static Map<String, ServerInfo> servers;

  @BeforeClass
  public static void setupProxyServer() {
    servers = new LinkedHashMap<>(); // LinkedHashMaps keep insertion order
    final ProxyServer mockProxyServer = Mockito.mock(ProxyServer.class);

    addMockServer("main");
    addMockServer("hub1");
    addMockServer("hub2");
    addMockServer("test");

    Mockito.when(mockProxyServer.getServers()).thenReturn(servers);

    ProxyServer.setInstance(mockProxyServer);
  }

  private static void addMockServer(String serverName) {
    final ServerInfo server = Mockito.mock(ServerInfo.class);

    Mockito.when(server.getName()).thenReturn(serverName);

    servers.put(serverName, server);
  }
}
