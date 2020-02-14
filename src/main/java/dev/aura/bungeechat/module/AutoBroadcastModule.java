package dev.aura.bungeechat.module;

import com.typesafe.config.Config;
import com.velocitypowered.api.scheduler.ScheduledTask;
import dev.aura.bungeechat.BungeeChat;
import dev.aura.bungeechat.task.AutomaticBroadcastTask;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class AutoBroadcastModule extends Module {
  private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;

  private ScheduledTask automaticBroadcastTask;

  @Override
  public String getName() {
    return "AutoBroadcast";
  }

  @Override
  public void onEnable() {
    Config section = getModuleSection();
    Config serverListSection =
      BungeecordModuleManager.AUTO_BROADCAST_MODULE.getModuleSection().getConfig("serverList");
      boolean serverListDisabled = !serverListSection.getBoolean("enabled");
      List<String> servers = serverListSection.getStringList("list");

    long interval = section.getDuration("interval", TIME_UNIT);
    long delay = Math.min(10, interval / 2);

    automaticBroadcastTask =
        BungeeChat.getInstance().getProxy()
            .getScheduler()
            .buildTask(
                BungeeChat.getInstance(),
                new AutomaticBroadcastTask(
                    section.getStringList("messages"), section.getBoolean("random"),
                    serverListDisabled ? null : servers))
            .delay(delay, TIME_UNIT)
            .repeat(interval, TIME_UNIT)
            .schedule();
  }

  @Override
  public void onDisable() {
    automaticBroadcastTask.cancel();
  }
}
