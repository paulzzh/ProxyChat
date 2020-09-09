package dev.aura.bungeechat.util;

import dev.aura.bungeechat.BungeeChat;
import dev.aura.bungeechat.api.account.BungeeChatAccount;
import lombok.experimental.UtilityClass;
import org.geysermc.floodgate.FloodgateAPI;
import org.geysermc.floodgate.FloodgatePlayer;

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

	public String getPlatformIcon(BungeeChatAccount player) {
		if(floodgateEnabled == null) {
			floodgateEnabled = BungeeChat.getInstance().getProxy().getPluginManager()
					.getPlugin("floodgate").isPresent();
		}

		if(!FloodgateUtil.isBedrockPlayer(player)) {
			return "";
		}

		FloodgatePlayer floodgatePlayer = FloodgateAPI.getPlayer(player.getUniqueId());

		if(floodgatePlayer == null) {
			return "";
		}

		switch(floodgatePlayer.getDeviceOS()) {
			case ANDROID:
				return String.valueOf('\uE1D0');
			case IOS:
				return String.valueOf('\uE1D1');
			case OSX:
				return String.valueOf('\uE1D2');
			case FIREOS:
				return String.valueOf('\uE1D3');
			case GEARVR:
				return String.valueOf('\uE1D4');
			case HOLOLENS:
				return String.valueOf('\uE1D5');
			case WIN10:
				return String.valueOf('\uE1D6');
			case WIN32:
				return String.valueOf('\uE1D7');
			case ORBIS:
				return String.valueOf('\uE1D8');
			case NX:
			case SWITCH:
				return String.valueOf('\uE1D9');
			case XBOX_ONE:
				return String.valueOf('\uE1DA');
			case WIN_PHONE:
				return String.valueOf('\uE1DB');
			default:
				return String.valueOf('\uE11B');
		}
	}
}
