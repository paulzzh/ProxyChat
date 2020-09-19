package dev.aura.bungeechat.module;

import dev.aura.bungeechat.api.placeholder.BungeeChatContext;
import dev.aura.bungeechat.api.placeholder.PlaceHolder;
import dev.aura.bungeechat.api.placeholder.PlaceHolderManager;
import dev.aura.bungeechat.util.PlatformUtil;

public class PlatformModule extends Module {
	@Override
	public String getName() {
		return "Platform";
	}

	@Override
	public void onEnable() {
		PlaceHolderManager.registerPlaceholder(
			new PlaceHolder("platform",
							context -> PlatformUtil.getPlatformIcon(context.getSender().get()),
							BungeeChatContext.HAS_SENDER));

		PlaceHolderManager.registerPlaceholder(
			new PlaceHolder("target_platform",
							context -> PlatformUtil.getPlatformIcon(context.getTarget().get()),
							BungeeChatContext.HAS_TARGET));
	}

	@Override
	public void onDisable() {

	}
}
