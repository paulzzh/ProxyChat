package dev.aura.bungeechat.filter;

import dev.aura.bungeechat.api.account.BungeeChatAccount;
import dev.aura.bungeechat.api.filter.BungeeChatFilter;
import dev.aura.bungeechat.api.filter.FilterManager;
import dev.aura.bungeechat.permission.Permission;
import dev.aura.bungeechat.permission.PermissionManager;

import java.util.List;
import java.util.regex.MatchResult;
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
	public String applyFilter(BungeeChatAccount sender, String message) {
		if(!noPermissions && PermissionManager.hasPermission(sender, Permission.USE_EMOTES)) {
			return message;
		}

		return emotePattern.matcher(message).replaceAll((MatchResult result) -> {
			int emoteIndex = emotes.indexOf(result.group(2).replace(prefix, "").toLowerCase());

			return emoteIndex > -1 ? result.group(1) + new String(
					Character.toChars(emoteCharacter + emoteIndex)) : result.group();
		});
	}

	@Override
	public int getPriority() {
		return FilterManager.EMOTE_FILTER_PRIORITY;
	}
}
