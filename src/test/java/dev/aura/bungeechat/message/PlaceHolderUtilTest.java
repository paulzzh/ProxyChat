package dev.aura.bungeechat.message;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PlaceHolderUtilTest {
  @Test
  public void transformAltColorCodesTest() {
    final String originalColors = "&0&1&2&3&4&5&6&7&8&9&a&b&c&d&e&f&k&l&m&n&o&r";
    final String expectedResult = originalColors.replace('&', '\u00A7');

    assertEquals(
        "Color code transformation is invalid!",
        expectedResult,
        PlaceHolderUtil.transformAltColorCodes(originalColors));
  }
}
