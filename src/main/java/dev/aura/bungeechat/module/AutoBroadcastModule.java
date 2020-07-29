package dev.aura.bungeechat.module;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;
import com.velocitypowered.api.scheduler.ScheduledTask;
import dev.aura.bungeechat.BungeeChat;
import dev.aura.bungeechat.message.MessagesService;
import dev.aura.bungeechat.task.AutomaticBroadcastTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AutoBroadcastModule extends Module {
	private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;

	private List<ScheduledTask> automaticBroadcastTasks;

	@Override
	public String getName() {
		return "AutoBroadcast";
	}

	@Override
	public void onEnable() {
		automaticBroadcastTasks = new ArrayList<>();
		ConfigList section = getModuleSection().getList("broadcasts");

		section.forEach((ConfigValue broadcast) -> {
			Config broadcastConfig = ((ConfigObject) broadcast).toConfig();

			long interval = broadcastConfig.getDuration("interval", TIME_UNIT);
			long delay = Math.min(10, interval / 2);

			ScheduledTask task = BungeeChat.getInstance().getProxy().getScheduler()
					.buildTask(
							BungeeChat.getInstance(),
							new AutomaticBroadcastTask(
									MessagesService.getServerListPredicate(broadcastConfig.getConfig("serverList")),
									broadcastConfig.getStringList("messages"), broadcastConfig.getBoolean("random")))
					.delay(delay, TIME_UNIT)
					.repeat(interval, TIME_UNIT)
					.schedule();

			automaticBroadcastTasks.add(task);
		});
	}

	@Override
	public void onDisable() {
		automaticBroadcastTasks.forEach(ScheduledTask::cancel);
	}
}
