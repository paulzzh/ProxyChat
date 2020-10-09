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

import uk.co.notnull.ProxyChat.api.enums.AccountType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import lombok.Getter;

public class AccountManager {
  @Getter protected static final ProxyChatAccount consoleAccount = new ConsoleAccount();
  protected static final ConcurrentMap<UUID, ProxyChatAccount> accounts =
      new ConcurrentHashMap<>();
  @Getter private static ProxyChatAccountStorage accountStorage;

  public static void setAccountStorage(ProxyChatAccountStorage accountStorage) {
    AccountManager.accountStorage = accountStorage;

    if (accountStorage.requiresConsoleAccountSave()) {
      saveAccount(consoleAccount);
    }
  }

  public static Optional<ProxyChatAccount> getAccount(UUID uuid) {
    return Optional.ofNullable(accounts.get(uuid));
  }

  public static Optional<ProxyChatAccount> getAccount(String name) {
    List<ProxyChatAccount> accounts = getAccountsForPartialName(name);

    if (accounts.size() == 1) {
      // See if a partial match is enough to identify one player
      return Optional.of(accounts.get(0));
    } else {
      // If there are several, we need to make sure the full name or UUID matches
      return accounts.stream()
          .filter(
              account ->
                  name.equalsIgnoreCase(account.getName())
                      || name.equalsIgnoreCase(account.getUniqueId().toString()))
          .findFirst();
    }
  }

  public static List<ProxyChatAccount> getAccounts() {
    return new ArrayList<>(accounts.values());
  }

  public static List<ProxyChatAccount> getPlayerAccounts() {
    return accounts.values().stream()
        .filter(account -> account.getAccountType() == AccountType.PLAYER)
        .collect(Collectors.toList());
  }

  public static List<ProxyChatAccount> getAccountsForPartialName(String partialName) {
    final String lowercasePartialName = partialName.toLowerCase();

    return accounts.values().stream()
        .filter(
            account ->
                account.getName().toLowerCase().startsWith(lowercasePartialName)
                    || account.getUniqueId().toString().startsWith(lowercasePartialName))
        .collect(Collectors.toList());
  }

  public static void loadAccount(UUID uuid) {
    AccountInfo loadedAccount = accountStorage.load(uuid);

    accounts.put(uuid, loadedAccount.getAccount());

    if (loadedAccount.isForceSave()) {
      saveAccount(loadedAccount.getAccount());
    }
  }

  public static void unloadAccount(UUID uuid) {
    Optional<ProxyChatAccount> account = getAccount(uuid);

    if (account.isPresent()) {
      unloadAccount(account.get());
    }
  }

  public static void unloadAccount(ProxyChatAccount account) {
    saveAccount(account);

    accounts.remove(account.getUniqueId());
  }

  public static void saveAccount(ProxyChatAccount account) {
    accountStorage.save(account);
  }

  static {
    accounts.put(consoleAccount.getUniqueId(), consoleAccount);
  }
}
