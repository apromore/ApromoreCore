/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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
package org.apromore.portal.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class StringUtilTest {

  @Test
  void testGetFileName_URL() {

    String result = StringUtil.getFileName(
        "https://www.dropbox.com/s/02ee18ybdm8xhog/APurchasingExample.zip?dl=0",
        "attachment; filename=\"APurchasingExample.zip\"; filename*=UTF-8''APurchasingExample.zip");
    assertEquals("APurchasingExample.zip", result);
  }

  @Test
  void testGetFileName_URL_INVALID_CHARS() {

    String result = StringUtil.getFileName(
        "https://www.dropbox.com/s/02ee18ybdm8xhog/APurchasingExample.zip?dl=0",
        "attachment; filename=\"APurchasing/Example:.zip\"; filename*=UTF-8''APurchasingExample.zip");
    assertEquals("APurchasing_Example_.zip", result);
  }

  @Test
  void testGetFileName_pathTraversal1() {

    String result = StringUtil.getFileName(
        "https://www.dropbox.com/s/02ee18ybdm8xhog/APurchasingExample.zip?dl=0",
        "attachment; filename=\"./etc/passwd\"; filename*=UTF-8''APurchasingExample.zip");
    assertEquals("._etc_passwd", result);
  }

  @Test
  void testGetFileName_URL_pathTraversal2() {

    String result = StringUtil.getFileName(
        "https://www.dropbox.com/s/02ee18ybdm8xhog/APurchasingExample.zip?dl=0",
        "attachment; filename=\"../../../etc/passwd\"; filename*=UTF-8''APurchasingExample.zip");
    assertEquals(".._.._.._etc_passwd", result);
  }

  @Test
  void testGetFileName_URL_pathTraversal3() {

    String result = StringUtil.getFileName(
        "https://www.dropbox.com/s/02ee18ybdm8xhog/APurchasingExample.zip?dl=0",
        "attachment; filename=\"../\"; filename*=UTF-8''APurchasingExample.zip");
    assertEquals(".._", result);
  }

  @Test
  void testGetFileName_URL_pathTraversal4() {

    String result = StringUtil.getFileName(
        "https://www.dropbox.com/s/02ee18ybdm8xhog/APurchasingExample.zip?dl=0",
        "attachment; filename=\"/\"; filename*=UTF-8''APurchasingExample.zip");
    assertEquals("_", result);
  }

  @Test
  void testIsValidDropBoxURL() {

    boolean testCase1 = StringUtil.isValidDropBoxURL(
        "https://www.dropbox.com/s/xadcmvtji1ojvwo/PurchasingExample" + ".csv?dl=0");
    boolean testCase2 = StringUtil.isValidDropBoxURL(
        "https://www.dropbox.com.abc.com/s/xadcmvtji1ojvwo/PurchasingExample.csv?dl=0");
    boolean testCase3 = StringUtil
        .isValidDropBoxURL("https://www.dropbox.com./s/xadcmvtji1ojvwo/PurchasingExample.csv?dl=0");


    assertTrue(testCase1);
    assertFalse(testCase2);
    assertFalse(testCase3);
  }

  @Test
  void testIsValidGoogleDriveURL() {

    boolean testCase1 = StringUtil.isValidGoogleDriveURL(
        "https://drive.google.com/file/d/1hQaySFOh06x6oBxTSNZYf1pqOQtm8gik/view?usp=sharing");
    boolean testCase2 = StringUtil.isValidGoogleDriveURL("https://drive.google.com"
        + ".abc.com/file/d/1hQaySFOh06x6oBxTSNZYf1pqOQtm8gik/view?usp=sharing");
    boolean testCase3 = StringUtil.isValidGoogleDriveURL(
        "https://drive.google.com./file/d/1hQaySFOh06x6oBxTSNZYf1pqOQtm8gik/view?usp=sharing");

    assertTrue(testCase1);
    assertFalse(testCase2);
    assertFalse(testCase3);
  }

  @Test
  void testIsValidOneDriveURL() {

    boolean testCase1 = StringUtil.isValidOneDriveURL(
        "https://onedrive.live.com/embed?cid=9AA1F51B7D69569C&resid=9AA1F51B7D69569C%21379&authkey=AA5cjmnDDs2_yOo");
    boolean testCase2 = StringUtil.isValidOneDriveURL(
        "https://onedrive.live.com.abc.com/embed?cid=9AA1F51B7D69569C&resid=9AA1F51B7D69569C%21379&authkey=AA5cjmnDDs2_yOo");
    boolean testCase3 = StringUtil.isValidOneDriveURL("https://onedrive.live.com"
        + "./embed?cid=9AA1F51B7D69569C&resid=9AA1F51B7D69569C%21379&authkey=AA5cjmnDDs2_yOo");

    assertTrue(testCase1);
    assertFalse(testCase2);
    assertFalse(testCase3);
  }

  @Test
  @Disabled
  // Wrong test
  void testValidateFileUrl_dropBox() {

    String validFileUrl = StringUtil
        .parseFileURL("https://www.dropbox.com/s/xadcmvtji1ojvwo/PurchasingExample" + ".csv?dl=0");
    String maliciousFileUrl = StringUtil.parseFileURL(
        "https://www.dropbox.com.abc.com/s/xadcmvtji1ojvwo/PurchasingExample.csv?dl=0");
    String maliciousFileUrl1 = StringUtil
        .parseFileURL("https://www.dropbox.com" + "./s/xadcmvtji1ojvwo/PurchasingExample.csv?dl=0");

    assertEquals("https://www.dropbox.com/s/xadcmvtji1ojvwo/PurchasingExample.csv?dl=1",
        validFileUrl);
    assertEquals("", maliciousFileUrl);
    assertEquals("", maliciousFileUrl1);
  }

  @Test
  @Disabled
  // Wrong test
  void testValidateFileUrl_googleDrive() {

    String validFileUrl = StringUtil.parseFileURL(
        "https://drive.google" + ".com/file/d/1hQaySFOh06x6oBxTSNZYf1pqOQtm8gik/view?usp=sharing");
    String maliciousFileUrl = StringUtil.parseFileURL(
        "https://drive.google.com.abc.com/file/d/1hQaySFOh06x6oBxTSNZYf1pqOQtm8gik/view?usp=sharing");
    String maliciousFileUrl1 = StringUtil.parseFileURL(
        "https://drive.google.com./file/d/1hQaySFOh06x6oBxTSNZYf1pqOQtm8gik/view?usp=sharing");

    assertEquals(
        "https://drive.google.com/uc?export=download&id=1hQaySFOh06x6oBxTSNZYf1pqOQtm8gik",
        validFileUrl);
    assertEquals("", maliciousFileUrl);
    assertEquals("", maliciousFileUrl1);
  }

  @Test
  @Disabled
  // Wrong test
  void testValidateFileUrl_oneDrive() {

    String validFileUrl =
        StringUtil.parseFileURL("https://onedrive.live.com/embed?cid=9AA1F51B7D69569C&resid"
            + "=9AA1F51B7D69569C%21379&authkey=AA5cjmnDDs2_yOo");
    String maliciousFileUrl = StringUtil.parseFileURL(
        "https://onedrive.live.com.abc.com/embed?cid=9AA1F51B7D69569C&resid=9AA1F51B7D69569C%21379&authkey=AA5cjmnDDs2_yOo");
    String maliciousFileUrl1 = StringUtil.parseFileURL(
        "https://onedrive.live.com./embed?cid=9AA1F51B7D69569C&resid=9AA1F51B7D69569C%21379&authkey=AA5cjmnDDs2_yOo");

    assertEquals(
        "https://onedrive.live.com/download?cid=9AA1F51B7D69569C&resid=9AA1F51B7D69569C%21379"
            + "&authkey=AA5cjmnDDs2_yOo",
        validFileUrl);
    assertEquals("", maliciousFileUrl);
    assertEquals("", maliciousFileUrl1);
  }

}
