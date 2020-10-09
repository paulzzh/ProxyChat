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

package uk.co.notnull.ProxyChat.module;

import uk.co.notnull.ProxyChat.api.placeholder.ProxyChatContext;
import uk.co.notnull.ProxyChat.api.placeholder.ComponentReplacementSupplier;
import uk.co.notnull.ProxyChat.api.placeholder.PlaceHolder;
import uk.co.notnull.ProxyChat.api.placeholder.PlaceHolderManager;
import uk.co.notnull.ProxyChat.util.PlatformUtil;
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
							ProxyChatContext.HAS_SENDER));

		PlaceHolderManager.registerPlaceholder(
			new PlaceHolder("target_platform",
							context -> PlatformUtil.getPlatformIcon(context.getTarget().get()),
							(ComponentReplacementSupplier) context -> {
								return Component.text(PlatformUtil.getPlatformIcon(context.getTarget().get()))
										.hoverEvent(PlatformUtil.getHover(context.getTarget().get()));
							},
							ProxyChatContext.HAS_TARGET));
	}

	@Override
	public void onDisable() {

	}
}
