package org.apromore.dao.model;

import org.apromore.test.heuristic.EqualsChecker;
import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Test the Temp Version Id POJO.
 *  @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class TempVersionIdUnitTest {

    @Test
    public void testLikeJavaBean() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(TempVersionId.class);
    }

    @Test
    public void testConstructor() {
        int first = 1;
        int second = 2;
        String third = "third";

        TempVersionId obj = new TempVersionId(first, second, third);
        assertEquals("First param wasn't the expected value" , first, obj.getCode());
        assertEquals("Second Param wasn't the expected value" , second, obj.getProcessId());
        assertEquals("Third Param wasn't the expected value" , third, obj.getNewVersion());
    }

    @Test
    public void testHashCode() {
        int first = 1;
        int second = 2;
        String third = "third";

        TempVersionId id1 = new TempVersionId(first, second, third);
        TempVersionId id2 = new TempVersionId(first, second, third);
        assertEquals(id1, id2);
        assertTrue(id1.hashCode() == id2.hashCode());
    }

    @Test
    public void testEquals() {
        int first = 1;
        int second = 2;
        String third = "third";

        TempVersionId id1 = new TempVersionId(first, second, third);
        TempVersionId id2 = new TempVersionId(first, second, third);
        assertTrue(id1.equals(id2));
        EqualsChecker.assertEqualsIsProperlyImplemented(id1, id2);
        assertFalse(id1.equals(null));
    }
}
