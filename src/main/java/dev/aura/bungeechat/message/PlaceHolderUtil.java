package dev.aura.bungeechat.message;

import com.google.common.collect.ImmutableMap;
import com.typesafe.config.Config;
import dev.aura.bungeechat.BungeeChat;
import dev.aura.bungeechat.api.account.BungeeChatAccount;
import dev.aura.bungeechat.api.placeholder.BungeeChatContext;
import dev.aura.bungeechat.api.placeholder.PlaceHolderManager;
import dev.aura.bungeechat.config.Configuration;
import dev.aura.bungeechat.permission.Permission;
import dev.aura.bungeechat.permission.PermissionManager;
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
    File dir = BungeeChat.getInstance().getLangFolder();
    String language = Configuration.get().getString(LANGUAGE);

    messageBase =
        new PluginMessagesTranslator(dir, language, BungeeChat.getInstance(), BungeeChat.ID);
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

  public static Component getFullFormatMessage(Format format, BungeeChatContext context) {
    return formatMessage(getFormat(format), context);
  }

  public static Component getFullMessage(Messages message) {
    return formatMessage(getMessage(message), new BungeeChatContext());
  }

  public static Component getFullMessage(Messages message, BungeeChatContext context) {
    return formatMessage(getMessage(message), context);
  }

//  public static Component formatMessage(String message, BungeeChatContext context) {
//    Component m = legacySerializer.deserialize(message);
//
//    return PlaceHolderManager.processMessage(m, context);
//  }

  public static Component formatMessage(Component message, BungeeChatContext context) {
    return PlaceHolderManager.processMessage(message, context);
  }

  public static Component filterFormatting(Component message, BungeeChatAccount account) {
    Style.Builder style = Style.style();
    TextColor color = message.color();

    if(color instanceof NamedTextColor) {
      if(PermissionManager.hasPermission(account, permissionMap.get(color))) {
        style.color(color);
      }
    } else if(color != null && PermissionManager.hasPermission(account, Permission.USE_CHAT_FORMAT_RGB)) {
      style.color(color);
    }

    for(Map.Entry<TextDecoration, TextDecoration.State> entry : message.decorations().entrySet()) {
      if(entry.getValue() == TextDecoration.State.TRUE) {
        Permission perm = permissionMap.get(entry.getKey());

        if(perm == null || PermissionManager.hasPermission(account, perm)) {
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
