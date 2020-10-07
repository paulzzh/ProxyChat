package dev.aura.bungeechat.api.placeholder;

import dev.aura.bungeechat.api.account.BungeeChatAccount;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import lombok.Data;
import lombok.experimental.Tolerate;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;


/**
 * This class represents a context for a message or other chat related action.<br>
 * It may contain the acting player (sender), the receiver (target), the message and possibly more
 * in the future.
 */
@Data
public class BungeeChatContext {
  private static final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.builder()
          .extractUrls(Style.style().color(TextColor.fromHexString("#8194e4")).decoration(TextDecoration.UNDERLINED, true).build())
          .character('&').hexColors().useUnusualXRepeatedCharacterHexFormat().build();

  /**
   * Predefined Predicate to check if a context has a sender.
   *
   * @see BungeeChatContext#require(Predicate...)
   */
  public static final Predicate<BungeeChatContext> HAS_SENDER = BungeeChatContext::hasSender;
  /**
   * Predefined Predicate to check if a context has a target.
   *
   * @see BungeeChatContext#require(Predicate...)
   */
  public static final Predicate<BungeeChatContext> HAS_TARGET = BungeeChatContext::hasTarget;
  /**
   * Predefined Predicate to check if a context has a message.
   *
   * @see BungeeChatContext#require(Predicate...)
   */
  public static final Predicate<BungeeChatContext> HAS_MESSAGE = BungeeChatContext::hasMessage;
  /**
   * Predefined Predicate to check if a context has a channel.
   *
   * @see BungeeChatContext#require(Predicate...)
   */
  public static final Predicate<BungeeChatContext> HAS_CHANNEL = BungeeChatContext::hasChannel;
  /**
   * Predefined Predicate to check if a context has a server.
   *
   * @see BungeeChatContext#require(Predicate...)
   */
  public static final Predicate<BungeeChatContext> HAS_SERVER = BungeeChatContext::hasServer;

  /**
   * Predefined Predicate to check if a context has been parsed.
   *
   * @see BungeeChatContext#require(Predicate...)
   */
  public static final Predicate<BungeeChatContext> IS_PARSED = BungeeChatContext::isParsed;

  /**
   * Predefined Predicate to check if a context does not have a sender.
   *
   * @see BungeeChatContext#require(Predicate...)
   */
  public static final Predicate<BungeeChatContext> HAS_NO_SENDER = HAS_SENDER.negate();
  /**
   * Predefined Predicate to check if a context does not have a target.
   *
   * @see BungeeChatContext#require(Predicate...)
   */
  public static final Predicate<BungeeChatContext> HAS_NO_TARGET = HAS_TARGET.negate();
  /**
   * Predefined Predicate to check if a context does not have a message.
   *
   * @see BungeeChatContext#require(Predicate...)
   */
  public static final Predicate<BungeeChatContext> HAS_NO_MESSAGE = HAS_MESSAGE.negate();
  /**
   * Predefined Predicate to check if a context does not have a channel.
   *
   * @see BungeeChatContext#require(Predicate...)
   */
  public static final Predicate<BungeeChatContext> HAS_NO_CHANNEL = HAS_CHANNEL.negate();
  /**
   * Predefined Predicate to check if a context does not have a server.
   *
   * @see BungeeChatContext#require(Predicate...)
   */
  public static final Predicate<BungeeChatContext> HAS_NO_SERVER = HAS_SERVER.negate();

  private static final Map<Predicate<BungeeChatContext>, String> requirementsNameCache =
      new HashMap<>(8);

  private BungeeChatAccount sender;
  private BungeeChatAccount target;
  private String message;
  private Component parsedMessage;
  private String channel;
  private String server;
  private boolean parsed = false;

  public BungeeChatContext() {
    sender = null;
    target = null;
    message = null;
    parsedMessage = null;
    channel = null;
    server = null;
  }

  public BungeeChatContext(BungeeChatAccount sender) {
    this();

    this.sender = sender;
  }

  public BungeeChatContext(String message) {
    this();

    this.message = message;
  }

  public BungeeChatContext(BungeeChatAccount sender, String message) {
    this(sender);
    this.message = message;
  }

  public BungeeChatContext(BungeeChatAccount sender, BungeeChatAccount target) {
    this(sender);

    this.target = target;
  }

  public BungeeChatContext(BungeeChatAccount sender, BungeeChatAccount target, String message) {
    this(sender, target);

    this.message = message;
  }

  public BungeeChatContext(BungeeChatAccount sender, String message, String server) {
    this(sender, message);

    this.server = server;
  }

  /**
   * This method is used to verify if a context is valid. All passed requirements must be true in
   * order for this test to pass. If it fails an {@link InvalidContextError} is thrown.<br>
   * It is recommended to use the static predefined {@link Predicate}s like {@link
   * BungeeChatContext#HAS_SENDER}.
   *
   * @param requirements An array of requirements which all must be true for this context to be
   *     valid.
   * @throws InvalidContextError This assertion error gets thrown when one (or more) requirements
   *     are not met. If it is a predefined {@link Predicate} from {@link BungeeChatContext} the
   *     name will be included in the error message. If not a generic message will be put.
   * @see BungeeChatContext#HAS_SENDER
   * @see BungeeChatContext#HAS_TARGET
   * @see BungeeChatContext#HAS_MESSAGE
   * @see BungeeChatContext#HAS_CHANNEL
   * @see BungeeChatContext#HAS_NO_SENDER
   * @see BungeeChatContext#HAS_NO_TARGET
   * @see BungeeChatContext#HAS_NO_MESSAGE
   * @see BungeeChatContext#HAS_NO_CHANNEL
   */
  @SafeVarargs
  public final void require(Predicate<? super BungeeChatContext>... requirements)
      throws InvalidContextError {
    for (Predicate<? super BungeeChatContext> requirement : requirements) {
      if (!requirement.test(this)) {
        if (requirementsNameCache.containsKey(requirement))
          throw new InvalidContextError(requirementsNameCache.get(requirement));

        throw new InvalidContextError();
      }
    }
  }

  public boolean hasSender() {
    return sender != null;
  }

  public boolean hasTarget() {
    return target != null;
  }

  public boolean hasMessage() {
    return message != null;
  }

  public boolean hasChannel() {
    return channel != null;
  }

  public boolean hasServer() {
    return server != null;
  }

  public Optional<BungeeChatAccount> getSender() {
    return Optional.ofNullable(sender);
  }

  public Optional<BungeeChatAccount> getTarget() {
    return Optional.ofNullable(target);
  }

  public Optional<String> getMessage() {
    return Optional.ofNullable(message);
  }

  public Optional<Component> getParsedMessage() {
    return Optional.ofNullable(parsedMessage);
  }

  public Optional<String> getChannel() {
    return Optional.ofNullable(channel);
  }

  public Optional<String> getServer() {
    return Optional.ofNullable(server);
  }

  public void setParsedMessage(Component message) {
    parsed = true;
    parsedMessage = message;
  }

  // Fill the requirementsNameCache
  static {
    final int modifers = Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL;

    for (Field field : BungeeChatContext.class.getDeclaredFields()) {
      try {
        if ((field.getModifiers() & modifers) == modifers) {
          @SuppressWarnings("unchecked")
          Predicate<BungeeChatContext> filter = (Predicate<BungeeChatContext>) field.get(null);

          requirementsNameCache.put(
              filter, "Context does not meet requirement " + field.getName() + "!");
        }

      } catch (IllegalArgumentException | IllegalAccessException e) {
        e.printStackTrace();
      }
    }
  }
}
