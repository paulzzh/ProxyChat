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

package uk.co.notnull.ProxyChat.hook.metrics;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import uk.co.notnull.ProxyChat.ProxyChat;
import java.io.File;
import org.junit.Test;

public class LanguageDataTest {
  @Test
  public void verifyBuiltinLanguages() {
    final File translationFolder =
        new File(ProxyChat.class.getResource("/assets/" + ProxyChat.ID + "/lang").getPath());

    assertTrue(translationFolder.isDirectory());

    String[] translationFiles = translationFolder.list();

    assertNotNull(translationFiles); // Are you happy now SpotBugs?

    for (String translationFile : translationFiles) {
      final String translationName = translationFile.replace(".lang", "");

      assertTrue("language: " + translationName, LanguageData.isValidLangauge(translationName));
    }
  }

  @Test
  public void randomTranslationsTest() {
    for (String lang :
        new String[] {
          "xx", "xx_XX", "en_XX", "xx_US", "xx_XX_XX", "xx_XX_XX_xx_xx_xx", "gfjdhgkdnhjfgjnj", ""
        }) {
      assertFalse("language: " + lang, LanguageData.isValidLangauge(lang));
    }
  }
}
