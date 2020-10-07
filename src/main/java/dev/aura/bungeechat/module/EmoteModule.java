package dev.aura.bungeechat.module;

import dev.aura.bungeechat.api.filter.FilterManager;
import dev.aura.bungeechat.filter.EmoteFilter;

public class EmoteModule extends Module {
	@Override
	public String getName() {
		return "Emotes";
	}

	@Override
	public void onEnable() {
		FilterManager.addPostParseFilter(getName(), new EmoteFilter(getModuleSection().getStringList("emoteNames"),
														   getModuleSection().getString("prefix")));
	}

	@Override
	public void onDisable() {
		FilterManager.removePostParseFilter(getName());
	}
}
