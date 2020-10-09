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

package uk.co.notnull.ProxyChat.api.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import uk.co.notnull.ProxyChat.api.utils.RegexUtil;
import java.util.regex.Pattern;
import org.junit.Test;

public class RegexUtilTest {
  @Test
  public void leetSpeakPatterns() {
    for (RegexUtil.LeetSpeakPattern leet : RegexUtil.LEET_PATTERNS.values()) {
      String letter = leet.getLetter();
      Pattern pattern = Pattern.compile(leet.getPattern());

      for (String replacement : leet.getLeetAlternatives()) {
        assertTrue(
            "Replacement \""
                + replacement
                + "\" doesn't match as a alternative for "
                + letter
                + "!",
            pattern.matcher(replacement).matches());
      }
    }
  }

  @Test
  public void leetSpeakWildcard() {
    for (RegexUtil.LeetSpeakPattern leet : RegexUtil.LEET_PATTERNS.values()) {
      String letter = leet.getLetter();
      Pattern pattern =
          RegexUtil.parseWildcardToPattern(
              leet.getLetter(), Pattern.CASE_INSENSITIVE, false, true, false, false);

      for (String replacement : leet.getLeetAlternatives()) {
        assertTrue(
            "Replacement \""
                + replacement
                + "\" doesn't match as a alternative for "
                + letter
                + "!",
            pattern.matcher(replacement).matches());
      }
    }
  }

  @Test
  public void specialChars() {
    final String testString = "dhäöüßÄÖÜàÎ学校hg";
    Pattern pattern;

    // Iterate over all possible flag combinations
    for (int i = 0; i < (1 << 4); ++i) {
      pattern =
          RegexUtil.parseWildcardToPattern(
              testString,
              Pattern.CASE_INSENSITIVE,
              (i & (1 << 0)) > 0,
              (i & (1 << 1)) > 0,
              (i & (1 << 2)) > 0,
              (i & (1 << 3)) > 0);

      assertTrue("Pattern doesn't match itself!", pattern.matcher(testString).matches());
    }
  }

  @Test
  public void wildCards() {
    final String test1 = "xxx_";
    final String test2 = "_xxx";
    final String test3 = "_xxx_";
    final String test4 = "xxx__";
    final String test5 = "__xxx";
    final String test6 = "__xxx__";

    for (int i = 0; i < 2; ++i) {
      final boolean leetSpeak = i > 0;

      final Pattern glob1 =
          RegexUtil.parseWildcardToPattern(
              "xxx*", Pattern.CASE_INSENSITIVE, false, leetSpeak, false, false);
      final Pattern glob2 =
          RegexUtil.parseWildcardToPattern(
              "*xxx", Pattern.CASE_INSENSITIVE, false, leetSpeak, false, false);
      final Pattern glob3 =
          RegexUtil.parseWildcardToPattern(
              "*xxx*", Pattern.CASE_INSENSITIVE, false, leetSpeak, false, false);
      final Pattern single1 =
          RegexUtil.parseWildcardToPattern(
              "xxx?", Pattern.CASE_INSENSITIVE, false, leetSpeak, false, false);
      final Pattern single2 =
          RegexUtil.parseWildcardToPattern(
              "?xxx", Pattern.CASE_INSENSITIVE, false, leetSpeak, false, false);
      final Pattern single3 =
          RegexUtil.parseWildcardToPattern(
              "?xxx?", Pattern.CASE_INSENSITIVE, false, leetSpeak, false, false);

      assertTrue(glob1.matcher(test1).matches());
      assertFalse(glob1.matcher(test2).matches());
      assertFalse(glob1.matcher(test3).matches());
      assertTrue(glob1.matcher(test4).matches());
      assertFalse(glob1.matcher(test5).matches());
      assertFalse(glob1.matcher(test6).matches());

      assertFalse(glob2.matcher(test1).matches());
      assertTrue(glob2.matcher(test2).matches());
      assertFalse(glob2.matcher(test3).matches());
      assertFalse(glob2.matcher(test4).matches());
      assertTrue(glob2.matcher(test5).matches());
      assertFalse(glob2.matcher(test6).matches());

      assertTrue(glob3.matcher(test1).matches());
      assertTrue(glob3.matcher(test2).matches());
      assertTrue(glob3.matcher(test3).matches());
      assertTrue(glob3.matcher(test4).matches());
      assertTrue(glob3.matcher(test5).matches());
      assertTrue(glob3.matcher(test6).matches());

      assertTrue(single1.matcher(test1).matches());
      assertFalse(single1.matcher(test2).matches());
      assertFalse(single1.matcher(test3).matches());
      assertFalse(single1.matcher(test4).matches());
      assertFalse(single1.matcher(test5).matches());
      assertFalse(single1.matcher(test6).matches());

      assertFalse(single2.matcher(test1).matches());
      assertTrue(single2.matcher(test2).matches());
      assertFalse(single2.matcher(test3).matches());
      assertFalse(single2.matcher(test4).matches());
      assertFalse(single2.matcher(test5).matches());
      assertFalse(single2.matcher(test6).matches());

      assertTrue(single3.matcher(test1).matches());
      assertTrue(single3.matcher(test2).matches());
      assertTrue(single3.matcher(test3).matches());
      assertFalse(single3.matcher(test4).matches());
      assertFalse(single3.matcher(test5).matches());
      assertFalse(single3.matcher(test6).matches());
    }
  }

  @Test
  public void punctuation() {
    final Pattern pattern = RegexUtil.parseWildcardToPattern("xxx");

    assertTrue(pattern.matcher("xxx").find());
    assertTrue(pattern.matcher("xxx ").find());
    assertTrue(pattern.matcher("xxx!").find());
    assertTrue(pattern.matcher(" xxx").find());
    assertTrue(pattern.matcher("!xxx").find());
    assertTrue(pattern.matcher(" xxx ").find());
    assertTrue(pattern.matcher(" xxx!").find());
    assertTrue(pattern.matcher("!xxx ").find());
    assertTrue(pattern.matcher("!xxx!").find());

    assertFalse(pattern.matcher("xx").find());
    assertFalse(pattern.matcher("xxxZ").find());
    assertFalse(pattern.matcher("Zxxx").find());
  }

  // TODO: More tests!
}
