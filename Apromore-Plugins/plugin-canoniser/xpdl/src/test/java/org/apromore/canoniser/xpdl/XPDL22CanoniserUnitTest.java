package org.apromore.canoniser.xpdl;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class XPDL22CanoniserUnitTest {

    @Test
    public void testGetNativeType() {
        assertEquals("XPDL 2.2", new XPDL22Canoniser().getNativeType());
    }

    @Test
    public void testGetType() {
        assertEquals("Canoniser", new XPDL22Canoniser().getType());
    }

}
