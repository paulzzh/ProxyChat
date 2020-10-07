package dev.aura.bungeechat.filter;

import dev.aura.bungeechat.api.account.BungeeChatAccount;
import dev.aura.bungeechat.api.filter.BungeeChatPostParseFilter;
import dev.aura.bungeechat.api.filter.FilterManager;
import dev.aura.bungeechat.permission.Permission;
import dev.aura.bungeechat.permission.PermissionManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class EmoteFilter implements BungeeChatPostParseFilter {
	private final static Pattern emotePattern = Pattern.compile(":(\\w+):");
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
			String emote = content.substring(1, content.length() - 1).replace(prefix, "").toLowerCase();
			int emoteIndex = emotes.indexOf(emote);
			String emoteChar = new String(Character.toChars(emoteCharacter + emoteIndex));

			if(emoteIndex > -1) {
				result.content(emoteChar);
				result.hoverEvent(Component.text(emoteChar + " " + emote)
										  .append(Component.newline())
										  .append(Component.text("Shift + Click to use",
																 Style.style().color(NamedTextColor.YELLOW).build()
										  )));
				result.insertion(":" + emote + ":");
			}

			return result;
		});
	}

	@Override
	public int getPriority() {
		return FilterManager.EMOTE_FILTER_PRIORITY;
	}
}
