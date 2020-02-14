package dev.aura.bungeechat.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigParseOptions;
import com.typesafe.config.ConfigRenderOptions;
import com.typesafe.config.ConfigSyntax;
import com.typesafe.config.ConfigValueFactory;
import dev.aura.bungeechat.BungeeChat;
import dev.aura.bungeechat.api.BungeeChatApi;
import dev.aura.bungeechat.util.LoggerHelper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Delegate;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;

@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class Configuration implements Config {
  protected static final ConfigParseOptions PARSE_OPTIONS =
      ConfigParseOptions.defaults().setAllowMissing(false).setSyntax(ConfigSyntax.CONF);
  protected static final ConfigRenderOptions RENDER_OPTIONS =
      ConfigRenderOptions.defaults().setOriginComments(false).setJson(false);
  protected static final String CONFIG_FILE_NAME = "config.conf";
  protected static final String OLD_CONFIG_FILE_NAME = "config.yml";
  protected static final String OLD_OLD_CONFIG_FILE_NAME = "config.old.yml";
  protected static final File CONFIG_FILE =
      new File(BungeeChat.getInstance().getConfigFolder(), CONFIG_FILE_NAME);
  protected static final File OLD_CONFIG_FILE =
      new File(CONFIG_FILE.getParentFile(), OLD_CONFIG_FILE_NAME);
  protected static final File OLD_OLD_CONFIG_FILE =
      new File(CONFIG_FILE.getParentFile(), OLD_OLD_CONFIG_FILE_NAME);

  @Getter(value = AccessLevel.PROTECTED, lazy = true)
  private static final String header = loadHeader();

  private static Configuration currentConfig;

  @Delegate protected Config config;

  /**
   * Creates and loads the config. Also saves it so that all missing values exist!<br>
   * Also set currentConfig to this config.
   *
   * @return a configuration object, loaded from the config file.
   */
  public static Configuration load() {
    Configuration config = new Configuration();
    config.loadConfig();

    currentConfig = config;

    return currentConfig;
  }

  public static Configuration get() {
    return currentConfig;
  }

  private static String loadHeader() {
    StringBuilder header = new StringBuilder();

    try {
      @Cleanup
      BufferedReader reader =
          new BufferedReader(
              new InputStreamReader(
                      Objects.requireNonNull(BungeeChat.getInstance().getClass().getClassLoader().getResourceAsStream(
                              CONFIG_FILE_NAME)),
                  StandardCharsets.UTF_8));
      String line;

      do {
        line = reader.readLine();

        if (line == null) throw new IOException("Unexpeted EOF while reading " + CONFIG_FILE_NAME);

        header.append(line).append('\n');
      } while (line.startsWith("#"));
    } catch (IOException e) {
      LoggerHelper.error("Error loading file header", e);
    }

    return header.toString();
  }

  protected void loadConfig() {
    Config defaultConfig =
        ConfigFactory.parseReader(
            new InputStreamReader(
                    Objects.requireNonNull(
                            BungeeChat.getInstance().getClass().getClassLoader().getResourceAsStream(CONFIG_FILE_NAME)),
                StandardCharsets.UTF_8),
            PARSE_OPTIONS);

    if (CONFIG_FILE.exists()) {
      try {
        Config fileConfig = ConfigFactory.parseFile(CONFIG_FILE, PARSE_OPTIONS);

        config = fileConfig.withFallback(defaultConfig.withoutPath("ServerAlias"));
      } catch (ConfigException e) {
        LoggerHelper.error("Error while reading config:\n" + e.getLocalizedMessage());

        config = defaultConfig;
      }
    } else {
      config = defaultConfig;
    }

    config = config.resolve();

    convertOldConfig();

    saveConfig();
  }

  protected void saveConfig() {
    try {
      @Cleanup PrintWriter writer = new PrintWriter(CONFIG_FILE, StandardCharsets.UTF_8.name());
      String renderedConfig = config.root().render(RENDER_OPTIONS);
      renderedConfig = getHeader() + renderedConfig;

      writer.print(renderedConfig);
    } catch (FileNotFoundException | UnsupportedEncodingException e) {
      LoggerHelper.error("Something very unexpected happend! Please report this!", e);
    }
  }

  private void convertOldConfig() {
    if (OLD_CONFIG_FILE.exists()) {
      convertYAMLConfig();
    }

    switch (String.format(Locale.ROOT, "%.1f", config.getDouble("Version"))) {
      default:
        // Unknow Version or old version
        // -> Update version
        config =
            config.withValue(
                "Version", ConfigValueFactory.fromAnyRef(BungeeChatApi.CONFIG_VERSION));

      case "11.0":
        // Up to date
        // -> No action needed
    }
  }

  private void convertYAMLConfig() {
    try {
      LoggerHelper.warning("Detected old YAML config. Trying to migrate to HOCON.");

      // Read old config
      ConfigurationNode oldConfig = YAMLConfigurationLoader.builder().setFile(OLD_CONFIG_FILE).build().load();
      ConfigurationNode section;

      // Migrate settigns
      section = oldConfig.getNode("AccountDataBase");
      final ImmutableMap<String, Object> accountDatabase =
          ImmutableMap.<String, Object>builder()
              .put("database", section.getNode("database").getString(""))
              .put("enabled", section.getNode("enabled").getBoolean())
              .put("ip", section.getNode("ip").getString(""))
              .put("password", section.getNode("password").getString(""))
              .put("port", section.getNode("port").getInt())
              .put("tablePrefix", section.getNode("tablePrefix").getString(""))
              .put("user", section.getNode("user").getString(""))
              .build();

      section = oldConfig.getNode("Formats");
      final ImmutableMap<String, Object> formats =
          ImmutableMap.<String, Object>builder()
              .put("alert", section.getNode("alert").getString(""))
              .put("chatLoggingConsole", section.getNode("chatLoggingConsole").getString(""))
              .put("chatLoggingFile", section.getNode("chatLoggingFile").getString(""))
              .put("globalChat", section.getNode("globalChat").getString(""))
              .put("helpOp", section.getNode("helpOp").getString(""))
              .put("joinMessage", section.getNode("joinMessage").getString(""))
              .put("leaveMessage", section.getNode("leaveMessage").getString(""))
              .put("localChat", section.getNode("localChat").getString(""))
              .put("localSpy", section.getNode("localSpy").getString(""))
              .put("messageSender", section.getNode("messageSender").getString(""))
              .put("messageTarget", section.getNode("messageTarget").getString(""))
              .put(
                  "motd",
                  String.join("\n",
                              oldConfig.getNode("Settings.Modules.MOTD.message").getList(Object::toString)))
              .put("serverSwitch", section.getString("serverSwitch"))
              .put("socialSpy", section.getString("socialSpy"))
              .put("staffChat", section.getString("staffChat"))
              .put(
                  "welcomeMessage",
                  String.join("\n",
                              oldConfig.getNode("Settings.Modules.WelcomeMessage.message").getList(Object::toString)))
              .build();

      final ConfigurationNode modulesSection =
          oldConfig.getNode("Settings.Modules");
      section = modulesSection.getNode("Alert");
      final ImmutableMap<String, Object> moduleAlert =
          ImmutableMap.<String, Object>builder()
              .put("aliases", section.getNode("aliases").getList(Object::toString))
              .put("enabled", section.getNode("enabled").getBoolean())
              .build();

      section = modulesSection.getNode("AntiAdvertising");
      final ImmutableMap<String, Object> moduleAntiAdvertising =
          ImmutableMap.<String, Object>builder()
              .put("enabled", section.getNode("enabled").getBoolean())
              .put("whitelisted", section.getNode("whitelisted").getList(Object::toString))
              .build();

      section = modulesSection.getNode("AntiDuplication");
      final ImmutableMap<String, Object> moduleAntiDuplication =
          ImmutableMap.<String, Object>builder()
              .put("checkPastMessages", section.getNode("checkPastMessages").getInt())
              .put("enabled", section.getNode("enabled").getBoolean())
              .build();

      section = modulesSection.getNode("AntiSwear");
      final ImmutableMap<String, Object> moduleAntiSwear =
          ImmutableMap.<String, Object>builder()
              .put("enabled", section.getNode("enabled").getBoolean())
              .put("freeMatching", section.getNode("freeMatching").getBoolean())
              .put("ignoreDuplicateLetters", section.getNode("ignoreDuplicateLetters").getBoolean())
              .put("ignoreSpaces", section.getNode("ignoreSpaces").getBoolean())
              .put("leetSpeak", section.getNode("leetSpeak").getBoolean())
              .put("replacement", section.getString("replacement"))
              .put("words", section.getNode("words").getList(Object::toString))
              .build();

      section = modulesSection.getNode("AutoBroadcast");
      final ImmutableMap<String, Object> moduleAutoBroadcast =
          ImmutableMap.<String, Object>builder()
              .put("enabled", section.getNode("enabled").getBoolean())
              .put("interval", section.getNode("interval").getInt() + "s")
              .put("messages", section.getNode("messages").getList(Object::toString))
              .put("random", section.getNode("random").getBoolean())
              .build();

      section = modulesSection.getNode("ChatLock");
      final ImmutableMap<String, Object> moduleChatLock =
          ImmutableMap.<String, Object>builder()
              .put("aliases", section.getNode("aliases").getList(Object::toString))
              .put("emptyLinesOnClear", section.getNode("emptyLinesOnClear").getInt())
              .put("enabled", section.getNode("enabled").getBoolean())
              .build();

      section = modulesSection.getNode("ChatLogging");
      final ImmutableMap<String, Object> moduleChatLogging =
          ImmutableMap.<String, Object>builder()
              .put("console", section.getNode("console").getBoolean())
              .put("enabled", section.getNode("enabled").getBoolean())
              .put("file", section.getNode("file").getBoolean())
              .put("filteredCommands", section.getNode("filteredCommands").getList(Object::toString))
              .put("logFile", section.getString("logFile"))
              .put("privateMessages", section.getNode("privateMessages").getBoolean())
              .build();

      section = modulesSection.getNode("ClearChat");
      final ImmutableMap<String, Object> moduleClearChat =
          ImmutableMap.<String, Object>builder()
              .put("aliases", section.getNode("aliases").getList(Object::toString))
              .put("emptyLinesOnClear", section.getNode("emptyLinesOnClear").getInt())
              .put("enabled", section.getNode("enabled").getBoolean())
              .build();

      section = modulesSection.getNode("GlobalChat");
      final ImmutableMap<String, Object> moduleGlobalChatServerList =
          ImmutableMap.<String, Object>builder()
              .put("enabled", section.getNode("serverList.enabled").getBoolean())
              .put("serverList", section.getNode("serverList.serverList").getList(Object::toString))
              .build();
      final ImmutableMap<String, Object> moduleGlobalChatSymbol =
          ImmutableMap.<String, Object>builder()
              .put("enabled", section.getNode("symbol.enabled").getBoolean())
              .put("symbol", section.getString("symbol.symbol"))
              .build();
      final ImmutableMap<String, Object> moduleGlobalChat =
          ImmutableMap.<String, Object>builder()
              .put("aliases", section.getNode("aliases").getList(Object::toString))
              .put("default", section.getNode("default").getBoolean())
              .put("enabled", section.getNode("enabled").getBoolean())
              .put("passToClientServer", section.getNode("passToClientServer").getBoolean())
              .put("serverList", moduleGlobalChatServerList)
              .put("symbol", moduleGlobalChatSymbol)
              .build();

      section = modulesSection.getNode("HelpOp");
      final ImmutableMap<String, Object> moduleHelpOp =
          ImmutableMap.<String, Object>builder()
              .put("aliases", section.getNode("aliases").getList(Object::toString))
              .put("enabled", section.getNode("enabled").getBoolean())
              .build();

      section = modulesSection.getNode("Ignoring");
      final ImmutableMap<String, Object> moduleIgnoring =
          ImmutableMap.<String, Object>builder()
              .put("aliases", section.getNode("aliases").getList(Object::toString))
              .put("enabled", section.getNode("enabled").getBoolean())
              .build();

      section = modulesSection.getNode("JoinMessage");
      final ImmutableMap<String, Object> moduleJoinMessage =
          ImmutableMap.<String, Object>builder()
              .put("enabled", section.getNode("enabled").getBoolean())
              .build();

      section = modulesSection.getNode("LeaveMessage");
      final ImmutableMap<String, Object> moduleLeaveMessage =
          ImmutableMap.<String, Object>builder()
              .put("enabled", section.getNode("enabled").getBoolean())
              .build();

      section = modulesSection.getNode("LocalChat");
      final ImmutableMap<String, Object> moduleLocalChat =
          ImmutableMap.<String, Object>builder()
              .put("enabled", section.getNode("enabled").getBoolean())
              .put("passToClientServer", section.getNode("passToClientServer").getBoolean())
              .build();

      section = modulesSection.getNode("MOTD");
      final ImmutableMap<String, Object> moduleMOTD =
          ImmutableMap.<String, Object>builder()
              .put("enabled", section.getNode("enabled").getBoolean())
              .build();

      section = modulesSection.getNode("Messenger");
      final ImmutableMap<String, Object> moduleMessengerAliases =
          ImmutableMap.<String, Object>builder()
              .put("message", section.getNode("aliases.message").getList(Object::toString))
              .put("msgtoggle", section.getNode("aliases.msgtoggle").getList(Object::toString))
              .put("reply", section.getNode("aliases.reply").getList(Object::toString))
              .build();
      final ImmutableMap<String, Object> moduleMessenger =
          ImmutableMap.<String, Object>builder()
              .put("aliases", moduleMessengerAliases)
              .put("enabled", section.getNode("enabled").getBoolean())
              .put("filterPrivateMessages", section.getNode("filterMessages").getBoolean())
              .build();

      section = modulesSection.getNode("Muting");
      final ImmutableMap<String, Object> moduleMutingAliases =
          ImmutableMap.<String, Object>builder()
              .put("mute", section.getNode("aliases.mute").getList(Object::toString))
              .put("tempmute", section.getNode("aliases.tempmute").getList(Object::toString))
              .put("unmute", section.getNode("aliases.unmute").getList(Object::toString))
              .build();
      final ImmutableMap<String, Object> moduleMuting =
          ImmutableMap.<String, Object>builder()
              .put("aliases", moduleMutingAliases)
              .put("blockedcommands", section.getNode("blockedcommands").getList(Object::toString))
              .put("disableWithOtherMutePlugins", section.getNode("disableWithOtherMutePlugins").getBoolean())
              .put("enabled", section.getNode("enabled").getBoolean())
              .build();

      section = modulesSection.getNode("ServerSwitchMessages");
      final ImmutableMap<String, Object> moduleServerSwitchMessages =
          ImmutableMap.<String, Object>builder()
              .put("enabled", section.getNode("enabled").getBoolean())
              .build();

      section = modulesSection.getNode("Spy");
      final ImmutableMap<String, Object> moduleSpyAliases =
          ImmutableMap.<String, Object>builder()
              .put("localspy", section.getNode("aliases.localspy").getList(Object::toString))
              .put("socialspy", section.getNode("aliases.socialspy").getList(Object::toString))
              .build();
      final ImmutableMap<String, Object> moduleSpy =
          ImmutableMap.<String, Object>builder()
              .put("aliases", moduleSpyAliases)
              .put("enabled", section.getNode("enabled").getBoolean())
              .build();

      section = modulesSection.getNode("StaffChat");
      final ImmutableMap<String, Object> moduleStaffChat =
          ImmutableMap.<String, Object>builder()
              .put("aliases", section.getNode("aliases").getList(Object::toString))
              .put("enabled", section.getNode("enabled").getBoolean())
              .put("passToClientServer", section.getNode("passToClientServer").getBoolean())
              .build();

      section = modulesSection.getNode("TabCompletion");
      final ImmutableMap<String, Object> moduleTabCompletion =
          ImmutableMap.<String, Object>builder()
              .put("enabled", section.getNode("enabled").getBoolean())
              .build();

      section = modulesSection.getNode("Vanish");
      final ImmutableMap<String, Object> moduleVanish =
          ImmutableMap.<String, Object>builder()
              .put("aliases", section.getNode("aliases").getList(Object::toString))
              .put("enabled", section.getNode("enabled").getBoolean())
              .build();

      section = modulesSection.getNode("WelcomeMessage");
      final ImmutableMap<String, Object> moduleWelcomeMessage =
          ImmutableMap.<String, Object>builder()
              .put("enabled", section.getNode("enabled").getBoolean())
              .build();

      final ImmutableMap<String, Object> modules =
          ImmutableMap.<String, Object>builder()
              .put("Alert", moduleAlert)
              .put("AntiAdvertising", moduleAntiAdvertising)
              .put("AntiDuplication", moduleAntiDuplication)
              .put("AntiSwear", moduleAntiSwear)
              .put("AutoBroadcast", moduleAutoBroadcast)
              .put("ChatLock", moduleChatLock)
              .put("ChatLogging", moduleChatLogging)
              .put("ClearChat", moduleClearChat)
              .put("GlobalChat", moduleGlobalChat)
              .put("HelpOp", moduleHelpOp)
              .put("Ignoring", moduleIgnoring)
              .put("JoinMessage", moduleJoinMessage)
              .put("LeaveMessage", moduleLeaveMessage)
              .put("LocalChat", moduleLocalChat)
              .put("MOTD", moduleMOTD)
              .put("Messenger", moduleMessenger)
              .put("Muting", moduleMuting)
              .put("ServerSwitchMessages", moduleServerSwitchMessages)
              .put("Spy", moduleSpy)
              .put("StaffChat", moduleStaffChat)
              .put("TabCompletion", moduleTabCompletion)
              .put("Vanish", moduleVanish)
              .put("WelcomeMessage", moduleWelcomeMessage)
              .build();

      section = oldConfig.getNode("Settings.PermissionsManager");
      final ImmutableMap<String, Object> permissionsManager =
          ImmutableMap.<String, Object>builder()
              .put("defaultPrefix", section.getString("defaultPrefix"))
              .put("defaultSuffix", section.getString("defaultSuffix"))
              .build();

      section = oldConfig.getNode("Settings.ServerAlias");
//      ConfigurationNode finalSection = section;
//      final ImmutableMap<String, String> serverAlias =
//          section.getChildrenMap().keySet().stream()
//              .collect(MapUtils.immutableMapCollector(key -> key, finalSection.getNode(key).getString()));

      final ImmutableMap<String, Object> configMap =
          ImmutableMap.<String, Object>builder()
              .put("AccountDatabase", accountDatabase)
              .put("Formats", formats)
              .put("Modules", modules)
              .put("PrefixDefaults", permissionsManager)
//              .put("ServerAlias", serverAlias)
              .build();

      config =
          ConfigFactory.parseMap(configMap)
              .withFallback(config.withoutPath("ServerAlias"))
              .resolve();

      // Rename old file
      Files.move(OLD_CONFIG_FILE, OLD_OLD_CONFIG_FILE);

      LoggerHelper.info("Old config file has been renamed to config.old.yml.");

      // Done
      LoggerHelper.info("Migration successful!");
    } catch (Exception e) {
      LoggerHelper.error("There has been an error while migrating the old YAML config file!", e);
    }
  }
}
