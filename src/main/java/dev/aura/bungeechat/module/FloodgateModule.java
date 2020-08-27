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
		String identifier = getModuleSection().getString("identifier");

		PlaceHolderManager.registerPlaceholder(
			new PlaceHolder(
					"platform",
				context -> FloodgateUtil.isBedrockPlayer(context.getSender().get()) ? identifier : "",
					BungeeChatContext.HAS_SENDER));

		PlaceHolderManager.registerPlaceholder(
			new PlaceHolder(
					"target_platform",
				context -> FloodgateUtil.isBedrockPlayer(context.getTarget().get()) ? identifier : "",
					BungeeChatContext.HAS_TARGET));
	}

	@Override
	public void onDisable() {

	}
}
