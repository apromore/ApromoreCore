package org.apromore.canoniser.xpdl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class XPDL21CanoniserUnitTest {

    @Test
    public void testGetNativeType() {
        assertEquals("XPDL 2.1", new XPDL21Canoniser().getNativeType());
    }

    @Test
    public void testGetType() {
        assertEquals("Canoniser", new XPDL21Canoniser().getType());
    }

}
