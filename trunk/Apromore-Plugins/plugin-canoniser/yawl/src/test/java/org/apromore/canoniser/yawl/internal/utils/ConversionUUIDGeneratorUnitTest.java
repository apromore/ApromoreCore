package org.apromore.canoniser.yawl.internal.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class ConversionUUIDGeneratorUnitTest {

    @Test
    public void testGetUUID() {
        final ConversionUUIDGenerator gen = new ConversionUUIDGenerator();
        assertNotNull(gen.getUUID(null));
        final String id1 = gen.getUUID("test");
        final String id2 = gen.getUUID("test");
        assertEquals(id1, id2);

        final String id3 = gen.getUUID("test_");
        assertFalse(id3.contains("_"));

        final String id4 = gen.getUUID("test ");
        assertFalse(id4.contains(" "));

    }

}
