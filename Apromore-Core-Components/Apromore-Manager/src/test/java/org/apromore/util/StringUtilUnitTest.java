package org.apromore.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StringUtilUnitTest {

    @Test
    public void testNormalizeFilename() {

        String fileName_1 = "abc*d?.=";
        String fileName_2 = "abc*abcdzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz_______________more_than_60_characters";
        String fileName_3 = "?";

        assertEquals("abcd.", StringUtil.normalizeFilename(fileName_1));
        assertEquals("abcabcdzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz_______________",
                StringUtil.normalizeFilename(fileName_2));
        assertEquals("Untitled", StringUtil.normalizeFilename(fileName_3));
    }
}
