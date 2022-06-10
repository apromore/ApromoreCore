/*-
 * #%L
 * This file is part of "Apromore Enterprise Edition".
 * %%
 * Copyright (C) 2019 - 2022 Apromore Pty Ltd. All Rights Reserved.
 * %%
 * NOTICE:  All information contained herein is, and remains the
 * property of Apromore Pty Ltd and its suppliers, if any.
 * The intellectual and technical concepts contained herein are
 * proprietary to Apromore Pty Ltd and its suppliers and may
 * be covered by U.S. and Foreign Patents, patents in process,
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this
 * material is strictly forbidden unless prior written permission
 * is obtained from Apromore Pty Ltd.
 * #L%
 */
package org.apromore.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringUtilUnitTest {

    @Test
    void testNormalizeFilename() {

        String fileName_1 = "abc*d?.=";
        String fileName_2 = "abc*abcdzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz_______________more_than_60_characters";
        String fileName_3 = "?";
        String fileName_4 = "購買プロセス_2.";
        String fileName_5 = "_تحياتي";
        String fileName_6 = "@#$%^&*";

        assertEquals("abcd_=", StringUtil.normalizeFilename(fileName_1));
        assertEquals("abcabcdzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz_______________",
                StringUtil.normalizeFilename(fileName_2));
        assertEquals("Untitled", StringUtil.normalizeFilename(fileName_3));
        assertEquals("購買プロセス_2", StringUtil.normalizeFilename(fileName_4));
        assertEquals("_تحياتي", StringUtil.normalizeFilename(fileName_5));
        assertEquals("Untitled", StringUtil.normalizeFilename(fileName_6));
    }
}
