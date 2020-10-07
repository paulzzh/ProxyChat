package dev.aura.bungeechat.api.hook;

import dev.aura.bungeechat.api.account.BungeeChatAccount;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

@UtilityClass
public class HookManager {
  public static final int DEFAULT_PREFIX_PRIORITY = 100;
  public static final int PERMISSION_PLUGIN_PREFIX_PRIORITY = 200;
  public static final int ACCOUNT_PREFIX_PRIORITY = 300;
  private static final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.builder()
          .character('&').extractUrls().hexColors().useUnusualXRepeatedCharacterHexFormat().build();

  private static Map<String, BungeeChatHook> hooks = new LinkedHashMap<>();

  public static void addHook(String name, BungeeChatHook hook) {
    hooks.put(name, hook);

    sortHooks();
  }

  public static BungeeChatHook removeHook(String name) {
    BungeeChatHook out = hooks.remove(name);

    sortHooks();

    return out;
  }

  public String getPrefix(BungeeChatAccount account) {
    Optional<String> out;

    for (BungeeChatHook hook : hooks.values()) {
      out = hook.getPrefix(account);

      if (out.isPresent()) return out.get();
    }

    return "";
  }

  public String getSuffix(BungeeChatAccount account) {
    Optional<String> out;

    for (BungeeChatHook hook : hooks.values()) {
      out = hook.getSuffix(account);

      if (out.isPresent()) return out.get();
    }

    return "";
  }

  public String getFullName(BungeeChatAccount account) {
    return getPrefix(account) + account.getName() + getSuffix(account);
  }

  public String getFullDisplayName(BungeeChatAccount account) {
    return getPrefix(account) + account.getDisplayName() + getSuffix(account);
  }

  public Component getPrefixComponent(BungeeChatAccount account) {
    for (BungeeChatHook hook : hooks.values()) {
      Optional<String> prefix = hook.getPrefix(account);

      if(prefix.isPresent()) {
        return legacySerializer.deserialize(prefix.get());
      }
    }

    return Component.empty();
  }

  public Component getSuffixComponent(BungeeChatAccount account) {
    for (BungeeChatHook hook : hooks.values()) {
      Optional<String> suffix = hook.getSuffix(account);

      if(suffix.isPresent()) {
        return legacySerializer.deserialize(suffix.get());
      }
    }

    return Component.empty();
  }

  public Component getFullNameComponent(BungeeChatAccount account) {
    return legacySerializer.deserialize(getPrefix(account) + account.getName() + getSuffix(account))
            .clickEvent(ClickEvent.suggestCommand("/w " + account.getName() + " "))
            .hoverEvent(Component.text("Click to whisper " + account.getName()));
  }

  public Component getFullDisplayNameComponent(BungeeChatAccount account) {
    return legacySerializer.deserialize(getPrefix(account) + account.getDisplayName() + getSuffix(account))
            .clickEvent(ClickEvent.suggestCommand("/w " + account.getName() + " "))
            .hoverEvent(Component.text("Click to whisper " + account.getName()));
  }

  private static void sortHooks() {
    hooks =
        hooks.entrySet().stream()
            .sorted(Collections.reverseOrder(Entry.comparingByValue()))
            .collect(
                Collectors.toMap(
                    Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
  }
}
