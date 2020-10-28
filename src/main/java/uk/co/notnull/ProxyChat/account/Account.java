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

package uk.co.notnull.ProxyChat.account;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.identity.Identity;
import org.checkerframework.checker.nullness.qual.NonNull;
import uk.co.notnull.ProxyChat.ProxyChat;
import uk.co.notnull.ProxyChat.api.account.ProxyChatAccount;
import uk.co.notnull.ProxyChat.api.enums.ChannelType;
import uk.co.notnull.ProxyChat.module.ProxyChatModuleManager;
import uk.co.notnull.ProxyChat.api.permission.Permission;
import uk.co.notnull.ProxyChat.util.DummyPlayer;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Data
@EqualsAndHashCode(of = "uuid")
public class Account implements ProxyChatAccount {
  protected static ChannelType defaultChannelType = ChannelType.LOCAL;

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private UUID uuid;

  @Getter
  private final Player player;

  private ChannelType channelType;
  private boolean vanished;

  @Getter(AccessLevel.NONE)
  private boolean messanger;

  @Getter(AccessLevel.NONE)
  private boolean socialSpy;

  @Getter(AccessLevel.NONE)
  private boolean localSpy;

  private final BlockingQueue<UUID> ignored;
  private Timestamp mutedUntil;

  protected Account(UUID uuid) {
    this.uuid = uuid;

    player = (Player) ProxyChatAccountManager.getCommandSource(uuid).orElse(new DummyPlayer(uuid));

    if(player instanceof DummyPlayer) {
      ProxyChat.getInstance().getLogger().error("Couldn't get player for uuid " + uuid + ". This is probably a bug!");
    }

    channelType = defaultChannelType;
    vanished = false;
    messanger = true;
    socialSpy = false;
    localSpy = false;
    ignored = new LinkedBlockingQueue<>();
    mutedUntil = new Timestamp(0);
  }

  protected Account(
      UUID uuid,
      ChannelType channelType,
      boolean vanished,
      boolean messanger,
      boolean socialSpy,
      boolean localSpy,
      BlockingQueue<UUID> ignored,
      Timestamp mutedUntil) {
    this.uuid = uuid;
    this.player = ProxyChat.getInstance().getProxy().getPlayer(uuid).orElse(new DummyPlayer(uuid));
    this.channelType = channelType;
    this.vanished = vanished;
    this.messanger = messanger;
    this.socialSpy = socialSpy;
    this.localSpy = localSpy;
    this.ignored = ignored;
    this.mutedUntil = mutedUntil;
  }

  @Override
  public UUID getUniqueId() {
    return uuid;
  }

  @Override
  public ChannelType getDefaultChannelType() {
    return defaultChannelType;
  }

  @Override
  public boolean hasMessangerEnabled() {
    return messanger;
  }

  @Override
  public boolean hasSocialSpyEnabled() {
    if (socialSpy && !hasPermission(Permission.COMMAND_SOCIALSPY)) {
      setSocialSpy(false);
    }

    return socialSpy;
  }

  @Override
  public boolean hasLocalSpyEnabled() {
    if (localSpy && !hasPermission(Permission.COMMAND_LOCALSPY)) {
      setLocalSpy(false);
    }

    return localSpy;
  }

  @Override
  public BlockingQueue<UUID> getIgnored() {
    return ProxyChatModuleManager.IGNORING_MODULE.isEnabled()
        ? ignored
        : new LinkedBlockingQueue<>();
  }

  public boolean hasIgnored(Player player) {
    return hasIgnored(player.getUniqueId());
  }

  @Override
  public void addIgnore(UUID uuid) {
    ignored.add(uuid);
  }

  public void addIgnore(Player player) {
    this.addIgnore(player.getUniqueId());
  }

  @Override
  public void removeIgnore(UUID uuid) {
    ignored.remove(uuid);
  }

  public void removeIgnore(Player player) {
    this.removeIgnore(player.getUniqueId());
  }

  @Override
  public String getName() {
    return getPlayer().getUsername();
  }

  @Override
  public String getDisplayName() {
    return getPlayer().getGameProfile().getName();
  }

  @Override
  public int getPing() {
    return (int) getPlayer().getPing();
  }

  @Override
  public Optional<RegisteredServer> getServer() {
    Optional<ServerConnection> server = getPlayer().getCurrentServer();
    return server.map(ServerConnection::getServer);
  }

  @Override
  public String getServerName() {
    return getServer().map(server -> server.getServerInfo().getName()).orElse(unknownServer);
  }

  @Override
  public String getServerIP() {
    return getServer().map(server -> server.getServerInfo().getAddress().toString()).orElse(unknownServer);
  }

  public static void setDefaultChannelType(ChannelType channelType) {
    defaultChannelType = channelType;
  }

  @Override
  public String toString() {
    return getName();
  }

  @Override
  public @NonNull Identity identity() {
    return Identity.identity(uuid);
  }

  @Override
  public boolean hasPermission(String permission) {
    return player.hasPermission(permission);
  }

  @Override
  public boolean hasPermission(Permission permission) {
    return player.hasPermission(permission.getStringedPermission());
  }
}
