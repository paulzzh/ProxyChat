package dev.aura.bungeechat;

import com.google.common.annotations.VisibleForTesting;
import com.typesafe.config.Config;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.aura.bungeechat.account.AccountFileStorage;
import dev.aura.bungeechat.account.AccountSQLStorage;
import dev.aura.bungeechat.account.BungeecordAccountManager;
import dev.aura.bungeechat.api.BungeeChatApi;
import dev.aura.bungeechat.api.account.AccountManager;
import dev.aura.bungeechat.api.enums.ChannelType;
import dev.aura.bungeechat.api.hook.HookManager;
import dev.aura.bungeechat.api.module.ModuleManager;
import dev.aura.bungeechat.api.placeholder.BungeeChatContext;
import dev.aura.bungeechat.api.placeholder.InvalidContextError;
import dev.aura.bungeechat.api.placeholder.PlaceHolderManager;
import dev.aura.bungeechat.api.utils.BungeeChatInstaceHolder;
import dev.aura.bungeechat.command.BungeeChatCommand;
import dev.aura.bungeechat.config.Configuration;
import dev.aura.bungeechat.hook.DefaultHook;
import dev.aura.bungeechat.hook.StoredDataHook;
import dev.aura.bungeechat.listener.BungeeChatEventsListener;
import dev.aura.bungeechat.listener.ChannelTypeCorrectorListener;
import dev.aura.bungeechat.message.MessagesService;
import dev.aura.bungeechat.message.PlaceHolderUtil;
import dev.aura.bungeechat.message.PlaceHolders;
import dev.aura.bungeechat.message.ServerAliases;
import dev.aura.bungeechat.module.BungeecordModuleManager;
import dev.aura.bungeechat.util.LoggerHelper;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.net.ssl.HttpsURLConnection;
import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;
import net.kyori.text.format.TextColor;
import org.slf4j.Logger;

@Plugin(id = "bungeechat", name = "BungeeChat", version = "0.1-SNAPSHOT",
        description = "", authors = {"Jim (NotKatuen)"}, dependencies = {
  @Dependency(id = "luckperms", optional = true)
})
public class BungeeChat implements BungeeChatApi {
  private static final String storedDataHookName = "storedData";
  private static final String defaultHookName = "default";
  private static final String errorVersion = "error";

  @Getter
  @Setter(AccessLevel.PRIVATE)
  @VisibleForTesting
  static BungeeChat instance;

  @Inject
  @DataDirectory
  private Path configDir;
  private File langDir;

  private final ProxyServer proxy;
  private final Logger logger;

  private BungeecordAccountManager bungeecordAccountManager;
  private ChannelTypeCorrectorListener channelTypeCorrectorListener;
  private BungeeChatEventsListener bungeeChatEventsListener;

  @Inject
  public BungeeChat(ProxyServer proxy, Logger logger) {
    this.proxy = proxy;
    this.logger = logger;
    setInstance(this);
    BungeeChatInstaceHolder.setInstance(instance);
  }

  @Subscribe
  public void onProxyInitialized(ProxyInitializeEvent event) {
    onEnable(true);
  }

  public void onEnable(boolean prinLoadScreen) {
    Configuration.load();
    PlaceHolderUtil.loadConfigSections();

    PlaceHolders.registerPlaceHolders();

    Config accountDatabase = Configuration.get().getConfig("AccountDatabase");

    if (accountDatabase.getBoolean("enabled")) {
      try {
        AccountManager.setAccountStorage(
            new AccountSQLStorage(
                accountDatabase.getString("ip"),
                accountDatabase.getInt("port"),
                accountDatabase.getString("database"),
                accountDatabase.getString("user"),
                accountDatabase.getString("password"),
                accountDatabase.getString("tablePrefix")));
      } catch (SQLException e) {
        LoggerHelper.error("Could not connect to specified database. Using file storage", e);

        AccountManager.setAccountStorage(new AccountFileStorage());
      }
    } else {
      AccountManager.setAccountStorage(new AccountFileStorage());
    }

    BungeeChatCommand bungeeChatCommand = new BungeeChatCommand();
    bungeecordAccountManager = new BungeecordAccountManager();
    channelTypeCorrectorListener = new ChannelTypeCorrectorListener();
    bungeeChatEventsListener = new BungeeChatEventsListener();

    bungeeChatCommand.register();
    proxy.getEventManager().register(this, bungeecordAccountManager);
    proxy.getEventManager().register(this, channelTypeCorrectorListener);
    proxy.getEventManager().register(this, bungeeChatEventsListener);

    Config prefixDefaults = Configuration.get().getConfig("PrefixDefaults");

    BungeecordModuleManager.registerPluginModules();
    ModuleManager.enableModules();
    HookManager.addHook(storedDataHookName, new StoredDataHook());
    HookManager.addHook(
        defaultHookName,
        new DefaultHook(
            prefixDefaults.getString("defaultPrefix"), prefixDefaults.getString("defaultSuffix")));
    ServerAliases.loadAliases();

    if (prinLoadScreen) {
      loadScreen();
    }
  }

