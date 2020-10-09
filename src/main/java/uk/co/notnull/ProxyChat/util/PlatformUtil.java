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
import uk.co.notnull.ProxyChat.account.Account;
import uk.co.notnull.ProxyChat.api.account.ProxyChatAccount;
import uk.co.notnull.ProxyChat.api.account.ConsoleAccount;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

@UtilityClass
public class PlatformUtil {
	public String getPlatformIcon(ProxyChatAccount player) {
		String icon = null;

		if(FloodgateUtil.isBedrockPlayer(player)) {
			icon = FloodgateUtil.getPlatformIcon(player);
		}

		if(icon == null && VivecraftUtil.isVivecraftPlayer(player)) {
			icon = VivecraftUtil.getPlatformIcon(player);
		}

		return icon != null ? icon : String.valueOf('\uE1DD');
	}

	public static String getPlatformName(ProxyChatAccount player) {
		if(player instanceof ConsoleAccount) {
			return ProxyChat.getInstance().getProxy().getVersion().getName();
		}

		String name = null;

		if(FloodgateUtil.isBedrockPlayer(player)) {
			name = FloodgateUtil.getPlatformName(player);
		}

		if(name == null && VivecraftUtil.isVivecraftPlayer(player)) {
			name = VivecraftUtil.getPlatformName(player);
		}

		return name != null ? name : "Java Edition";
	}

	public static String getPlatformVersion(ProxyChatAccount player) {
		if(player instanceof ConsoleAccount) {
			return ProxyChat.getInstance().getProxy().getVersion().getVersion();
		}

		String name = null;

		if(FloodgateUtil.isBedrockPlayer(player)) {
			name = FloodgateUtil.getPlatformVersion(player);
		}

		if(name == null && VivecraftUtil.isVivecraftPlayer(player)) {
			name = VivecraftUtil.getPlatformVersion(player);
		}

		return name != null ? name : ((Account) player).getPlayer().getProtocolVersion().getName();
	}

	public static TextComponent getHover(ProxyChatAccount player) {
		TextComponent.Builder result = Component.text().content(player.getDisplayName() + " is using:\n");

		String icon = getPlatformIcon(player);
		String platform = getPlatformName(player);
		String version = getPlatformVersion(player);

		result.append(Component.text(icon))
				.append(Component.text(" " + platform + "\n", NamedTextColor.YELLOW))
				.append(Component.text(version, NamedTextColor.GRAY));

		return result.build();
	}
}
