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

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BungeeChatCommand extends BaseCommand {
  private final String prefix = "&9Bungee Chat &8// ";
  private static final List<String> arg1Completetions =
      Arrays.asList("modules", "reload", "setprefix", "setsuffix");

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
                      sender, prefix + "&aThe plugin has been reloaded!");
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
                + "&7Active Modules: &a"
                + BungeecordModuleManager.getActiveModuleString());
        return;
      }
    }

    MessagesService.sendMessage(
        sender,
        prefix
            + "&7Coded by &6"
            + BungeeChatApi.AUTHOR_BRAINSTONE
            + "&7 and &6"
            + BungeeChatApi.AUTHOR_SHAWN
            + ".");
  }

  @Override
  public List<String> suggest(CommandSource sender, String[] args) {
    if(args.length == 0) {
      return arg1Completetions;
    }

    final String param1 = args[0];

    if (args.length == 1 && !arg1Completetions.contains(args[0])) {
      return arg1Completetions.stream()
          .filter(completion -> completion.startsWith(param1))
          .collect(Collectors.toList());
    } else if ((args.length == 2) && ("setprefix".equals(param1) || "setsuffix".equals(param1))) {
      return BungeecordAccountManager.getAccountsForPartialName(args[1], sender).stream()
          .map(BungeeChatAccount::getName)
          .collect(Collectors.toList());
    }

    return super.suggest(sender, args);
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
