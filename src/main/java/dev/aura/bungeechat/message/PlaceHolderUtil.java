package dev.aura.bungeechat.message;

import com.google.common.collect.ImmutableMap;
import com.typesafe.config.Config;
import dev.aura.bungeechat.BungeeChat;
import dev.aura.bungeechat.api.account.AccountManager;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextFormat;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

@UtilityClass
public class PlaceHolderUtil {
  private static final String FORMATS = "Formats";
  private static final String LANGUAGE = "Language";
  private static Config formatsBase;
  private static MessagesTranslator messageBase;

  private static final char altColorChar = '&';
  private static final String altColorString = String.valueOf(altColorChar);
//  private static final String colorCodeReplacement = "\u00A7$1";

  private static final ImmutableMap<TextFormat, Permission> colorCodeMap =
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

//  private static final Pattern urlEscapePattern = Pattern.compile("(?:(?:https?)://)?(?:[-\\w_.]+\\.\\w{2,})(?:/\\S*)?");

//  private static final Map<Integer, Optional<Pattern>> patternCache = new HashMap<>();
  private static final char placeholderChar = PlaceHolderManager.placeholderChar;
  private static final String placeholderString = String.valueOf(placeholderChar);

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

      return LegacyComponentSerializer.legacyAmpersand().deserialize(formatsBase.getString(format.getStringPath()));
    } catch (RuntimeException e) {
      return LegacyComponentSerializer.legacyAmpersand().deserialize(format.getStringPath());
    }
  }

  public static Component getMessage(Messages message) {
    try {
      if (messageBase == null) {
        loadMessageBase();
      }

      return LegacyComponentSerializer.legacyAmpersand().deserialize(messageBase.translateWithFallback(message));
    } catch (RuntimeException e) {
      return LegacyComponentSerializer.legacyAmpersand().deserialize(message.getStringPath());
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

  public static Component formatMessage(String message, BungeeChatContext context) {
    Component m = LegacyComponentSerializer.legacyAmpersand().deserialize(message);

    return filterFormatting(PlaceHolderManager.processMessage(m, context));
  }

  public static Component formatMessage(Component message, BungeeChatContext context) {
    return filterFormatting(PlaceHolderManager.processMessage(message, context));
  }

  public static Component filterFormatting(Component message) {
    return filterFormatting(message, Optional.empty());
  }

  public static Component filterFormatting(Component message, Optional<BungeeChatAccount> account) {
    BungeeChatAccount permsAccount = account.orElseGet(AccountManager::getConsoleAccount);
    Map<TextDecoration, TextDecoration.State> decorations = message.decorations();

    TextColor color = message.color();

    if(color instanceof NamedTextColor && !PermissionManager.hasPermission(permsAccount, colorCodeMap.get(color))) {
      message.color(null);
    } else if(!PermissionManager.hasPermission(permsAccount, Permission.USE_CHAT_FORMAT_RGB)) {
      message.color(null);
    }

    for (Map.Entry<TextDecoration, TextDecoration.State> entry : decorations.entrySet()) {
      Permission perm = colorCodeMap.get(entry.getKey());

      if(perm != null && !PermissionManager.hasPermission(permsAccount, perm)) {
        entry.setValue(TextDecoration.State.NOT_SET);
      }
    }

    message.decorations(decorations);

    if(!message.children().isEmpty()) {
      message.children().forEach(child -> filterFormatting(child, account));
    }

    return message;
  }

  public static String escapeAltColorCodes(String message) {
//    List<String> urls = new ArrayList<>();
//    Matcher matcher = urlEscapePattern.matcher(message);
//
//    //Save and remove URLs before colour code replacement
//    while(matcher.find()) {
//      urls.add(matcher.group());
//    }
//
//    message = urlEscapePattern.matcher(message).replaceAll("%url%");
//    message = message.replace(altColorString, altColorString + altColorString);
//
//    //Readd urls
//    for (String url : urls) {
//      message = message.replaceFirst("%url%", url);
//    }
//
    return message;
  }

  public static String escapePlaceholders(String message) {
    return message.replace(placeholderString, placeholderString + placeholderString);
  }

  public static String escape(String message) {
    return escapeAltColorCodes(escapePlaceholders(message));
  }
}
