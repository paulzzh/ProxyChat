/*
 * ProxyChat, a Velocity chat solution
 * Copyright (C) 2020 James Lyne
 *
 * Based on BungeeChat2 (https://github.com/AuraDevelopmentTeam/BungeeChat2)
 * Copyright (C) 2020 Aura Development Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package uk.co.notnull.ProxyChat.util;

import uk.co.notnull.ProxyChat.ProxyChat;
import uk.co.notnull.ProxyChat.api.account.ProxyChatAccount;
import lombok.experimental.UtilityClass;
import org.geysermc.floodgate.FloodgateAPI;
import org.geysermc.floodgate.FloodgatePlayer;

import java.util.Optional;

@UtilityClass
public class FloodgateUtil {
	private Boolean floodgateEnabled = null;

	public boolean isBedrockPlayer(ProxyChatAccount player) {
		if(floodgateEnabled == null) {
			floodgateEnabled = ProxyChat.getInstance().getProxy().getPluginManager()
					.getPlugin("floodgate").isPresent();
		}

		return floodgateEnabled && FloodgateAPI.isBedrockPlayer(player.getUniqueId());
	}

	private Optional<FloodgatePlayer> getFloodgatePlayer(ProxyChatAccount player) {
		if(floodgateEnabled == null) {
			floodgateEnabled = ProxyChat.getInstance().getProxy().getPluginManager()
					.getPlugin("floodgate").isPresent();
		}

		if(!FloodgateUtil.isBedrockPlayer(player)) {
			return Optional.empty();
		}

		FloodgatePlayer floodgatePlayer = FloodgateAPI.getPlayer(player.getUniqueId());

		if(floodgatePlayer == null) {
			return Optional.empty();
		}

		return Optional.of(floodgatePlayer);
	}

	public String getPlatformName(ProxyChatAccount player) {
		Optional<FloodgatePlayer> floodgatePlayer = getFloodgatePlayer(player);

		if(floodgatePlayer.isPresent()) {
			switch(floodgatePlayer.get().getDeviceOS()) {
				case DEDICATED:
				case UNKNOWN:
					return "Bedrock Edition";

				default:
					return "Bedrock Edition on " + floodgatePlayer.get().getDeviceOS().toString();
			}
		}

		return null;
	}

	public String getPlatformIcon(ProxyChatAccount player) {
		Optional<FloodgatePlayer> floodgatePlayer = getFloodgatePlayer(player);

		if(floodgatePlayer.isPresent()) {
			switch(floodgatePlayer.get().getDeviceOS()) {
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

		return null;
	}

	public static String getPlatformVersion(ProxyChatAccount player) {
		return getFloodgatePlayer(player).map(FloodgatePlayer::getVersion).orElse(null);
	}
}
