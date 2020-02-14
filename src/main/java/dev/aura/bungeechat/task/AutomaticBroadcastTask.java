package dev.aura.bungeechat.task;

import dev.aura.bungeechat.api.placeholder.BungeeChatContext;
import dev.aura.bungeechat.message.MessagesService;
import dev.aura.bungeechat.message.PlaceHolderUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AutomaticBroadcastTask implements Runnable {
  private final List<String> messages;
  private final List<String> servers;
  private final int size;
  private final boolean random;
  private int current;
  private Random rand;

  public AutomaticBroadcastTask(List<String> messages, boolean random, List<String> servers) {
    this.messages = new ArrayList<>(messages);
    this.servers = servers != null ? new ArrayList<>(servers) : null;
    size = messages.size();
    this.random = random;
    current = -1;
    rand = new Random();
  }

  @Override
  public void run() {
    MessagesService.sendToMatchingPlayers(
        PlaceHolderUtil.formatMessage(getMessage(), new BungeeChatContext()),
        this.servers != null ? MessagesService.getServerPredicate(this.servers) : MessagesService.getGlobalPredicate());
  }

  private String getMessage() {
    if (random) {
      current = rand.nextInt(size);
    } else {
      current++;

      if (current >= size) {
        current = 0;
      }
    }

    return messages.get(current);
  }
}
