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

package uk.co.notnull.ProxyChat.task;

import uk.co.notnull.ProxyChat.api.account.ProxyChatAccount;
import uk.co.notnull.ProxyChat.api.placeholder.ProxyChatContext;
import uk.co.notnull.ProxyChat.message.MessagesService;
import uk.co.notnull.ProxyChat.message.PlaceHolderUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class AutomaticBroadcastTask implements Runnable {
  private final Predicate<ProxyChatAccount> predicate;
  private final List<Component> messages;
  private final int size;
  private final boolean random;
  private int current;

  private static Random rand = new Random();
  private static final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.builder()
          .extractUrls(
                  Style.style().color(TextColor.fromHexString("#8194e4")).decoration(TextDecoration.UNDERLINED, true).build())
          .character('&').hexColors().useUnusualXRepeatedCharacterHexFormat().build();

  public AutomaticBroadcastTask(
		  Predicate<ProxyChatAccount> predicate, List<String> messages, boolean random) {
    this.predicate = predicate;

    this.messages = new ArrayList<>();
    messages.forEach(message -> this.messages.add(legacySerializer.deserialize(message)));

    size = this.messages.size();
    this.random = random;
    current = -1;
  }

  @Override
  public void run() {
    MessagesService.sendToMatchingPlayers(
            PlaceHolderUtil.formatMessage(getMessage(), new ProxyChatContext()), predicate);
  }

  private Component getMessage() {
    if (random) {
      current = rand.nextInt(size);
    } else {
      current = (++current >= size) ? 0 : current;
    }

    return messages.get(current);
  }
}
