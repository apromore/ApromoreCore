package org.apromore.canoniser.epml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class EPML20CanoniserTest {

    private EPML20Canoniser epml20Canoniser;

    @Before
    public void setUp() {
        epml20Canoniser = new EPML20Canoniser();
    }

    @Test
    public void testEPML20Canoniser() {
        assertNotNull(epml20Canoniser);
    }

    @Ignore
    @Test
    public void testCanonise() {
        //TODO
    }

    @Ignore
    @Test
    public void testDeCanonise() {
      //TODO
    }

    @Test
    public void testGetNativeType() {
        assertEquals("EPML 2.0", epml20Canoniser.getNativeType());
    }

    @Test
    public void testGetName() {
        assertNotNull(epml20Canoniser.getName());
    }

    @Test
    public void testGetVersion() {
        assertNotNull(epml20Canoniser.getVersion());
    }

    @Test
    public void testGetType() {
        assertEquals("Canoniser", epml20Canoniser.getType());
    }

    @Test
    public void testGetDescription() {
        assertNotNull(epml20Canoniser.getDescription());
    }

    @Test
    public void testGetAuthor() {
        assertNotNull(epml20Canoniser.getAuthor());
    }

}
