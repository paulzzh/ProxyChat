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

import com.velocitypowered.api.plugin.PluginManager;
import com.velocitypowered.api.proxy.ProxyServer;
import uk.co.notnull.ProxyChat.ProxyChat;
import uk.co.notnull.ProxyChat.account.Account;
import uk.co.notnull.ProxyChat.api.account.ProxyChatAccount;
import uk.co.notnull.ProxyChat.api.account.ConsoleAccount;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import uk.co.notnull.platformdetection.Platform;
import uk.co.notnull.platformdetection.PlatformDetectionVelocity;

import java.util.Optional;

@UtilityClass
public class PlatformUtil {
	private static boolean initialised = false;
	private static PlatformDetectionVelocity platformDetection = null;

	private static void getPlatformAPI() {
		if(initialised) {
			return;
		}

		initialised = true;
		PluginManager pluginManager = ProxyChat.getInstance().getProxy().getPluginManager();

		if(pluginManager.isLoaded("platform-detection")) {
			platformDetection = (PlatformDetectionVelocity) pluginManager.getPlugin("platform-detection").get()
					.getInstance().get();
		}
	}

	public Optional<Platform> getPlatform(ProxyChatAccount player) {
		getPlatformAPI();
		return Optional.ofNullable(platformDetection != null ? platformDetection.getPlatform(player.getUniqueId()) : null);
	}

	public String getPlatformIcon(ProxyChatAccount player) {
		return getPlatform(player).map(Platform::getIcon).orElse("");
	}

	public static String getPlatformName(ProxyChatAccount player) {
		return getPlatform(player).map(Platform::getLabel).orElse("");
	}

	public static String getPlatformVersion(ProxyChatAccount player) {
		getPlatformAPI();

		if(player instanceof ConsoleAccount) {
			return ProxyChat.getInstance().getProxy().getVersion().getVersion();
		}

		if(platformDetection == null) {
			return "";
		}

		return platformDetection.getPlatformVersion(player.getUniqueId());
	}

	public static TextComponent getHover(ProxyChatAccount player) {
		TextComponent.Builder result = Component.text().content(player.getDisplayName() + " is using:\n");

		Optional<Platform> platform = getPlatform(player);
		String version = getPlatformVersion(player);

		String icon = platform.map(Platform::getIcon).orElse("");
		String name = platform.map(Platform::getLabel).orElse("");

		result.append(Component.text(icon))
				.append(Component.text(" " + name + "\n", NamedTextColor.YELLOW))
				.append(Component.text(version, NamedTextColor.GRAY));

		return result.build();
	}
}
