package dev.aura.bungeechat.listener;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.TabCompleteEvent;
import dev.aura.bungeechat.api.utils.ChatUtils;
import dev.aura.bungeechat.command.BaseCommand;
import dev.aura.bungeechat.util.LoggerHelper;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class CommandTabCompleteListener {
  private Map<String, BaseCommand> bungeeChatCommands = null;

  @Subscribe
  public void onTabComplete(TabCompleteEvent event) {
    final String message = event.getPartialMessage();

    if (!ChatUtils.isCommand(message)) return;

    final String[] allArgs = event.getPartialMessage().split(" ", -1);
    final String command = allArgs[0].substring(1);

    if (allArgs.length == 1) return;
    if (!bungeeChatCommands.containsKey(command)) return;

    final BaseCommand commandHandler = bungeeChatCommands.get(command);
    CommandSource sender = event.getPlayer();

    if (!commandHandler.hasPermission(sender, allArgs)) return;

    String[] args = Arrays.copyOfRange(allArgs, 1, allArgs.length);
    Collection<String> suggestions = null;

    try {
      suggestions = commandHandler.suggest(sender, args);
    } catch (RuntimeException e) {
      LoggerHelper.warning("Uncaught error during tabcomplete of /" + command, e);
    }

    if (suggestions != null) event.getSuggestions().addAll(suggestions);
  }

  public void updateBungeeChatCommands() {
    bungeeChatCommands = getBungeeChatCommands();
  }

  private static Map<String, BaseCommand> getBungeeChatCommands() {
    return Collections.emptyMap();
  }
}