  public void onDisable() {
    HookManager.removeHook(defaultHookName);
    HookManager.removeHook(storedDataHookName);
    ModuleManager.disableModules();

    proxy.getEventManager().unregisterListener(this, bungeecordAccountManager);
    //proxy.getEventManager().unregisterCommand(bungeeChatCommand);
    proxy.getEventManager().unregisterListener(this, channelTypeCorrectorListener);
    proxy.getEventManager().unregisterListener(this, bungeeChatEventsListener);

    //proxy.getScheduler().cancel(this);

    // Just to be sure
    proxy.getEventManager().unregisterListeners(this);
    //proxy.getCommandManager().unregisterCommands(this);

    PlaceHolderManager.clear();
    PlaceHolderUtil.clearConfigSections();
    ModuleManager.clearActiveModules();
  }

  @Override
  public File getConfigFolder() {
    if(this.configDir == null) {
      return null;
    }

    File configDir = this.configDir.toFile();

    if (!configDir.exists() && !configDir.mkdirs()) {
        throw new RuntimeException(new IOException("Could not create " + configDir));
    }

    return configDir;
  }

  public File getLangFolder() {
    if (langDir == null) {
      langDir = new File(getConfigFolder(), "lang");

      if (!langDir.exists() && !langDir.mkdirs())
        throw new RuntimeException(new IOException("Could not create " + langDir));
    }

    return langDir;
  }

  @Override
  public void sendPrivateMessage(BungeeChatContext context) throws InvalidContextError {
    MessagesService.sendPrivateMessage(context);
  }

  @Override
  public void sendChannelMessage(BungeeChatContext context, ChannelType channel)
      throws InvalidContextError {
    MessagesService.sendChannelMessage(context, channel);
  }

  private void loadScreen() {
    StartupBannerSize size =
        StartupBannerSize.optionalValueOf(
                Configuration.get().getString("Miscellaneous.startupBannerSize"))
            .orElse(StartupBannerSize.NORMAL);

    if (size == StartupBannerSize.NONE) return;

    if (size != StartupBannerSize.SHORT) {
      LoggerHelper.info(
              TextColor.GOLD
              + "---------------- "
              + TextColor.AQUA
              + "Bungee Chat"
              + TextColor.GOLD
              + " ----------------");
      LoggerHelper.info(getPeopleMessage("Authors", BungeeChatApi.AUTHORS));
    }

    LoggerHelper.info(TextColor.YELLOW + "Version: " + TextColor.GREEN + VERSION_STR);

    if (size == StartupBannerSize.LONG) {
      LoggerHelper.info(TextColor.YELLOW + "Modules:");

      ModuleManager.getAvailableModulesStream()
          .map(
              module -> {
                if (module.isEnabled()) return "\t" + TextColor.GREEN + "On  - " + module.getName();
                else return "\t" + TextColor.RED + "Off - " + module.getName();
              })
          .forEachOrdered(LoggerHelper::info);
    } else {
      LoggerHelper.info(
          TextColor.YELLOW
              + "Modules: "
              + TextColor.GREEN
              + BungeecordModuleManager.getActiveModuleString());
    }

    if (size != StartupBannerSize.SHORT) {
      LoggerHelper.info(getPeopleMessage("Contributors", BungeeChatApi.CONTRIBUTORS));
      LoggerHelper.info(getPeopleMessage("Translators", BungeeChatApi.TRANSLATORS));
      LoggerHelper.info(getPeopleMessage("Donators", BungeeChatApi.DONATORS));
    }

    if (size != StartupBannerSize.SHORT) {
      LoggerHelper.info(TextColor.GOLD + "---------------------------------------------");
    }
  }

  private String getPeopleMessage(String name, String... people) {
    return Arrays.stream(people)
        .collect(
            Collectors.joining(
                BungeecordModuleManager.MODULE_CONCATENATOR,
                TextColor.YELLOW + name + ": " + TextColor.GREEN,
                ""));
  }

  public Logger getLogger() {
    return logger;
  }

  public ProxyServer getProxy() {
    return proxy;
  }

  private enum StartupBannerSize {
    NONE,
    SHORT,
    NORMAL,
    LONG;

    public static Optional<StartupBannerSize> optionalValueOf(String value) {
      for (StartupBannerSize element : values()) {
        if (element.name().equalsIgnoreCase(value)) return Optional.of(element);
      }

      return Optional.empty();
    }
  }
}
