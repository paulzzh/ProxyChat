package dev.aura.bungeechat.api.placeholder;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import dev.aura.bungeechat.api.utils.TextReplacementRenderer;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.PatternReplacementResult;
import net.kyori.adventure.text.TextComponent;

@UtilityClass
public class PlaceHolderManager {
  private static final Character placeholderChar = '%';
  private static final Pattern placeholderPattern = Pattern.compile(placeholderChar + "\\w+?" + placeholderChar);
  private static final List<BungeeChatPlaceHolder> placeholders = new LinkedList<>();

  public static Stream<BungeeChatPlaceHolder> getPlaceholderStream() {
    return placeholders.stream();
  }

  public static Stream<BungeeChatPlaceHolder> getApplicableStream(BungeeChatContext context) {
    return getPlaceholderStream().filter(placeholder -> placeholder.isContextApplicable(context));
  }

  public static Component processMessage(Component message, BungeeChatContext context) {
    final List<BungeeChatPlaceHolder> placeholders =
        getApplicableStream(context).collect(Collectors.toList());

    Function<TextComponent.Builder, ComponentLike> replacement = (TextComponent.Builder match) -> {
      String placeholderName = match.content().substring(1, match.content().length() -1);

      if(placeholderName.charAt(0) == '%') {
        return match;
      } else {
        Optional<BungeeChatPlaceHolder> placeholder =
                placeholders.stream().filter(p -> p.matchesName(placeholderName)).findFirst();

        if(placeholder.isPresent()) {
          match.content("");
          match.append(placeholder.get().getReplacementComponent(placeholderName, context));
        }
      }

      return match;
    };

    return TextReplacementRenderer.INSTANCE.render(message,
                                                   new TextReplacementRenderer.State(placeholderPattern,
                                                                                     (result, builder) -> replacement.apply(builder),
                                                                                     (index, replaced) -> PatternReplacementResult.REPLACE));

  }

  public static String processMessage(String message, BungeeChatContext context) {
    final StringBuilder builder = new StringBuilder();
    final List<BungeeChatPlaceHolder> placeholders =
        getApplicableStream(context).collect(Collectors.toList());

    processMessageInternal(message, context, builder, placeholders);

    return builder.toString();
  }

  private static void processMessageInternal(
      String message,
      BungeeChatContext context,
      StringBuilder builder,
      List<BungeeChatPlaceHolder> placeholders) {
    boolean encounteredPlaceholder = false;
    StringBuilder placeholderName = null;

    for (char c : message.toCharArray()) {
      if (c == placeholderChar) {
        if (encounteredPlaceholder) {
          // We only need to do stuff if we are finding the end of the placeholder
          if (placeholderName == null) {
            // We just found the delimiter twice (this is an escape sequence). Add it to the buffer
            // once
            builder.append(placeholderChar);
          } else {
            // Render the placeholderName and delete the builder
            final String placeholderNameStr = placeholderName.toString();
            placeholderName = null;

            Optional<BungeeChatPlaceHolder> placeholder =
                placeholders.stream().filter(p -> p.matchesName(placeholderNameStr)).findFirst();

            if (placeholder.isPresent()) {
              // Apply the placeholder
              final String placeholderReplacement =
                  placeholder.get().getReplacement(placeholderNameStr, context);

              // Apply placeholders to that (note that appends any normal string parts)
              processMessageInternal(placeholderReplacement, context, builder, placeholders);
            } else {
              // Placeholder not found, let's add it to the output verbatim (with the delimiter
              // surrounding it)
              builder.append(placeholderChar).append(placeholderNameStr).append(placeholderChar);
            }
          }
        }

        // Toggle the state
        encounteredPlaceholder = !encounteredPlaceholder;
      } else {
        if (encounteredPlaceholder) {
          // We're parsing the name of the placeholder
          if (placeholderName == null) {
            // Create the instance for the builder if necessary
            placeholderName = new StringBuilder();
          }

          placeholderName.append(c);
        } else {
          // Just a normal char. Append it to the buffer
          builder.append(c);
        }
      }
    }

    if (encounteredPlaceholder) {
      builder.append(placeholderChar);

      if (placeholderName != null) {
        builder.append(placeholderName);
      }
    }
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
