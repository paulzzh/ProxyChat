package dev.aura.bungeechat.module;

import dev.aura.bungeechat.api.placeholder.BungeeChatContext;
import dev.aura.bungeechat.api.placeholder.PlaceHolder;
import dev.aura.bungeechat.api.placeholder.PlaceHolderManager;
import dev.aura.bungeechat.util.FloodgateUtil;

public class FloodgateModule extends Module {
	@Override
	public String getName() {
		return "Floodgate";
	}

	@Override
	public void onEnable() {
		PlaceHolderManager.registerPlaceholder(
			new PlaceHolder("platform",
							context -> FloodgateUtil.getPlatformIcon(context.getSender().get()),
							BungeeChatContext.HAS_SENDER));

		PlaceHolderManager.registerPlaceholder(
			new PlaceHolder("target_platform",
							context -> FloodgateUtil.getPlatformIcon(context.getTarget().get()),
							BungeeChatContext.HAS_TARGET));
	}

	@Override
	public void onDisable() {

	}
}
