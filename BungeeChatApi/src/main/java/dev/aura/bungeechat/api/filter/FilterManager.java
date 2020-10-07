package dev.aura.bungeechat.api.filter;

import dev.aura.bungeechat.api.account.BungeeChatAccount;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;

@UtilityClass
public class FilterManager {
  public static final int SWEAR_FILTER_PRIORITY = 100;
  public static final int ADVERTISING_FILTER_PRIORITY = 200;
  public static final int CAPSLOCK_FILTER_PRIORITY = 300;
  public static final int DUPLICATION_FILTER_PRIORITY = 400;
  public static final int SPAM_FILTER_PRIORITY = 500;
  public static final int LOCK_CHAT_FILTER_PRIORITY = 600;
  public static final int EMOTE_FILTER_PRIORITY = 700;

  private static Map<String, BungeeChatPreParseFilter> preParsefilters = new LinkedHashMap<>();
  private static Map<String, BungeeChatPostParseFilter> postParsefilters = new LinkedHashMap<>();

  public static void addPreParseFilter(String name, BungeeChatPreParseFilter filter) throws UnsupportedOperationException {
    preParsefilters.put(name, filter);

    sortFilters();
  }

  public static void addPostParseFilter(String name, BungeeChatPostParseFilter filter) throws UnsupportedOperationException {
    postParsefilters.put(name, filter);

    sortFilters();
  }

  public static void removePreParseFilter(String name) throws UnsupportedOperationException {
    preParsefilters.remove(name);
    sortFilters();
  }

  public static void removePostParseFilter(String name) throws UnsupportedOperationException {
    postParsefilters.remove(name);
    sortFilters();
  }

  public static String applyFilters(BungeeChatAccount sender, String message)
      throws UnsupportedOperationException, BlockMessageException {
    for (BungeeChatPreParseFilter filter : preParsefilters.values()) {
      message = filter.applyFilter(sender, message);
    }

    return message;
  }

  public static Component applyFilters(BungeeChatAccount sender, Component message)
      throws UnsupportedOperationException, BlockMessageException {
    for (BungeeChatPostParseFilter filter : postParsefilters.values()) {
      message = filter.applyFilter(sender, message);
    }

    return message;
  }

  private static void sortFilters() {
    preParsefilters =
        preParsefilters.entrySet().stream()
            .sorted(Collections.reverseOrder(Entry.comparingByValue()))
            .collect(
                Collectors.toMap(
                    Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

    postParsefilters =
        postParsefilters.entrySet().stream()
            .sorted(Collections.reverseOrder(Entry.comparingByValue()))
            .collect(
                Collectors.toMap(
                    Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
  }
}
