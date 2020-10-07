package dev.aura.bungeechat.api.filter;

import dev.aura.bungeechat.api.account.BungeeChatAccount;

public interface BungeeChatPreParseFilter extends BungeeChatFilter<String> {
  String applyFilter(BungeeChatAccount sender, String message) throws BlockMessageException;
}
