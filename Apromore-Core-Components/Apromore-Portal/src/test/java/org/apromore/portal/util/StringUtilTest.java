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
package org.apromore.portal.util;

import org.junit.Assert;
import org.junit.Test;

public class StringUtilTest {

    @Test
    public void testGetFileName_URL() {

        String result = StringUtil.getFileName("https://www.dropbox.com/s/02ee18ybdm8xhog/APurchasingExample.zip?dl=0",
                "attachment; filename=\"APurchasingExample.zip\"; filename*=UTF-8''APurchasingExample.zip");
        Assert.assertEquals(
                "APurchasingExample.zip",
                result);
    }

    @Test
    public void testGetFileName_URL_INVALID_CHARS() {

        String result = StringUtil.getFileName("https://www.dropbox.com/s/02ee18ybdm8xhog/APurchasingExample.zip?dl=0",
                "attachment; filename=\"APurchasing/Example:.zip\"; filename*=UTF-8''APurchasingExample.zip");
        Assert.assertEquals(
                "APurchasing_Example_.zip",
                result);
    }

    @Test
    public void testGetFileName_pathTraversal1() {

        String result = StringUtil.getFileName("https://www.dropbox.com/s/02ee18ybdm8xhog/APurchasingExample.zip?dl=0",
                "attachment; filename=\"./etc/passwd\"; filename*=UTF-8''APurchasingExample.zip");
        Assert.assertEquals(
                "._etc_passwd",
                result);
    }

    @Test
    public void testGetFileName_URL_pathTraversal2() {

        String result = StringUtil.getFileName("https://www.dropbox.com/s/02ee18ybdm8xhog/APurchasingExample.zip?dl=0",
                "attachment; filename=\"../../../etc/passwd\"; filename*=UTF-8''APurchasingExample.zip");
        Assert.assertEquals(
                ".._.._.._etc_passwd",
                result);
    }

    @Test
    public void testGetFileName_URL_pathTraversal3() {

        String result = StringUtil.getFileName("https://www.dropbox.com/s/02ee18ybdm8xhog/APurchasingExample.zip?dl=0",
                "attachment; filename=\"../\"; filename*=UTF-8''APurchasingExample.zip");
        Assert.assertEquals(
                ".._",
                result);
    }

    @Test
    public void testGetFileName_URL_pathTraversal4() {

        String result = StringUtil.getFileName("https://www.dropbox.com/s/02ee18ybdm8xhog/APurchasingExample.zip?dl=0",
                "attachment; filename=\"/\"; filename*=UTF-8''APurchasingExample.zip");
        Assert.assertEquals(
                "_",
                result);
    }

    @Test
    public void testIsValidDropBoxURL() {

        boolean testCase1 = StringUtil.isValidDropBoxURL("https://www.dropbox.com/s/xadcmvtji1ojvwo/PurchasingExample" +
                ".csv?dl=0");
        boolean testCase2 = StringUtil.isValidDropBoxURL("https://www.dropbox.com.abc.com/s/xadcmvtji1ojvwo/PurchasingExample.csv?dl=0");
        boolean testCase3 = StringUtil.isValidDropBoxURL("https://www.dropbox.com./s/xadcmvtji1ojvwo/PurchasingExample.csv?dl=0");


        Assert.assertTrue(testCase1);
        Assert.assertFalse(testCase2);
        Assert.assertFalse(testCase3);
    }

    @Test
    public void testIsValidGoogleDriveURL() {

        boolean testCase1 = StringUtil.isValidGoogleDriveURL("https://drive.google.com/file/d/1hQaySFOh06x6oBxTSNZYf1pqOQtm8gik/view?usp=sharing");
        boolean testCase2 = StringUtil.isValidGoogleDriveURL("https://drive.google.com" +
                ".abc.com/file/d/1hQaySFOh06x6oBxTSNZYf1pqOQtm8gik/view?usp=sharing");
        boolean testCase3 = StringUtil.isValidGoogleDriveURL("https://drive.google.com./file/d/1hQaySFOh06x6oBxTSNZYf1pqOQtm8gik/view?usp=sharing");

        Assert.assertTrue(testCase1);
        Assert.assertFalse(testCase2);
        Assert.assertFalse(testCase3);
    }

