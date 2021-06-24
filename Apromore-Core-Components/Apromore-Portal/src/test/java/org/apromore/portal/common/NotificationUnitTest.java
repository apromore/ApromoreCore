/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.portal.common;

import org.apromore.portal.common.notification.Notification;
import org.apromore.portal.servlet.filter.FilterRegexUtil;
import org.junit.Test;

/* Test suite for {@link Navigation}. */
public class NotificationUnitTest {

  final String CASE1 = "A string with \"double quotes\" in it";
  final String CASE2 = "A string with 'single quotes' in it";
  final String CASE3 = "A string with HTML <strong>snippets</strong> in it";

  // Test cases.

  @Test
  public void test1() {

    String dString = "/dashboard/fonts/icomoon.jpeg";
    boolean isMathed = FilterRegexUtil.isMatchingFilterRegex(dString);
    System.out.println(isMathed);
  }

  /**
   * Test {@link Notification#sanitize} method.
   */
  @Test
  public void testSanitize() throws Exception {
    assertValidMessage(Notification.sanitize(CASE1));
    assertValidMessage(Notification.sanitize(CASE2));
    assertValidMessage(Notification.sanitize(CASE3));
  }

  // Internal methods

  /**
   * Assert if message is valid, all single quotes are properly escaped
   *
   * @param message
   */
  private void assertValidMessage(String message) throws Exception {
    char ch, prevCh = ' ';

    for (int i = 0; i < message.length(); i++) {
      ch = message.charAt(i);
      if (ch == '\'') {
        if (prevCh != '\\') {
          throw new Exception("Invalid message");
        }
      }
      prevCh = ch;
    }
  }
}
