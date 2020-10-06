package dev.aura.bungeechat.filter;

import dev.aura.bungeechat.api.account.BungeeChatAccount;
import dev.aura.bungeechat.api.filter.BlockMessageException;
import dev.aura.bungeechat.message.Messages;
import lombok.Getter;
import net.kyori.adventure.text.Component;

public class ExtendedBlockMessageException extends BlockMessageException {
  private static final long serialVersionUID = 5519820760858610372L;

  @Getter private Messages messageType;

  public ExtendedBlockMessageException(
      Messages messageType, BungeeChatAccount sender) {
    super(messageType.get(sender));

    this.messageType = messageType;
  }
}
