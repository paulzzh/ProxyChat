package dev.aura.bungeechat.util;

import dev.aura.bungeechat.BungeeChat;
import dev.aura.bungeechat.account.Account;
import dev.aura.bungeechat.api.account.BungeeChatAccount;
import dev.aura.bungeechat.api.account.ConsoleAccount;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEventSource;
import net.kyori.adventure.text.format.NamedTextColor;

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

	public static String getPlatformName(BungeeChatAccount player) {
		if(player instanceof ConsoleAccount) {
			return BungeeChat.getInstance().getProxy().getVersion().getName();
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

	public static String getPlatformVersion(BungeeChatAccount player) {
		if(player instanceof ConsoleAccount) {
			return BungeeChat.getInstance().getProxy().getVersion().getVersion();
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

	public static TextComponent getHover(BungeeChatAccount player) {
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
