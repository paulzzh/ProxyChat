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

import com.techjar.vbe.VivecraftAPI;
import uk.co.notnull.ProxyChat.ProxyChat;
import uk.co.notnull.ProxyChat.api.account.ProxyChatAccount;
import lombok.experimental.UtilityClass;

@UtilityClass
public class VivecraftUtil {
	private Boolean vivecraftEnabled = null;

	public boolean isVivecraftPlayer(ProxyChatAccount player) {
		if(vivecraftEnabled == null) {
			vivecraftEnabled = ProxyChat.getInstance().getProxy().getPluginManager()
					.getPlugin("vivecraft-velocity-extensions").isPresent();
		}

		return vivecraftEnabled  && VivecraftAPI.isVive(player.getUniqueId());
	}

	public boolean isVR(ProxyChatAccount player) {
		if(vivecraftEnabled == null) {
			vivecraftEnabled = ProxyChat.getInstance().getProxy().getPluginManager()
					.getPlugin("vivecraft-velocity-extensions").isPresent();
		}

		return vivecraftEnabled && VivecraftAPI.isVR(player.getUniqueId());
	}

	public String getPlatformIcon(ProxyChatAccount player) {
		if(vivecraftEnabled == null) {
			vivecraftEnabled = ProxyChat.getInstance().getProxy().getPluginManager()
					.getPlugin("vivecraft-velocity-extensions").isPresent();
		}

		if(!isVivecraftPlayer(player)) {
			return null;
		}

		return isVR(player) ? String.valueOf('\uE1DC') : String.valueOf('\uE1DE');
	}

	public static String getPlatformName(ProxyChatAccount player) {
		if(vivecraftEnabled == null) {
			vivecraftEnabled = ProxyChat.getInstance().getProxy().getPluginManager()
					.getPlugin("vivecraft-velocity-extensions").isPresent();
		}

		if(!isVivecraftPlayer(player)) {
			return null;
		}

		return isVR(player) ? "Vivecraft - VR" : "Vivecraft - No VR";
	}

	public static String getPlatformVersion(ProxyChatAccount player) {
		return "";
	}
}
