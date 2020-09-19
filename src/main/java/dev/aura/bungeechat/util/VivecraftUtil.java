package dev.aura.bungeechat.util;

import com.techjar.vbe.VivecraftAPI;
import dev.aura.bungeechat.BungeeChat;
import dev.aura.bungeechat.api.account.BungeeChatAccount;
import lombok.experimental.UtilityClass;

@UtilityClass
public class VivecraftUtil {
	private Boolean vivecraftEnabled = null;

	public boolean isVivecraftPlayer(BungeeChatAccount player) {
		if(vivecraftEnabled == null) {
			vivecraftEnabled = BungeeChat.getInstance().getProxy().getPluginManager()
					.getPlugin("vivecraft-velocity-extensions").isPresent();
		}

		return vivecraftEnabled  && VivecraftAPI.isVive(player.getUniqueId());
	}

	public boolean isVR(BungeeChatAccount player) {
		if(vivecraftEnabled == null) {
			vivecraftEnabled = BungeeChat.getInstance().getProxy().getPluginManager()
					.getPlugin("vivecraft-velocity-extensions").isPresent();
		}

		return vivecraftEnabled && VivecraftAPI.isVR(player.getUniqueId());
	}

	public String getPlatformIcon(BungeeChatAccount player) {
		if(vivecraftEnabled == null) {
			vivecraftEnabled = BungeeChat.getInstance().getProxy().getPluginManager()
					.getPlugin("vivecraft-velocity-extensions").isPresent();
		}

		if(!isVivecraftPlayer(player)) {
			return null;
		}

		return isVR(player) ? String.valueOf('\uE1DC') : String.valueOf('\uE1DE');
	}
}
