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

package uk.co.notnull.ProxyChat.api.account;

import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.identity.Identified;
import net.kyori.adventure.identity.Identity;
import uk.co.notnull.ProxyChat.api.enums.AccountType;
import uk.co.notnull.ProxyChat.api.enums.ChannelType;
import uk.co.notnull.ProxyChat.api.permission.Permission;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public interface ProxyChatAccount extends Identified {
  String unknownServer = "unknown";

  UUID getUniqueId();

  default AccountType getAccountType() {
    return AccountType.PLAYER;
  }

  ChannelType getChannelType();

  /**
   * Returns the <b>global</b> default channel type for <b>all players</b>!!
   *
   * @return default channel type
   */
  ChannelType getDefaultChannelType();

  boolean isVanished();

  boolean hasMessangerEnabled();

  boolean hasSocialSpyEnabled();

  boolean hasLocalSpyEnabled();

  BlockingQueue<UUID> getIgnored();

  default boolean hasIgnored(UUID uuid) {
    return getIgnored().contains(uuid);
  }

  default boolean hasIgnored(ProxyChatAccount account) {
    return this.hasIgnored(account.getUniqueId());
  }

  String getName();

  default String getDisplayName() {
    return getName();
  }

  int getPing();

  Optional<RegisteredServer> getServer();

  String getServerName();

  String getServerIP();

  Timestamp getMutedUntil();

  default boolean isMuted() {
    return getMutedUntil().after(new Timestamp(System.currentTimeMillis()));
  }

  void setChannelType(ChannelType channelType);

  void setVanished(boolean vanished);

  void setMessanger(boolean messanger);

  void setSocialSpy(boolean socialSpy);

  void setLocalSpy(boolean localSpy);

  default void toggleVanished() {
    setVanished(!isVanished());
  }

  default void toggleMessanger() {
    setMessanger(!hasMessangerEnabled());
  }

  default void toggleSocialSpy() {
    setSocialSpy(!hasSocialSpyEnabled());
  }

  default void toggleLocalSpy() {
    setLocalSpy(!hasLocalSpyEnabled());
  }

  void addIgnore(UUID uuid);

  default void addIgnore(ProxyChatAccount account) {
    this.addIgnore(account.getUniqueId());
  }

  void removeIgnore(UUID uuid);

  default void removeIgnore(ProxyChatAccount account) {
    this.removeIgnore(account.getUniqueId());
  }

  void setMutedUntil(Timestamp mutedUntil);

  default void setMutedUntil(long mutedUntilMillis) {
    setMutedUntil(new Timestamp(mutedUntilMillis));
  }

  default void mutePermanetly() {
    setMutedUntil(Timestamp.valueOf("9999-12-31 23:59:59"));
  }

  default void muteFor(long amount, TimeUnit timeUnit) {
    setMutedUntil(System.currentTimeMillis() + timeUnit.toMillis(amount));
  }

  default void unmute() {
    setMutedUntil(0L);
  }

  boolean hasPermission(Permission permission);

  boolean hasPermission(String permission);

  Identity identity();
}
