package dev.aura.bungeechat.module;

import com.typesafe.config.Config;
import dev.aura.bungeechat.BungeeChat;
import dev.aura.bungeechat.chatlog.ChatLoggingManager;
import dev.aura.bungeechat.chatlog.ConsoleLogger;
import dev.aura.bungeechat.chatlog.FileLogger;
import dev.aura.bungeechat.listener.ChatLoggingListener;

public class ChatLoggingModule extends Module {
  private ChatLoggingListener chatLoggingListener;

  private ConsoleLogger consoleLogger;
  private FileLogger fileLogger;

  @Override
  public String getName() {
    return "ChatLogging";
  }

  @Override
  public void onEnable() {
    Config section = getModuleSection();

    if (section.getBoolean("console")) {
      consoleLogger = new ConsoleLogger();

      ChatLoggingManager.addLogger(consoleLogger);
    }
    if (section.getBoolean("file")) {
      fileLogger = new FileLogger(section.getString("logFile"));

      ChatLoggingManager.addLogger(fileLogger);
    }

    ChatLoggingManager.loadFilteredCommands(section.getStringList("filteredCommands"));

    chatLoggingListener = new ChatLoggingListener();

    BungeeChat.getInstance().getProxy()
        .getEventManager()
        .register(BungeeChat.getInstance(), chatLoggingListener);
  }

  @Override
  public void onDisable() {
    BungeeChat.getInstance().getProxy().getEventManager().unregisterListener(BungeeChat.getInstance(), chatLoggingListener);

    if (chatLoggingListener != null) {
      ChatLoggingManager.removeLogger(consoleLogger);
    }
    if (fileLogger != null) {
      ChatLoggingManager.removeLogger(fileLogger);
    }
  }
}
