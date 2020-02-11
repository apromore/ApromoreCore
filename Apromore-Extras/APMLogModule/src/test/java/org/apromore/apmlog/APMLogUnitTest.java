package org.apromore.apmlog;

import org.deckfour.xes.in.XesXmlGZIPParser;
import org.deckfour.xes.model.XLog;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test suite for {@link APMLog}.
 */
public class APMLogUnitTest {

    private XLog xLog;

    @Before
    public void before() throws Exception {
        xLog = (new XesXmlGZIPParser()).parse(getClass().getResourceAsStream("/BPI Challenge 2013 closed problems.xes.gz")).get(0);
    }

    @Ignore("This test demonstrates the defect AP-1037")
    @Test
    public void testConstructor_BPIC13() {
        APMLog apmLog = new APMLog(xLog);
    }
}
