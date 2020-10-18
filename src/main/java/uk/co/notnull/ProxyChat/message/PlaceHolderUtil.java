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

package uk.co.notnull.ProxyChat.message;

import com.google.common.collect.ImmutableMap;
import com.typesafe.config.Config;
import uk.co.notnull.ProxyChat.ProxyChat;
import uk.co.notnull.ProxyChat.api.account.ProxyChatAccount;
import uk.co.notnull.ProxyChat.api.placeholder.ProxyChatContext;
import uk.co.notnull.ProxyChat.api.placeholder.PlaceHolderManager;
import uk.co.notnull.ProxyChat.config.Configuration;
import uk.co.notnull.ProxyChat.api.permission.Permission;
import dev.aura.lib.messagestranslator.MessagesTranslator;
import dev.aura.lib.messagestranslator.PluginMessagesTranslator;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.*;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

@UtilityClass
public class PlaceHolderUtil {
  private static final String FORMATS = "Formats";
  private static final String LANGUAGE = "Language";
  private static Config formatsBase;
  private static MessagesTranslator messageBase;

  private static final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.builder()
          .character('&').extractUrls().hexColors().hexCharacter('x').useUnusualXRepeatedCharacterHexFormat().build();

  private static final ImmutableMap<TextFormat, Permission> permissionMap =
      ImmutableMap.<TextFormat, Permission>builder()
          .put(NamedTextColor.BLACK, Permission.USE_CHAT_COLOR_BLACK)
          .put(NamedTextColor.DARK_BLUE, Permission.USE_CHAT_COLOR_DARK_BLUE)
          .put(NamedTextColor.DARK_GREEN, Permission.USE_CHAT_COLOR_DARK_GREEN)
          .put(NamedTextColor.DARK_AQUA, Permission.USE_CHAT_COLOR_DARK_AQUA)
          .put(NamedTextColor.DARK_RED, Permission.USE_CHAT_COLOR_DARK_RED)
          .put(NamedTextColor.DARK_PURPLE, Permission.USE_CHAT_COLOR_DARK_PURPLE)
          .put(NamedTextColor.GOLD, Permission.USE_CHAT_COLOR_GOLD)
          .put(NamedTextColor.GRAY, Permission.USE_CHAT_COLOR_GRAY)
          .put(NamedTextColor.DARK_GRAY, Permission.USE_CHAT_COLOR_DARK_GRAY)
          .put(NamedTextColor.BLUE, Permission.USE_CHAT_COLOR_BLUE)
          .put(NamedTextColor.GREEN, Permission.USE_CHAT_COLOR_GREEN)
          .put(NamedTextColor.AQUA, Permission.USE_CHAT_COLOR_AQUA)
          .put(NamedTextColor.RED, Permission.USE_CHAT_COLOR_RED)
          .put(NamedTextColor.LIGHT_PURPLE, Permission.USE_CHAT_COLOR_LIGHT_PURPLE)
          .put(NamedTextColor.YELLOW, Permission.USE_CHAT_COLOR_YELLOW)
          .put(NamedTextColor.WHITE, Permission.USE_CHAT_COLOR_WHITE)
          .put(TextDecoration.OBFUSCATED, Permission.USE_CHAT_FORMAT_OBFUSCATED)
          .put(TextDecoration.BOLD, Permission.USE_CHAT_FORMAT_BOLD)
          .put(TextDecoration.STRIKETHROUGH, Permission.USE_CHAT_FORMAT_STRIKETHROUGH)
          .put(TextDecoration.UNDERLINED, Permission.USE_CHAT_FORMAT_UNDERLINE)
          .put(TextDecoration.ITALIC, Permission.USE_CHAT_FORMAT_ITALIC)
          .build();

  public static void clearConfigSections() {
    formatsBase = null;
    messageBase = null;
  }

  public static void loadConfigSections() {
    loadFormatsBase();
    loadMessageBase();
  }

  public static void loadFormatsBase() {
    formatsBase = Configuration.get().getConfig(FORMATS);
  }

  public static void loadMessageBase() {
    File dir = ProxyChat.getInstance().getLangFolder();
    String language = Configuration.get().getString(LANGUAGE);

    messageBase =
        new PluginMessagesTranslator(dir, language, ProxyChat.getInstance(), ProxyChat.ID);
  }

  public static Component getFormat(Format format) {
    try {
      if (formatsBase == null) {
        loadFormatsBase();
      }

      return legacySerializer.deserialize(formatsBase.getString(format.getStringPath()));
    } catch (RuntimeException e) {
      return legacySerializer.deserialize(format.getStringPath());
    }
  }

  public static String getFormatRaw(Format format) {
    try {
      if (formatsBase == null) {
        loadFormatsBase();
      }

      return formatsBase.getString(format.getStringPath());
    } catch (RuntimeException e) {
      return format.getStringPath();
    }
  }

  public static Component getMessage(Messages message) {
    try {
      if (messageBase == null) {
        loadMessageBase();
      }

      return legacySerializer.deserialize(messageBase.translateWithFallback(message));
    } catch (RuntimeException e) {
      return legacySerializer.deserialize(message.getStringPath());
    }
  }

  public static Component getFullFormatMessage(Format format, ProxyChatContext context) {
    return formatMessage(getFormat(format), context);
  }

  public static String getFullFormatMessageRaw(Format format, ProxyChatContext context) {
    return formatMessageRaw(getFormatRaw(format), context);
  }

  public static Component getFullMessage(Messages message) {
    return formatMessage(getMessage(message), new ProxyChatContext());
  }

  public static Component getFullMessage(Messages message, ProxyChatContext context) {
    return formatMessage(getMessage(message), context);
  }

  public static Component formatMessage(Component message, ProxyChatContext context) {
    return PlaceHolderManager.processMessage(message, context);
  }

  public static String formatMessageRaw(String message, ProxyChatContext context) {
    return PlaceHolderManager.processMessage(message, context);
  }

  public static Component filterFormatting(Component message, ProxyChatAccount account) {
    Style.Builder style = Style.style();
    TextColor color = message.color();

    //TODO: Permissions for these?
    style.clickEvent(message.clickEvent());
    style.hoverEvent(message.hoverEvent());
    style.insertion(message.insertion());
    style.font(message.style().font());

    if(color instanceof NamedTextColor) {
      if(account.hasPermission(permissionMap.get(color))) {
        style.color(color);
      }
    } else if(color != null && account.hasPermission(Permission.USE_CHAT_FORMAT_RGB)) {
      style.color(color);
    }

    for(Map.Entry<TextDecoration, TextDecoration.State> entry : message.decorations().entrySet()) {
      if(entry.getKey() == TextDecoration.UNDERLINED && entry.getValue() == TextDecoration.State.TRUE
              && message.clickEvent() != null) {
        style.decoration(entry.getKey(), TextDecoration.State.TRUE);
        continue;
      }

      if(entry.getValue() == TextDecoration.State.TRUE) {
        Permission perm = permissionMap.get(entry.getKey());

        if(perm == null || account.hasPermission(perm)) {
          style.decoration(entry.getKey(), TextDecoration.State.TRUE);
        }
      } else {
        style.decoration(entry.getKey(), entry.getValue());
      }
    }

    message = message.style(style);

    if(!message.children().isEmpty()) {
      message = message.children(message.children().stream()
                                         .map(child -> filterFormatting(child, account))
                                         .collect(Collectors.toList()));
    }

    return message;
  }
}
