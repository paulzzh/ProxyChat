package dev.aura.bungeechat.task;

import dev.aura.bungeechat.api.account.BungeeChatAccount;
import dev.aura.bungeechat.api.placeholder.BungeeChatContext;
import dev.aura.bungeechat.message.MessagesService;
import dev.aura.bungeechat.message.PlaceHolderUtil;
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
  private final Predicate<BungeeChatAccount> predicate;
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
      Predicate<BungeeChatAccount> predicate, List<String> messages, boolean random) {
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
        PlaceHolderUtil.formatMessage(getMessage(), new BungeeChatContext()), predicate);
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
