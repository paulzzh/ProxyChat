package dev.aura.bungeechat.util;

import dev.aura.bungeechat.api.account.BungeeChatAccount;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PlatformUtil {
	public String getPlatformIcon(BungeeChatAccount player) {
		String icon = null;

		if(FloodgateUtil.isBedrockPlayer(player)) {
			icon = FloodgateUtil.getPlatformIcon(player);
		}

		if(icon == null && VivecraftUtil.isVivecraftPlayer(player)) {
			icon = VivecraftUtil.getPlatformIcon(player);
		}

		return icon != null ? icon : String.valueOf('\uE1DD');
	}
}
