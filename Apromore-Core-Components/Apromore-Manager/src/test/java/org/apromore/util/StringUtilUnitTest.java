package org.apromore.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StringUtilUnitTest {

    @Test
    public void testNormalizeFilename() {

        String fileName_1 = "abc*d?.=";
        String fileName_2 = "abc*abcdzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz_______________more_than_60_characters";
        String fileName_3 = "?";

        assertEquals(StringUtil.normalizeFilename(fileName_1), "abcd.");
        assertEquals(StringUtil.normalizeFilename(fileName_2), "abcabcdzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz_______________");
        assertEquals(StringUtil.normalizeFilename(fileName_3), "Untitled");
    }
}
