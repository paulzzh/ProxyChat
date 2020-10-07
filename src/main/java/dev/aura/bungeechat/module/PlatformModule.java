package dev.aura.bungeechat.module;

import dev.aura.bungeechat.api.placeholder.BungeeChatContext;
import dev.aura.bungeechat.api.placeholder.ComponentReplacementSupplier;
import dev.aura.bungeechat.api.placeholder.PlaceHolder;
import dev.aura.bungeechat.api.placeholder.PlaceHolderManager;
import dev.aura.bungeechat.util.PlatformUtil;
import net.kyori.adventure.text.Component;

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
							(ComponentReplacementSupplier) context -> {
								return Component.text(PlatformUtil.getPlatformIcon(context.getSender().get()))
										.hoverEvent(PlatformUtil.getHover(context.getSender().get()));
							},
							BungeeChatContext.HAS_SENDER));

		PlaceHolderManager.registerPlaceholder(
			new PlaceHolder("target_platform",
							context -> PlatformUtil.getPlatformIcon(context.getTarget().get()),
							(ComponentReplacementSupplier) context -> {
								return Component.text(PlatformUtil.getPlatformIcon(context.getTarget().get()))
										.hoverEvent(PlatformUtil.getHover(context.getTarget().get()));
							},
							BungeeChatContext.HAS_TARGET));
	}

	@Override
	public void onDisable() {

	}
}
