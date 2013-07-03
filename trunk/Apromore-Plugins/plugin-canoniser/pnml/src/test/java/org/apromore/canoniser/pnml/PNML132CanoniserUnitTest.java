package org.apromore.canoniser.pnml;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PNML132CanoniserUnitTest {

    @Test
    public void testGetNativeType() {
        assertEquals("PNML 1.3.2", new PNML132Canoniser().getNativeType());
    }

    @Test
    public void testGetType() {
        assertEquals("Canoniser", new PNML132Canoniser().getType());
    }

}
