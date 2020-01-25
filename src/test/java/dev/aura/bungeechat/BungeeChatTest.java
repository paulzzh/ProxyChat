package dev.aura.bungeechat;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class BungeeChatTest {
  private static BungeeChat bungeeChat;

  @BeforeClass
  public static void initBungeeChat() {
    TestHelper.initBungeeChat();

    bungeeChat = BungeeChat.getInstance();
  }

  @AfterClass
  public static void deinitBungeeChat() throws IOException {
    TestHelper.deinitBungeeChat();
  }
}
