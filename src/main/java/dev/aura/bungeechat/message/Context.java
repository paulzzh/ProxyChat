package dev.aura.bungeechat.message;

import com.velocitypowered.api.command.CommandSource;
import dev.aura.bungeechat.account.BungeecordAccountManager;
import dev.aura.bungeechat.api.account.AccountManager;
import dev.aura.bungeechat.api.account.BungeeChatAccount;
import dev.aura.bungeechat.api.placeholder.BungeeChatContext;
import net.kyori.adventure.text.Component;

import java.util.UUID;

public class Context extends BungeeChatContext {
  public Context() {
    super();
  }

  public Context(BungeeChatAccount player) {
    super(player);
  }

  public Context(BungeeChatAccount sender, BungeeChatAccount target) {
    super(sender, target);
  }

  public Context(UUID sender) {
    super(AccountManager.getAccount(sender).get());
  }

  public Context(UUID sender, UUID target) {
    super(AccountManager.getAccount(sender).get(), AccountManager.getAccount(target).get());
  }

  public Context(CommandSource sender) {
    super(BungeecordAccountManager.getAccount(sender).get());
  }

  public Context(CommandSource player, String message) {
    this(player);

    setMessage(message);
  }

  public Context(CommandSource player, String message, String server) {
    this(player, message);

    setServer(server);
  }

  public Context(CommandSource sender, CommandSource target) {
    super(
        BungeecordAccountManager.getAccount(sender).get(),
        BungeecordAccountManager.getAccount(target).get());
  }

  public Context(CommandSource sender, CommandSource target, String message) {
    this(sender, target);

    setMessage(message);
  }
}
