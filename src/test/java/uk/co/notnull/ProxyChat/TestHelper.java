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

package uk.co.notnull.ProxyChat;

import com.google.common.base.Preconditions;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.PluginDescription;
import com.velocitypowered.api.plugin.PluginManager;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.config.ProxyConfig;
import com.velocitypowered.api.proxy.messages.ChannelRegistrar;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import com.velocitypowered.api.scheduler.Scheduler;
import com.velocitypowered.api.util.ProxyVersion;
import com.velocitypowered.api.util.bossbar.BossBar;
import com.velocitypowered.api.util.bossbar.BossBarColor;
import com.velocitypowered.api.util.bossbar.BossBarOverlay;
import uk.co.notnull.ProxyChat.config.Configuration;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import net.kyori.text.Component;
import org.apache.commons.io.FileUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

@UtilityClass
public class TestHelper {
  private static ProxyChat proxyChat;
  private static ProxyServer proxyServer;
  private static boolean hasInitRun = false;

  @SneakyThrows
  public static void initProxyChat() {
    if (!hasInitRun) {
      proxyServer = new DummyProxyServer();
      PluginDescription desc = new PluginDescription();

      ProxyServer.setInstance(proxyServer);

      proxyChat = new ProxyChat(proxyServer, desc);

      if (proxyChat.getProxy() == null) {
        Method init =
            Plugin.class.getDeclaredMethod("init", ProxyServer.class, PluginDescription.class);
        init.setAccessible(true);
        init.invoke(proxyChat, proxyServer, desc);
      }

      proxyChat.onLoad();

      hasInitRun = true;
    }

    Configuration.load();
  }

  public static void deinitProxyChat() throws IOException {
    File directory = proxyChat.getConfigFolder();

    if(directory == null) {
      return;
    }

    FileUtils.deleteDirectory(directory);
    Preconditions.checkState(
            proxyChat.getConfigFolder().mkdirs(), "Could not create config folder");
  }

  public static class DummyProxyServer implements ProxyServer {
    @Getter private PluginManager pluginManager;

    @Getter
    private File pluginsFolder =
        new File(System.getProperty("java.io.tmpdir"), "ProxyChatTest/" + UUID.randomUUID());

    @Getter private Logger logger = Logger.getLogger("DummyProxyServer");

    protected DummyProxyServer() {
      pluginManager = new PluginManager() {
        @Override
        public Optional<PluginContainer> fromInstance(Object instance) {
          return Optional.empty();
        }

        @Override
        public Optional<PluginContainer> getPlugin(String id) {
          return Optional.empty();
        }

        @Override
        public Collection<PluginContainer> getPlugins() {
          return null;
        }

        @Override
        public boolean isLoaded(String id) {
          return false;
        }

        @Override
        public void addToClasspath(Object plugin, Path path) {

        }
      };
    }

    @Override
    public ProxyVersion getVersion() {
      return null;
    }

    @Override
    public @NonNull BossBar createBossBar(
            @NonNull Component title, @NonNull BossBarColor color, @NonNull BossBarOverlay overlay, float progress) {
      return null;
    }

    @Override
    public Optional<Player> getPlayer(String name) {
      return null;
    }

    @Override
    public Optional<Player> getPlayer(UUID uuid) {
      return null;
    }

    @Override
    public void broadcast(Component component) {

    }

    @Override
    public Collection<Player> getAllPlayers() {
      return null;
    }

    @Override
    public int getPlayerCount() {
      return 0;
    }

    @Override
    public Optional<RegisteredServer> getServer(String name) {
      return Optional.empty();
    }

    @Override
    public Collection<RegisteredServer> getAllServers() {
      return null;
    }

    @Override
    public ChannelRegistrar getChannelRegistrar() {
      return null;
    }

    @Override
    public InetSocketAddress getBoundAddress() {
      return null;
    }

    @Override
    public ProxyConfig getConfiguration() {
      return null;
    }

    @Override
    public Collection<Player> matchPlayer(String match) {
      return null;
    }

    @Override
    public Collection<RegisteredServer> matchServer(String partialName) {
      return null;
    }

    @Override
    public RegisteredServer registerServer(ServerInfo server) {
      return null;
    }

    @Override
    public void unregisterServer(ServerInfo server) {

    }

    @Override
    public ConsoleCommandSource getConsoleCommandSource() {
      return null;
    }

    @Override
    public EventManager getEventManager() {
      return null;
    }

    @Override
    public CommandManager getCommandManager() {
      return null;
    }

    @Override
    public Scheduler getScheduler() {
      return null;
    }

    @Override
    public ServerInfo constructServerInfo(
        String name, SocketAddress address, String motd, boolean restricted) {
      return null;
    }
  }
}
