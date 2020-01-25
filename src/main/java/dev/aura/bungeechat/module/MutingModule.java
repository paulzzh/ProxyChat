package dev.aura.bungeechat.module;

import com.velocitypowered.api.plugin.PluginManager;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.aura.bungeechat.BungeeChat;
import dev.aura.bungeechat.command.MuteCommand;
import dev.aura.bungeechat.command.TempMuteCommand;
import dev.aura.bungeechat.command.UnmuteCommand;
import dev.aura.bungeechat.listener.MutingListener;

public class MutingModule extends Module {
  private MuteCommand muteCommand;
  private TempMuteCommand tempMuteCommand;
  private UnmuteCommand unmuteCommand;
  private MutingListener mutingListener;
  private final String[] mutePlugins = {
    "AdvancedBan", "BungeeBan", "BungeeSystem", "BungeeAdminTools", "Banmanager"
  };

  @Override
  public String getName() {
    return "Muting";
  }

  @Override
  public boolean isEnabled() {
    if (!super.isEnabled()) return false;

    if (getModuleSection().getBoolean("disableWithOtherMutePlugins")) {
      PluginManager pm = BungeeChat.getInstance().getProxy().getPluginManager();

      for (String mutePlugin : mutePlugins) {
        if (pm.getPlugin(mutePlugin).isPresent()) return false;
      }
    }

    return true;
  }

  @Override
  public void onEnable() {
    muteCommand = new MuteCommand(this);
    tempMuteCommand = new TempMuteCommand(this);
    unmuteCommand = new UnmuteCommand(this);
    mutingListener = new MutingListener();

    BungeeChat plugin = BungeeChat.getInstance();
    ProxyServer proxy = plugin.getProxy();

    muteCommand.register();
    tempMuteCommand.register();
    unmuteCommand.register();
    proxy.getEventManager()
        .register(BungeeChat.getInstance(), mutingListener);
  }

  @Override
  public void onDisable() {
    muteCommand.unregister();
    tempMuteCommand.unregister();
    unmuteCommand.unregister();
    BungeeChat.getInstance().getProxy().getEventManager().unregisterListener(BungeeChat.getInstance(), mutingListener);
  }
}
