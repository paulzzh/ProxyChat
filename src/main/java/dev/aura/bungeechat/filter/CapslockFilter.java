package dev.aura.bungeechat.filter;

import com.google.common.annotations.VisibleForTesting;
import dev.aura.bungeechat.api.account.BungeeChatAccount;
import dev.aura.bungeechat.api.filter.BlockMessageException;
import dev.aura.bungeechat.api.filter.BungeeChatFilter;
import dev.aura.bungeechat.api.filter.FilterManager;
import dev.aura.bungeechat.message.Messages;
import dev.aura.bungeechat.permission.Permission;
import dev.aura.bungeechat.permission.PermissionManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;

public class CapslockFilter implements BungeeChatFilter {
  private final int minimumLetterCount;
  private final int maximumCapsPercentage;
  private final boolean noPermissions;
  private static final PlainComponentSerializer serializer = PlainComponentSerializer.plain();

  public CapslockFilter(int minimumLetterCount, int maximumCapsPercentage) {
    this(minimumLetterCount, maximumCapsPercentage, false);
  }

  @VisibleForTesting
  CapslockFilter(int minimumLetterCount, int maximumCapsPercentage, boolean noPermissions) {
    this.minimumLetterCount = minimumLetterCount;
    this.maximumCapsPercentage = maximumCapsPercentage;
    this.noPermissions = noPermissions;
  }

  @Override
  public Component applyFilter(BungeeChatAccount sender, Component message) throws BlockMessageException {
    if (!noPermissions && PermissionManager.hasPermission(sender, Permission.BYPASS_ANTI_CAPSLOCK))
      return message;

    int uppercase = 0;
    int lowercase = 0;

    String text = serializer.serialize(message);

    for (char c : text.toCharArray()) {
      if (Character.isUpperCase(c)) {
        uppercase++;
      } else if (Character.isLowerCase(c)) {
        lowercase++;
      }
    }

    int total = uppercase + lowercase;

    if (total < minimumLetterCount) return message;

    if (((uppercase * 100) / total) > maximumCapsPercentage)
      throw new ExtendedBlockMessageException(Messages.ANTI_CAPSLOCK, sender);

    return message;
  }

  @Override
  public int getPriority() {
    return FilterManager.CAPSLOCK_FILTER_PRIORITY;
  }
}
