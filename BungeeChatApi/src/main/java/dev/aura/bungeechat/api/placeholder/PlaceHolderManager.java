package dev.aura.bungeechat.api.placeholder;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;

@UtilityClass
public class PlaceHolderManager {
  public static final char placeholderChar = '%';
  private static final List<BungeeChatPlaceHolder> placeholders = new LinkedList<>();

  public static Stream<BungeeChatPlaceHolder> getPlaceholderStream() {
    return placeholders.stream();
  }

  public static Stream<BungeeChatPlaceHolder> getApplicableStream(BungeeChatContext context) {
    return getPlaceholderStream().filter(placeholder -> placeholder.isContextApplicable(context));
  }

  public static Component processMessage(Component message, BungeeChatContext context) {
    final StringBuilder builder = new StringBuilder();
    final List<BungeeChatPlaceHolder> placeholders =
        getApplicableStream(context).collect(Collectors.toList());

    return message.replaceText(Pattern.compile("%(.*)%"), (TextComponent.Builder match) -> {
      String placeholderName = match.content().substring(1, match.content().length() -1);

      if(placeholderName.charAt(0) == '%') {
        return match;
      } else {
        Optional<BungeeChatPlaceHolder> placeholder =
                placeholders.stream().filter(p -> p.matchesName(placeholderName)).findFirst();

        if(placeholder.isPresent()) {
          return placeholder.get().getReplacementComponent(placeholderName, context);
        }
      }

      return match;
    });
  }

  public static void registerPlaceholder(BungeeChatPlaceHolder... placeholder) {
    for (BungeeChatPlaceHolder p : placeholder) {
      registerPlaceholder(p);
    }
  }

  public static void registerPlaceholder(BungeeChatPlaceHolder placeholder) {
    if (placeholders.contains(placeholder))
      throw new IllegalArgumentException(
          "Placeholder " + placeholder.getName() + " has already been registered!");

    placeholders.add(placeholder);
  }

  public static void clear() {
    placeholders.clear();
  }
}
