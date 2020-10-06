package dev.aura.bungeechat.chatlog;

import dev.aura.bungeechat.BungeeChat;
import dev.aura.bungeechat.api.placeholder.BungeeChatContext;
import dev.aura.bungeechat.message.Format;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import org.slf4j.Logger;

public class ConsoleLogger implements ChatLogger {
  private final Logger logger;

  public ConsoleLogger() {
    logger = BungeeChat.getInstance().getLogger();
  }

  @Override
  public void log(BungeeChatContext context) {
    logger.info(PlainComponentSerializer.plain().serialize(Format.CHAT_LOGGING_CONSOLE.get(context)));
  }
}
