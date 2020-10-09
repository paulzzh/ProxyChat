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

package uk.co.notnull.ProxyChat.module;

import com.typesafe.config.Config;
import uk.co.notnull.ProxyChat.ProxyChat;
import uk.co.notnull.ProxyChat.chatlog.ChatLoggingManager;
import uk.co.notnull.ProxyChat.chatlog.ConsoleLogger;
import uk.co.notnull.ProxyChat.chatlog.FileLogger;
import uk.co.notnull.ProxyChat.listener.ChatLoggingListener;

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

    ProxyChat.getInstance().getProxy()
        .getEventManager()
        .register(ProxyChat.getInstance(), chatLoggingListener);
  }

  @Override
  public void onDisable() {
    ProxyChat.getInstance().getProxy().getEventManager().unregisterListener(ProxyChat.getInstance(), chatLoggingListener);

    if (chatLoggingListener != null) {
      ChatLoggingManager.removeLogger(consoleLogger);
    }
    if (fileLogger != null) {
      ChatLoggingManager.removeLogger(fileLogger);
    }
  }
}
