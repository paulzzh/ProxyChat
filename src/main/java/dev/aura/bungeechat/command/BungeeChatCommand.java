package dev.aura.bungeechat.command;

import com.velocitypowered.api.command.CommandSource;
import dev.aura.bungeechat.BungeeChat;
import dev.aura.bungeechat.account.BungeecordAccountManager;
import dev.aura.bungeechat.api.BungeeChatApi;
import dev.aura.bungeechat.api.account.AccountManager;
import dev.aura.bungeechat.api.account.BungeeChatAccount;
import dev.aura.bungeechat.message.Messages;
import dev.aura.bungeechat.message.MessagesService;
import dev.aura.bungeechat.module.BungeecordModuleManager;
import dev.aura.bungeechat.permission.Permission;
import dev.aura.bungeechat.permission.PermissionManager;
import dev.aura.bungeechat.util.LoggerHelper;
import net.kyori.text.format.TextColor;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class BungeeChatCommand extends BaseCommand {
  private final String prefix = TextColor.BLUE + "Bungee Chat " + TextColor.DARK_GRAY + "// ";

  public BungeeChatCommand() {
    super("bungeechat");
  }

  @Override
  public void execute(CommandSource sender, String[] args) {
    if (args.length != 0) {
      if (args[0].equalsIgnoreCase("reload")
          && PermissionManager.hasPermission(sender, Permission.BUNGEECHAT_RELOAD)) {
        final BungeeChat instance = BungeeChat.getInstance();

        BungeeChat.getInstance().getProxy()
            .getScheduler()
            .buildTask(
                instance,
                () -> {
                  instance.onDisable();
                  instance.onEnable(false);

                  MessagesService.sendMessage(
                      sender, prefix + TextColor.GREEN + "The plugin has been reloaded!");
                }).schedule();

        return;
      } else if (args[0].equalsIgnoreCase("setprefix")
          && PermissionManager.hasPermission(sender, Permission.BUNGEECHAT_SETPREFIX)) {

        if (args.length < 2) {
          MessagesService.sendMessage(
              sender,
              Messages.INCORRECT_USAGE.get(sender, "/bungeechat setprefix <player> [new prefix]"));
        } else {
          Optional<BungeeChatAccount> targetAccount = AccountManager.getAccount(args[1]);

          if (!targetAccount.isPresent()) {
            MessagesService.sendMessage(sender, Messages.PLAYER_NOT_FOUND.get());
          } else {
            CommandSource target =
                BungeecordAccountManager.getCommandSource(targetAccount.get()).get();

            if (args.length < 3) {
              targetAccount.get().setStoredPrefix(Optional.empty());
              MessagesService.sendMessage(sender, prefix + Messages.PREFIX_REMOVED.get(target));
            } else {
              String newPrefix =
                  getUnquotedString(
                      Arrays.stream(args, 2, args.length).collect(Collectors.joining(" ")));

              targetAccount.get().setStoredPrefix(Optional.of(newPrefix));
              MessagesService.sendMessage(sender, prefix + Messages.PREFIX_SET.get(target));
            }
          }
        }
        return;
      } else if (args[0].equalsIgnoreCase("setsuffix")
          && PermissionManager.hasPermission(sender, Permission.BUNGEECHAT_SETSUFFIX)) {

        if (args.length < 2) {
          MessagesService.sendMessage(
              sender,
              Messages.INCORRECT_USAGE.get(sender, "/bungeechat setsuffix <player> [new suffix]"));
        } else {
          Optional<BungeeChatAccount> targetAccount = AccountManager.getAccount(args[1]);

          if (!targetAccount.isPresent()) {
            MessagesService.sendMessage(sender, Messages.PLAYER_NOT_FOUND.get());
          } else {
            CommandSource target =
                BungeecordAccountManager.getCommandSource(targetAccount.get()).get();

            if (args.length < 3) {
              targetAccount.get().setStoredSuffix(Optional.empty());
              MessagesService.sendMessage(sender, prefix + Messages.SUFFIX_REMOVED.get(target));
            } else {
              String newSuffix =
                  getUnquotedString(
                      Arrays.stream(args, 2, args.length).collect(Collectors.joining(" ")));

              targetAccount.get().setStoredSuffix(Optional.of(newSuffix));
              MessagesService.sendMessage(sender, prefix + Messages.SUFFIX_SET.get(target));
            }
          }
        }

        return;
      } else if (args[0].equalsIgnoreCase("modules")
          && PermissionManager.hasPermission(sender, Permission.BUNGEECHAT_MODULES)) {
        MessagesService.sendMessage(
            sender,
            prefix
                + TextColor.GRAY
                + "Active Modules: "
                + TextColor.GREEN
                + BungeecordModuleManager.getActiveModuleString());
        return;
      }
    }

    checkForUpdates(sender);
    MessagesService.sendMessage(
        sender,
        prefix
            + TextColor.GRAY
            + "Coded by "
            + TextColor.GOLD
            + BungeeChatApi.AUTHOR_BRAINSTONE
            + TextColor.GRAY
            + "and "
            + TextColor.GOLD
            + BungeeChatApi.AUTHOR_SHAWN
            + ".");
  }

  private void checkForUpdates(CommandSource sender) {
    BungeeChat instance = BungeeChat.getInstance();
    String latestVersion = instance.getLatestVersion(true);

    if (instance.isLatestVersion()) {
      MessagesService.sendMessage(
          sender,
          prefix + TextColor.GRAY + "Version: " + TextColor.GREEN + BungeeChatApi.VERSION_STR);
    } else {
      MessagesService.sendMessage(
          sender,
          prefix + TextColor.GRAY + "Version: " + TextColor.RED + BungeeChatApi.VERSION_STR);
      MessagesService.sendMessage(
          sender, prefix + TextColor.GRAY + "Newest Version: " + TextColor.GREEN + latestVersion);
    }
  }

  private String getUnquotedString(String str) {
    if ((str == null) || !(str.startsWith("\"") && str.endsWith("\""))) return str;

    new StreamTokenizer(new StringReader(str));
    StreamTokenizer parser = new StreamTokenizer(new StringReader(str));
    String result;

    try {
      parser.nextToken();
      if (parser.ttype == '"') {
        result = parser.sval;
      } else {
        result = "ERROR!";
      }
    } catch (IOException e) {
      result = null;

      LoggerHelper.info("Encountered an IOException while parsing the input string", e);
    }

    return result;
  }
}
