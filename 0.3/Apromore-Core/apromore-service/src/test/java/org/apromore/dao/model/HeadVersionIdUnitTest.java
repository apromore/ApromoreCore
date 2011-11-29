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
public class HeadVersionIdUnitTest {

    @Test
    public void testLikeJavaBean() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(HeadVersionId.class);
    }

    @Test
    public void testConstructor() {
        Integer processId = new Integer(10);
        String version = "bob";

        HeadVersionId obj = new HeadVersionId(processId, version);
        assertEquals("ProcessId wasn't the expected value" , processId, obj.getProcessId());
        assertEquals("Version wasn't the expected value" , version, obj.getVersion());
    }

    @Test
    public void testHashCode() {
        HeadVersionId id1 = new HeadVersionId(new Integer(20), "process");
        HeadVersionId id2 = new HeadVersionId(new Integer(20), "process");
        assertEquals(id1, id2);
        assertTrue(id1.hashCode() == id2.hashCode());
    }

    @Test
    public void testEquals() {
        HeadVersionId id1 = new HeadVersionId(new Integer(20), "process");
        HeadVersionId id2 = new HeadVersionId(new Integer(20), "process");
        assertTrue(id1.equals(id2));
        EqualsChecker.assertEqualsIsProperlyImplemented(id1, id2);

        assertFalse(id1.equals(null));
    }
}
