package org.apromore.canoniser.epml;

import org.junit.Ignore;
import org.junit.Test;

public class AudioUnitTest extends AbstractTest {

    @Ignore("Canonisation of C-IEPC <range> not implemented")
    @Test public void testCanonise() throws Exception { testCanonise("Audio"); }
}
