package org.apromore.canoniser.yawl.internal.utils;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.HashSet;

import org.junit.Test;

public class ConversionUtilsUnitTest {

    @Test
    public void testConvertColorToString() {
        assertEquals("R:204G:204B:204", ConversionUtils.convertColorToString(-3355444));
    }

    @Test
    public void testConvertColorToBigInteger() {
        // Different result as we are missing the Hue value in our String
        assertEquals(new BigInteger("13421772"), ConversionUtils.convertColorToBigInteger("R:204G:204B:204"));
    }

    @Test
    public void testGenerateUniqueName() {
        HashSet<String> nameSet = new HashSet<String>();
        nameSet.add("test");
        nameSet.add("test1");
        nameSet.add("test2");
        assertEquals("test3", ConversionUtils.generateUniqueName("test", nameSet));
    }

}
