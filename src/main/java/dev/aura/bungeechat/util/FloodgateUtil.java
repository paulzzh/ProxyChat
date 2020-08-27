package dev.aura.bungeechat.util;

import dev.aura.bungeechat.BungeeChat;
import dev.aura.bungeechat.api.account.BungeeChatAccount;
import lombok.experimental.UtilityClass;
import org.geysermc.floodgate.FloodgateAPI;

@UtilityClass
public class FloodgateUtil {
	private Boolean floodgateEnabled = null;

	public boolean isBedrockPlayer(BungeeChatAccount player) {
		if(floodgateEnabled == null) {
			floodgateEnabled = BungeeChat.getInstance().getProxy().getPluginManager()
					.getPlugin("floodgate").isPresent();
		}

		return floodgateEnabled && FloodgateAPI.isBedrockPlayer(player.getUniqueId());
	}
}