    @Test
    public void testIsValidOneDriveURL() {

        boolean testCase1 = StringUtil.isValidOneDriveURL("https://onedrive.live.com/embed?cid=9AA1F51B7D69569C&resid=9AA1F51B7D69569C%21379&authkey=AA5cjmnDDs2_yOo");
        boolean testCase2 = StringUtil.isValidOneDriveURL("https://onedrive.live.com.abc.com/embed?cid=9AA1F51B7D69569C&resid=9AA1F51B7D69569C%21379&authkey=AA5cjmnDDs2_yOo");
        boolean testCase3 = StringUtil.isValidOneDriveURL("https://onedrive.live.com" +
                "./embed?cid=9AA1F51B7D69569C&resid=9AA1F51B7D69569C%21379&authkey=AA5cjmnDDs2_yOo");

        Assert.assertTrue(testCase1);
        Assert.assertFalse(testCase2);
        Assert.assertFalse(testCase3);
    }

    @Test
    public void testValidateFileUrl_dropBox() {

        String validFileUrl = StringUtil.validateFileURL("https://www.dropbox.com/s/xadcmvtji1ojvwo/PurchasingExample" +
                ".csv?dl=0");
        String maliciousFileUrl = StringUtil.validateFileURL("https://www.dropbox.com.abc.com/s/xadcmvtji1ojvwo/PurchasingExample.csv?dl=0");
        String maliciousFileUrl1 = StringUtil.validateFileURL("https://www.dropbox.com" +
                "./s/xadcmvtji1ojvwo/PurchasingExample.csv?dl=0");

        Assert.assertEquals("https://www.dropbox.com/s/xadcmvtji1ojvwo/PurchasingExample.csv?dl=1", validFileUrl);
        Assert.assertEquals("", maliciousFileUrl);
        Assert.assertEquals("", maliciousFileUrl1);
    }

    @Test
    public void testValidateFileUrl_googleDrive() {

        String validFileUrl = StringUtil.validateFileURL("https://drive.google" +
                ".com/file/d/1hQaySFOh06x6oBxTSNZYf1pqOQtm8gik/view?usp=sharing");
        String maliciousFileUrl = StringUtil.validateFileURL("https://drive.google.com.abc.com/file/d/1hQaySFOh06x6oBxTSNZYf1pqOQtm8gik/view?usp=sharing");
        String maliciousFileUrl1 = StringUtil.validateFileURL("https://drive.google.com./file/d/1hQaySFOh06x6oBxTSNZYf1pqOQtm8gik/view?usp=sharing");

        Assert.assertEquals("https://drive.google.com/uc?export=download&id=1hQaySFOh06x6oBxTSNZYf1pqOQtm8gik",
                validFileUrl);
        Assert.assertEquals("", maliciousFileUrl);
        Assert.assertEquals("", maliciousFileUrl1);
    }

    @Test
    public void testValidateFileUrl_oneDrive() {

        String validFileUrl = StringUtil.validateFileURL("https://onedrive.live.com/embed?cid=9AA1F51B7D69569C&resid" +
                "=9AA1F51B7D69569C%21379&authkey=AA5cjmnDDs2_yOo");
        String maliciousFileUrl = StringUtil.validateFileURL("https://onedrive.live.com.abc.com/embed?cid=9AA1F51B7D69569C&resid=9AA1F51B7D69569C%21379&authkey=AA5cjmnDDs2_yOo");
        String maliciousFileUrl1 = StringUtil.validateFileURL("https://onedrive.live.com./embed?cid=9AA1F51B7D69569C&resid=9AA1F51B7D69569C%21379&authkey=AA5cjmnDDs2_yOo");

        Assert.assertEquals("https://onedrive.live.com/download?cid=9AA1F51B7D69569C&resid=9AA1F51B7D69569C%21379" +
                "&authkey=AA5cjmnDDs2_yOo", validFileUrl);
        Assert.assertEquals("", maliciousFileUrl);
        Assert.assertEquals("", maliciousFileUrl1);
    }

}
