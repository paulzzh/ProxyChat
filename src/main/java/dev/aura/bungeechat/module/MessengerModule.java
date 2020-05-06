package dev.aura.bungeechat.module;

import dev.aura.bungeechat.BungeeChat;
import dev.aura.bungeechat.command.MessageCommand;
import dev.aura.bungeechat.command.MessageToggleCommand;
import dev.aura.bungeechat.command.ReplyCommand;

public class MessengerModule extends Module {
  private MessageCommand messageCommand;
  private ReplyCommand replyCommand;
  private MessageToggleCommand toggleCommand;

  @Override
  public String getName() {
    return "Messenger";
  }

  @Override
  public void onEnable() {
    messageCommand = new MessageCommand(this);
    replyCommand = new ReplyCommand(this);
    toggleCommand = new MessageToggleCommand(this);

    messageCommand.register();
    replyCommand.register();
    toggleCommand.register();
  }

  @Override
  public void onDisable() {
    messageCommand.unregister();
    replyCommand.unregister();
    toggleCommand.unregister();
  }
}
