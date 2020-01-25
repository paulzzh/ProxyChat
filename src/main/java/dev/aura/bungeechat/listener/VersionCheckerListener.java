package dev.aura.bungeechat.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.proxy.Player;
import dev.aura.bungeechat.BungeeChat;
import dev.aura.bungeechat.message.Messages;
import dev.aura.bungeechat.message.MessagesService;
import dev.aura.bungeechat.permission.Permission;
import dev.aura.bungeechat.permission.PermissionManager;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VersionCheckerListener {
  private static final long FIVE_MINUTES = TimeUnit.MINUTES.toMillis(5);

  private long lastCheck = System.currentTimeMillis();
  private final boolean checkOnAdminLogin;

  @Subscribe
  public void onPlayerJoin(PostLoginEvent e) {
    Player player = e.getPlayer();

    if (PermissionManager.hasPermission(player, Permission.CHECK_VERSION)) {
      BungeeChat instance = BungeeChat.getInstance();

      BungeeChat.getInstance().getProxy().getScheduler()
          .buildTask(instance, new VersionCheckerTask(player, instance)).delay(1, TimeUnit.SECONDS);
    }
  }

  @RequiredArgsConstructor
  private class VersionCheckerTask implements Runnable {
    private final Player player;
    private final BungeeChat instance;

    @Override
    public void run() {
      if (checkOnAdminLogin || ((lastCheck + FIVE_MINUTES) < System.currentTimeMillis())) {
        instance.getLatestVersion(true);
        lastCheck = System.currentTimeMillis();
      }

      if (!instance.isLatestVersion() && player.isActive()) {
        MessagesService.sendMessage(
            player, Messages.UPDATE_AVAILABLE.get(player, instance.getLatestVersion()));
      }
    }
  }
}
