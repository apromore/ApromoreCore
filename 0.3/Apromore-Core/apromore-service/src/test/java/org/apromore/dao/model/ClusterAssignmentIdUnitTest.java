package org.apromore.dao.model;

import org.apromore.test.heuristic.EqualsChecker;
import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Test the Cluster Assignment Id POJO.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class ClusterAssignmentIdUnitTest {

    @Test
    public void testLikeJavaBean() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(ClusterAssignmentId.class);
    }

    @Test
    public void testConstructor() {
        String first = "1";
        String second = "2";

        ClusterAssignmentId obj = new ClusterAssignmentId(first, second);
        assertEquals("First param wasn't the expected value" , first, obj.getFragmentId());
        assertEquals("Second Param wasn't the expected value" , second, obj.getClusterId());
    }

    @Test
    public void testHashCode() {
        String first = "1";
        String second = "2";

        ClusterAssignmentId id1 = new ClusterAssignmentId(first, second);
        ClusterAssignmentId id2 = new ClusterAssignmentId(first, second);
        assertEquals(id1, id2);
        assertTrue(id1.hashCode() == id2.hashCode());
    }

    @Test
    public void testEquals() {
        String first = "1";
        String second = "2";

        ClusterAssignmentId id1 = new ClusterAssignmentId(first, second);
        ClusterAssignmentId id2 = new ClusterAssignmentId(first, second);
        assertTrue(id1.equals(id2));
        EqualsChecker.assertEqualsIsProperlyImplemented(id1, id2);
        assertFalse(id1.equals(null));
    }
}
