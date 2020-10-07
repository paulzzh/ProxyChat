package dev.aura.bungeechat.api.filter;

import dev.aura.bungeechat.api.account.BungeeChatAccount;
import net.kyori.adventure.text.Component;

public interface BungeeChatPostParseFilter extends BungeeChatFilter<Component> {
	Component applyFilter(BungeeChatAccount sender, Component message) throws BlockMessageException;
}
