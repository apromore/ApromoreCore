package org.apromore.dao.model;

import org.apromore.test.heuristic.EqualsChecker;
import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Test the Annotation POJO.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class TempVersionIdUnitTest {

    @Test
    public void testLikeJavaBean() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(TempVersionId.class);
    }

    @Test
    public void testConstructor() {
        int code = 10;
        int processId = 20;
        String version = "2.0";

        TempVersionId obj = new TempVersionId(code, processId, version);
        assertEquals("Code wasn't the expected value" , code, obj.getCode());
        assertEquals("ProcessId wasn't the expected value" , processId, obj.getProcessId());
        assertEquals("Version wasn't the expected value" , version, obj.getNewVersion());
    }

    @Test
    public void testHashCode() {
        TempVersionId id1 = new TempVersionId(10, 20, "3.0");
        TempVersionId id2 = new TempVersionId(10, 20, "3.0");
        assertEquals(id1, id2);
        assertTrue(id1.hashCode() == id2.hashCode());
    }

    @Test
    public void testEquals() {
        TempVersionId id1 = new TempVersionId(10, 20, "3.0");
        TempVersionId id2 = new TempVersionId(10, 20, "3.0");
        assertTrue(id1.equals(id2));
        EqualsChecker.assertEqualsIsProperlyImplemented(id1, id2);

        assertFalse(id1.equals(null));
    }
}
