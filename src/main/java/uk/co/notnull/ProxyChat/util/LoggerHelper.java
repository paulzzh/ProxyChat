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

package uk.co.notnull.ProxyChat.util;

import uk.co.notnull.ProxyChat.ProxyChat;
import java.io.PrintWriter;
import java.io.StringWriter;
import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.slf4j.Logger;

@UtilityClass
public class LoggerHelper {
  @Getter(value = AccessLevel.PRIVATE, lazy = true)
  private static final Logger logger = ProxyChat.getInstance().getLogger();

  public static void error(String text) {
    getLogger().error(text);
  }

  public static void error(String text, Throwable t) {
    error(text + '\n' + getStackTrace(t));
  }

  public static String getStackTrace(Throwable t) {
    StringWriter sw = new StringWriter();
    @Cleanup PrintWriter pw = new PrintWriter(sw);
    t.printStackTrace(pw);

    return sw.toString();
  }

  public static void info(String text) {
    getLogger().info(text);
  }

  public static void info(String text, Throwable t) {
    info(text + '\n' + getStackTrace(t));
  }

  public static void warning(String text) {
    getLogger().warn(text);
  }

  public static void warning(String text, Throwable t) {
    warning(text + '\n' + getStackTrace(t));
  }
}
