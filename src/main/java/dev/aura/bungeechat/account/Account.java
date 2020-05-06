package dev.aura.bungeechat.account;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.ServerInfo;
import dev.aura.bungeechat.api.account.BungeeChatAccount;
import dev.aura.bungeechat.api.enums.ChannelType;
import dev.aura.bungeechat.module.BungeecordModuleManager;
import dev.aura.bungeechat.permission.Permission;
import dev.aura.bungeechat.permission.PermissionManager;
import dev.aura.bungeechat.util.DummyPlayer;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Data
@EqualsAndHashCode(of = "uuid")
public class Account implements BungeeChatAccount {
  protected static ChannelType defaultChannelType = ChannelType.LOCAL;

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private UUID uuid;

  @Getter(lazy = true)
  private final Player player =
      (Player)
          BungeecordAccountManager.getCommandSource(this).orElseGet(() -> new DummyPlayer(uuid));

  private ChannelType channelType;
  private boolean vanished;

  @Getter(AccessLevel.NONE)
  private boolean messanger;

  @Getter(AccessLevel.NONE)
  private boolean socialSpy;

  @Getter(AccessLevel.NONE)
  private boolean localSpy;

  private final BlockingQueue<UUID> ignored;
  private Timestamp mutedUntil;
  private Optional<String> storedPrefix;
  private Optional<String> storedSuffix;

  protected Account(Player player) {
    this(player.getUniqueId());
  }

  protected Account(UUID uuid) {
    this.uuid = uuid;
    channelType = ChannelType.LOCAL;
    vanished = false;
    messanger = true;
    socialSpy = false;
    localSpy = false;
    ignored = new LinkedBlockingQueue<>();
    mutedUntil = new Timestamp(0);
    storedPrefix = Optional.empty();
    storedSuffix = Optional.empty();
  }

  protected Account(
      UUID uuid,
      ChannelType channelType,
      boolean vanished,
      boolean messanger,
      boolean socialSpy,
      boolean localSpy,
      BlockingQueue<UUID> ignored,
      Timestamp mutedUntil,
      Optional<String> storedPrefix,
      Optional<String> storedSuffix) {
    this.uuid = uuid;
    this.channelType = channelType;
    this.vanished = vanished;
    this.messanger = messanger;
    this.socialSpy = socialSpy;
    this.localSpy = localSpy;
    this.ignored = ignored;
    this.mutedUntil = mutedUntil;
    this.storedPrefix = storedPrefix;
    this.storedSuffix = storedSuffix;
  }

  @Override
  public UUID getUniqueId() {
    return uuid;
  }

  @Override
  public ChannelType getDefaultChannelType() {
    return defaultChannelType;
  }

  @Override
  public boolean hasMessangerEnabled() {
    return messanger;
  }

  @Override
  public boolean hasSocialSpyEnabled() {
    if (socialSpy && !PermissionManager.hasPermission(this, Permission.COMMAND_SOCIALSPY)) {
      setSocialSpy(false);
    }

    return socialSpy;
  }

  @Override
  public boolean hasLocalSpyEnabled() {
    if (localSpy && !PermissionManager.hasPermission(this, Permission.COMMAND_LOCALSPY)) {
      setLocalSpy(false);
    }

    return localSpy;
  }

  @Override
  public BlockingQueue<UUID> getIgnored() {
    return BungeecordModuleManager.IGNORING_MODULE.isEnabled()
        ? ignored
        : new LinkedBlockingQueue<>();
  }

  public boolean hasIgnored(Player player) {
    return hasIgnored(player.getUniqueId());
  }

  @Override
  public void addIgnore(UUID uuid) {
    ignored.add(uuid);
  }

  public void addIgnore(Player player) {
    this.addIgnore(player.getUniqueId());
  }

  @Override
  public void removeIgnore(UUID uuid) {
    ignored.remove(uuid);
  }

  public void removeIgnore(Player player) {
    this.removeIgnore(player.getUniqueId());
  }

  @Override
  public String getName() {
    return getPlayer().getUsername();
  }

  @Override
  public String getDisplayName() {
    return getPlayer().getGameProfile().getName();
  }

  @Override
  public int getPing() {
    return (int) getPlayer().getPing();
  }

  @Override
  public String getServerName() {
    try {
      return getServerInfo().getName();
    } catch (NullPointerException e) {
      return unknownServer;
    }
  }

  @Override
  public String getServerIP() {
    try {
      return getServerInfo().getAddress().toString();
    } catch (NullPointerException e) {
      return unknownServer;
    }
  }

  @Override
  public void setDefaultChannelType(ChannelType channelType) {
    defaultChannelType = channelType;
  }

  public static void staticSetDefaultChannelType(ChannelType channelType) {
    defaultChannelType = channelType;
  }

  @Override
  public String toString() {
    return getName();
  }

  private ServerInfo getServerInfo() {
    Player player = getPlayer();
    Optional<ServerConnection> server = player.getCurrentServer();

    return server.map(ServerConnection::getServerInfo).orElse(null);
  }
}
