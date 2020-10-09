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

import static org.junit.Assert.assertEquals;

import uk.co.notnull.ProxyChat.api.utils.TimeUtil;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Supplier;
import org.junit.Test;

public class TimeUtilTest {
  private static double DELTA = 0.0;

  private static void assertCorrectTimeFormat(String timeFormat, Supplier<String> testMethod) {
    SimpleDateFormat sdfDate = new SimpleDateFormat(timeFormat);
    Date now = new Date();

    assertEquals("Expected time format to match", sdfDate.format(now), testMethod.get());
  }

  @Test
  public void convertStringTimeToDoubleTest() {
    assertEquals(
        "Converting years to milliseconds yielded the wrong result.",
        331128000000.0,
        TimeUtil.convertStringTimeToDouble("10.5y"),
        DELTA);
    assertEquals(
        "Converting months to milliseconds yielded the wrong result.",
        27216000000.0,
        TimeUtil.convertStringTimeToDouble("10.5mo"),
        DELTA);
    assertEquals(
        "Converting weeks to milliseconds yielded the wrong result.",
        6350400000.0,
        TimeUtil.convertStringTimeToDouble("10.5w"),
        DELTA);
    assertEquals(
        "Converting days to milliseconds yielded the wrong result.",
        907200000.0,
        TimeUtil.convertStringTimeToDouble("10.5d"),
        DELTA);
    assertEquals(
        "Converting hours to milliseconds yielded the wrong result.",
        37800000.0,
        TimeUtil.convertStringTimeToDouble("10.5h"),
        DELTA);
    assertEquals(
        "Converting minutes to milliseconds yielded the wrong result.",
        630000.0,
        TimeUtil.convertStringTimeToDouble("10.5m"),
        DELTA);
    assertEquals(
        "Converting seconds to milliseconds yielded the wrong result.",
        10500.0,
        TimeUtil.convertStringTimeToDouble("10.5s"),
        DELTA);
    assertEquals(
        "Converting milliseconds to milliseconds yielded the wrong result.",
        10.5,
        TimeUtil.convertStringTimeToDouble("10.5"),
        DELTA);
  }

  @Test(expected = NumberFormatException.class)
  public void convertStringTimeToDoubleExceptionTest1() {
    TimeUtil.convertStringTimeToDouble("10ms");
  }

  @Test(expected = NumberFormatException.class)
  public void convertStringTimeToDoubleExceptionTest2() {
    TimeUtil.convertStringTimeToDouble("10xz");
  }

  @Test
  public void getDateTest() {
    assertCorrectTimeFormat("yyyy/MM/dd", TimeUtil::getDate);
  }

  @Test
  public void getDayTest() {
    assertCorrectTimeFormat("dd", TimeUtil::getDay);
  }

  @Test
  public void getLongTimeStampTest() {
    assertCorrectTimeFormat("yyyy/MM/dd HH:mm:ss", TimeUtil::getLongTimeStamp);
  }

  @Test
  public void getMonthTest() {
    assertCorrectTimeFormat("MM", TimeUtil::getMonth);
  }

  @Test
  public void getShortTimeStampTest() {
    assertCorrectTimeFormat("HH:mm", TimeUtil::getShortTimeStamp);
  }

  @Test
  public void getTimeStampTest() {
    assertCorrectTimeFormat("HH:mm:ss", TimeUtil::getTimeStamp);
  }

  @Test
  public void getYearTest() {
    assertCorrectTimeFormat("yyyy", TimeUtil::getYear);
  }
}
