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
import uk.co.notnull.ProxyChat.api.enums.AccountType;
import uk.co.notnull.ProxyChat.api.enums.ChannelType;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import lombok.Getter;
import lombok.Setter;

public class ConsoleAccount implements ProxyChatAccount {
  private static final UUID uuid = new UUID(0, 0);
  private static final BlockingQueue<UUID> ignoreList = new LinkedBlockingQueue<>();

  @Getter @Setter private Optional<String> storedPrefix;
  @Getter @Setter private Optional<String> storedSuffix;

  protected ConsoleAccount() {
    storedPrefix = Optional.empty();
    storedSuffix = Optional.empty();
  }

  @Override
  public UUID getUniqueId() {
    return uuid;
  }

  @Override
  public AccountType getAccountType() {
    return AccountType.CONSOLE;
  }

  @Override
  public ChannelType getChannelType() {
    return ChannelType.GLOBAL;
  }

  @Override
  public ChannelType getDefaultChannelType() {
    return ChannelType.GLOBAL;
  }

  @Override
  public boolean isVanished() {
    return true;
  }

  @Override
  public boolean hasMessangerEnabled() {
    return true;
  }

  @Override
  public boolean hasSocialSpyEnabled() {
    return false;
  }

  @Override
  public boolean hasLocalSpyEnabled() {
    return false;
  }

  @Override
  public BlockingQueue<UUID> getIgnored() {
    return ignoreList;
  }

  @Override
  public boolean hasIgnored(UUID uuid) {
    return false;
  }

  @Override
  public String getName() {
    return "~Console~";
  }

  @Override
  public int getPing() {
    return 0;
  }

  @Override
  public Optional<RegisteredServer> getServer() {
    return null;
  }

  @Override
  public String getServerName() {
    return "none";
  }

  @Override
  public String getServerIP() {
    return "127.0.0.1";
  }

  @Override
  public Timestamp getMutedUntil() {
    return new Timestamp(0);
  }

  @Override
  public void setChannelType(ChannelType channelType) {
    // Do nothing
  }

  @Override
  public void setDefaultChannelType(ChannelType channelType) {
    // Do nothing
  }

  @Override
  public void setVanished(boolean vanished) {
    // Do nothing
  }

  @Override
  public void setMessanger(boolean messanger) {
    // Do nothing
  }

  @Override
  public void setSocialSpy(boolean socialSpy) {
    // Do nothing
  }

  @Override
  public void setLocalSpy(boolean localSpy) {
    // Do nothing
  }

  @Override
  public void addIgnore(UUID uuid) {
    // Do nothing
  }

  @Override
  public void removeIgnore(UUID uuid) {
    // Do nothing
  }

  @Override
  public void setMutedUntil(Timestamp mutedUntil) {
    // Do nothing
  }

  @Override
  public String toString() {
    return getName();
  }
}
