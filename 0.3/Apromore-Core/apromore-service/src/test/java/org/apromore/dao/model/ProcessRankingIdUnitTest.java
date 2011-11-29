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
public class ProcessRankingIdUnitTest {

    @Test
    public void testLikeJavaBean() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(ProcessRankingId.class);
    }

    @Test
    public void testConstructor() {
        Integer processId = new Integer(10);
        Double ranking = new Double(2.0);

        ProcessRankingId obj = new ProcessRankingId(processId, ranking);
        assertEquals("ProcessId wasn't the expected value" , processId, obj.getProcessId());
        assertEquals("Ranking wasn't the expected value" , ranking, obj.getRanking());
    }

    @Test
    public void testHashCode() {
        ProcessRankingId id1 = new ProcessRankingId(new Integer(20), new Double(2.0));
        ProcessRankingId id2 = new ProcessRankingId(new Integer(20), new Double(2.0));
        assertEquals(id1, id2);
        assertTrue(id1.hashCode() == id2.hashCode());
    }

    @Test
    public void testEquals() {
        ProcessRankingId id1 = new ProcessRankingId(new Integer(20), new Double(2.0));
        ProcessRankingId id2 = new ProcessRankingId(new Integer(20), new Double(2.0));
        assertTrue(id1.equals(id2));
        EqualsChecker.assertEqualsIsProperlyImplemented(id1, id2);

        assertFalse(id1.equals(null));
    }
}
