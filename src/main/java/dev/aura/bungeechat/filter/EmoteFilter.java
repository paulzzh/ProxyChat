package dev.aura.bungeechat.filter;

import dev.aura.bungeechat.api.account.BungeeChatAccount;
import dev.aura.bungeechat.api.filter.BungeeChatFilter;
import dev.aura.bungeechat.api.filter.FilterManager;
import dev.aura.bungeechat.permission.Permission;
import dev.aura.bungeechat.permission.PermissionManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class EmoteFilter implements BungeeChatFilter {
	private final static Pattern emotePattern = Pattern.compile("([^\\\\]|^):(\\w+):");
	private final static char emoteCharacter = '\ue110';
	private final List<String> emotes;
	private final boolean noPermissions;
	private final String prefix;

	public EmoteFilter(List<String> emotes, String prefix) {
		this(emotes, prefix, false);
	}

	public EmoteFilter(List<String> emotes, String prefix, boolean noPermissions) {
		this.prefix = prefix;
		this.emotes = emotes.stream().map(String::toLowerCase).collect(Collectors.toList());
		this.noPermissions = noPermissions;
	}

	@Override
	public Component applyFilter(BungeeChatAccount sender, Component message) {
		if(!noPermissions && PermissionManager.hasPermission(sender, Permission.USE_EMOTES)) {
			return message;
		}

		return message.replaceText(emotePattern, (TextComponent.Builder result) -> {
			String content = result.content();
			String beforeEmote = content.substring(content.indexOf(':') - 1);
			String emote = content.substring(content.indexOf(':'), content.length() - 1)
					.replace(prefix, "").toLowerCase();

			int emoteIndex = emotes.indexOf(emote);

			if(emoteIndex > -1) {
				result.content(beforeEmote + new String(Character.toChars(emoteCharacter + emoteIndex)));
				result.hoverEvent(Component.text(":" + emote + ":"));
				result.insertion(":" + emote + ":");
			}

			return message;
		});
	}

	@Override
	public int getPriority() {
		return FilterManager.EMOTE_FILTER_PRIORITY;
	}
}
