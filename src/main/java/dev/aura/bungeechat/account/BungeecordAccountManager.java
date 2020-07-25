package dev.aura.bungeechat.account;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.permission.Tristate;
import com.velocitypowered.api.proxy.ConnectionRequestBuilder;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.player.PlayerSettings;
import com.velocitypowered.api.proxy.player.TabList;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.util.GameProfile;
import com.velocitypowered.api.util.MessagePosition;
import com.velocitypowered.api.util.ModInfo;
import com.velocitypowered.api.util.title.Title;
import dev.aura.bungeechat.BungeeChat;
import dev.aura.bungeechat.api.account.AccountInfo;
import dev.aura.bungeechat.api.account.AccountManager;
import dev.aura.bungeechat.api.account.BungeeChatAccount;
import dev.aura.bungeechat.event.BungeeChatJoinEvent;
import dev.aura.bungeechat.event.BungeeChatLeaveEvent;
import net.kyori.text.Component;

import java.net.InetSocketAddress;
import dev.aura.bungeechat.permission.Permission;
import dev.aura.bungeechat.permission.PermissionManager;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class BungeecordAccountManager extends AccountManager {
  private static final ConcurrentMap<UUID, CommandSource> nativeObjects = new ConcurrentHashMap<>();
  private static final List<UUID> newPlayers = new LinkedList<>();

  public static Optional<BungeeChatAccount> getAccount(CommandSource player) {
    if (player instanceof Player) return getAccount(((Player) player).getUniqueId());
    else if (player == null) return Optional.empty();
    else return Optional.of(consoleAccount);
  }

  public static Optional<CommandSource> getCommandSource(UUID uuid) {
    return Optional.ofNullable(nativeObjects.get(uuid));
  }

  public static Optional<CommandSource> getCommandSource(BungeeChatAccount account) {
    return getCommandSourceFromAccount(account);
  }

  public static List<BungeeChatAccount> getAccountsForPartialName(
      String partialName, CommandSource player) {
    List<BungeeChatAccount> accounts = getAccountsForPartialName(partialName);

    if (!PermissionManager.hasPermission(player, Permission.COMMAND_VANISH_VIEW)) {
      accounts =
          accounts.stream().filter(account -> !account.isVanished()).collect(Collectors.toList());
    }

    return accounts;
  }

  public static List<BungeeChatAccount> getAccountsForPartialName(
      String partialName, BungeeChatAccount account) {
    return getAccountsForPartialName(partialName, getCommandSourceFromAccount(account).orElse(null));
  }

  public static void loadAccount(UUID uuid) {
    AccountInfo loadedAccount = getAccountStorage().load(uuid);

    accounts.put(uuid, loadedAccount.getAccount());
    nativeObjects.put(uuid, getCommandSourceFromAccount(loadedAccount.getAccount()).get());

    if (loadedAccount.isForceSave()) {
      saveAccount(loadedAccount.getAccount());
    }

    if (loadedAccount.isNewAccount()) {
      newPlayers.add(loadedAccount.getAccount().getUniqueId());
    }
  }

  public static void unloadAccount(UUID uuid) {
    Optional<BungeeChatAccount> account = getAccount(uuid);

    account.ifPresent(
        acc -> {
          unloadAccount(acc);
          newPlayers.remove(acc.getUniqueId());
        });
  }

  public static void unloadAccount(BungeeChatAccount account) {
    AccountManager.unloadAccount(account);
    nativeObjects.remove(account.getUniqueId());
  }

  public static boolean isNew(UUID uuid) {
    return newPlayers.contains(uuid);
  }

  private static Optional<CommandSource> getCommandSourceFromAccount(BungeeChatAccount account) {
    if(BungeeChat.getInstance() == null || BungeeChat.getInstance().getProxy() == null) {
      return Optional.of(new DummyConsole());
    }

    ProxyServer instance = BungeeChat.getInstance().getProxy();

    if (instance == null) return Optional.of(new DummyConsole());

    if(account == null) {
      return Optional.empty();
    }

    switch (account.getAccountType()) {
      case PLAYER:
        return Optional.ofNullable(instance.getPlayer(account.getUniqueId()).orElse(null));
      case CONSOLE:
      default:
        return Optional.ofNullable(instance.getConsoleCommandSource());
    }
  }

  @Subscribe(order = PostOrder.LATE)
  public void onPlayerConnect(BungeeChatJoinEvent event) {
    loadAccount(event.getPlayer().getUniqueId());
  }

  @Subscribe(order = PostOrder.LATE)
  public void onPlayerDisconnect(BungeeChatLeaveEvent event) {
    unloadAccount(event.getPlayer().getUniqueId());
  }

  static {
    nativeObjects.put(consoleAccount.getUniqueId(), getCommandSourceFromAccount(consoleAccount).get());
  }

  private static class DummyConsole implements Player {
    @Override
    public String getUsername() {
      return null;
    }

    @Override
    public UUID getUniqueId() {
      return null;
    }

    @Override
    public Optional<ServerConnection> getCurrentServer() {
      return Optional.empty();
    }

    @Override
    public PlayerSettings getPlayerSettings() {
      return null;
    }

    @Override
    public Optional<ModInfo> getModInfo() {
      return Optional.empty();
    }

    @Override
    public long getPing() {
      return 0;
    }

    @Override
    public boolean isOnlineMode() {
      return false;
    }

    @Override
    public void sendMessage(Component message) {
      // Do nothing
    }

    @Override
    public void sendMessage(Component component, MessagePosition position) {

    }

    @Override
    public ConnectionRequestBuilder createConnectionRequest(
            RegisteredServer server) {
      return null;
    }

    @Override
    public List<GameProfile.Property> getGameProfileProperties() {
      return null;
    }

    @Override
    public void setGameProfileProperties(List<GameProfile.Property> properties) {

    }

    @Override
    public GameProfile getGameProfile() {
      return null;
    }

    @Override
    public void setHeaderAndFooter(Component header, Component footer) {

    }

    @Override
    public void clearHeaderAndFooter() {

    }

    @Override
    public TabList getTabList() {
      return null;
    }

    @Override
    public void disconnect(Component reason) {

    }

    @Override
    public void disconnect(net.kyori.adventure.text.Component reason) {

    }

    @Override
    public void sendTitle(Title title) {

    }

    @Override
    public void spoofChatInput(String input) {

    }

    @Override
    public void sendResourcePack(String url) {

    }

    @Override
    public void sendResourcePack(String url, byte[] hash) {

    }

    @Override
    public boolean hasPermission(String permission) {
      return true;
    }

    @Override
    public Tristate getPermissionValue(String permission) {
      return Tristate.fromBoolean(true);
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
      return null;
    }

    @Override
    public Optional<InetSocketAddress> getVirtualHost() {
      return Optional.empty();
    }

    @Override
    public boolean isActive() {
      return false;
    }

    @Override
    public ProtocolVersion getProtocolVersion() {
      return null;
    }

    @Override
    public boolean sendPluginMessage(ChannelIdentifier identifier, byte[] data) {
      return false;
    }
  }
}
