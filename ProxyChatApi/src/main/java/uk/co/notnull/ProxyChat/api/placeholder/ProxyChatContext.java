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

package uk.co.notnull.ProxyChat.api.placeholder;

import com.velocitypowered.api.proxy.server.RegisteredServer;
import uk.co.notnull.ProxyChat.api.account.ProxyChatAccount;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import lombok.Data;
import net.kyori.adventure.text.Component;

/**
 * This class represents a context for a message or other chat related action.<br>
 * It may contain the acting player (sender), the receiver (target), the message and possibly more
 * in the future.
 */
@Data
public class ProxyChatContext {
  /**
   * Predefined Predicate to check if a context has a sender.
   *
   * @see ProxyChatContext#require(Predicate...)
   */
  public static final Predicate<ProxyChatContext> HAS_SENDER = ProxyChatContext::hasSender;
  /**
   * Predefined Predicate to check if a context has a target.
   *
   * @see ProxyChatContext#require(Predicate...)
   */
  public static final Predicate<ProxyChatContext> HAS_TARGET = ProxyChatContext::hasTarget;
  /**
   * Predefined Predicate to check if a context has a message.
   *
   * @see ProxyChatContext#require(Predicate...)
   */
  public static final Predicate<ProxyChatContext> HAS_MESSAGE = ProxyChatContext::hasMessage;
  /**
   * Predefined Predicate to check if a context has a channel.
   *
   * @see ProxyChatContext#require(Predicate...)
   */
  public static final Predicate<ProxyChatContext> HAS_CHANNEL = ProxyChatContext::hasChannel;
  /**
   * Predefined Predicate to check if a context has a server.
   *
   * @see ProxyChatContext#require(Predicate...)
   */
  public static final Predicate<ProxyChatContext> HAS_SERVER = ProxyChatContext::hasServer;

  /**
   * Predefined Predicate to check if a context has been parsed.
   *
   * @see ProxyChatContext#require(Predicate...)
   */
  public static final Predicate<ProxyChatContext> IS_PARSED = ProxyChatContext::isParsed;

  /**
   * Predefined Predicate to check if a context does not have a sender.
   *
   * @see ProxyChatContext#require(Predicate...)
   */
  public static final Predicate<ProxyChatContext> HAS_NO_SENDER = HAS_SENDER.negate();
  /**
   * Predefined Predicate to check if a context does not have a target.
   *
   * @see ProxyChatContext#require(Predicate...)
   */
  public static final Predicate<ProxyChatContext> HAS_NO_TARGET = HAS_TARGET.negate();
  /**
   * Predefined Predicate to check if a context does not have a message.
   *
   * @see ProxyChatContext#require(Predicate...)
   */
  public static final Predicate<ProxyChatContext> HAS_NO_MESSAGE = HAS_MESSAGE.negate();
  /**
   * Predefined Predicate to check if a context does not have a channel.
   *
   * @see ProxyChatContext#require(Predicate...)
   */
  public static final Predicate<ProxyChatContext> HAS_NO_CHANNEL = HAS_CHANNEL.negate();
  /**
   * Predefined Predicate to check if a context does not have a server.
   *
   * @see ProxyChatContext#require(Predicate...)
   */
  public static final Predicate<ProxyChatContext> HAS_NO_SERVER = HAS_SERVER.negate();

  private static final Map<Predicate<ProxyChatContext>, String> requirementsNameCache =
      new HashMap<>(8);

  private ProxyChatAccount sender;
  private ProxyChatAccount target;
  private String message;
  private Component parsedMessage;
  private String channel;
  private RegisteredServer server;
  private boolean parsed = false;

  public ProxyChatContext() {
    sender = null;
    target = null;
    message = null;
    parsedMessage = null;
    channel = null;
    server = null;
  }

  public ProxyChatContext(ProxyChatAccount sender) {
    this();

    this.sender = sender;
  }

  public ProxyChatContext(String message) {
    this();

    this.message = message;
  }

  public ProxyChatContext(ProxyChatAccount sender, String message) {
    this(sender);
    this.message = message;
  }

  public ProxyChatContext(ProxyChatAccount sender, ProxyChatAccount target) {
    this(sender);

    this.target = target;
  }

  public ProxyChatContext(ProxyChatAccount sender, ProxyChatAccount target, String message) {
    this(sender, target);

    this.message = message;
  }

  public ProxyChatContext(ProxyChatAccount sender, String message, RegisteredServer server) {
    this(sender, message);

    this.server = server;
  }

  /**
   * This method is used to verify if a context is valid. All passed requirements must be true in
   * order for this test to pass. If it fails an {@link InvalidContextError} is thrown.<br>
   * It is recommended to use the static predefined {@link Predicate}s like {@link
   * ProxyChatContext#HAS_SENDER}.
   *
   * @param requirements An array of requirements which all must be true for this context to be
   *     valid.
   * @throws InvalidContextError This assertion error gets thrown when one (or more) requirements
   *     are not met. If it is a predefined {@link Predicate} from {@link ProxyChatContext} the
   *     name will be included in the error message. If not a generic message will be put.
   * @see ProxyChatContext#HAS_SENDER
   * @see ProxyChatContext#HAS_TARGET
   * @see ProxyChatContext#HAS_MESSAGE
   * @see ProxyChatContext#HAS_CHANNEL
   * @see ProxyChatContext#HAS_NO_SENDER
   * @see ProxyChatContext#HAS_NO_TARGET
   * @see ProxyChatContext#HAS_NO_MESSAGE
   * @see ProxyChatContext#HAS_NO_CHANNEL
   */
  @SafeVarargs
  public final void require(Predicate<? super ProxyChatContext>... requirements)
      throws InvalidContextError {
    for (Predicate<? super ProxyChatContext> requirement : requirements) {
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

  public Optional<ProxyChatAccount> getSender() {
    return Optional.ofNullable(sender);
  }

  public Optional<ProxyChatAccount> getTarget() {
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

  public Optional<RegisteredServer> getServer() {
    return Optional.ofNullable(server);
  }

  public void setParsedMessage(Component message) {
    parsed = true;
    parsedMessage = message;
  }

  // Fill the requirementsNameCache
  static {
    final int modifers = Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL;

    for (Field field : ProxyChatContext.class.getDeclaredFields()) {
      try {
        if ((field.getModifiers() & modifers) == modifers) {
          @SuppressWarnings("unchecked")
          Predicate<ProxyChatContext> filter = (Predicate<ProxyChatContext>) field.get(null);

          requirementsNameCache.put(
              filter, "Context does not meet requirement " + field.getName() + "!");
        }

      } catch (IllegalArgumentException | IllegalAccessException e) {
        e.printStackTrace();
      }
    }
  }
}
