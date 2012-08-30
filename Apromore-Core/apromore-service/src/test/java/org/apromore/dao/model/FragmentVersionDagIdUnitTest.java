package org.apromore.dao.model;

import org.apromore.test.heuristic.EqualsChecker;
import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Test the FragmentVersionDagId POJO.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class FragmentVersionDagIdUnitTest {

    @Test
    public void testLikeJavaBean() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(FragmentVersionDagId.class);
    }

    @Test
    public void testConstructor() {
        String first = "first";
        String second = "second";
        String third = "third";

        FragmentVersionDagId obj = new FragmentVersionDagId(first, second, third);
        assertEquals("First param wasn't the expected value" , first, obj.getFragmentVersionId());
        assertEquals("Second Param wasn't the expected value" , second, obj.getChildFragmentVersionId());
        assertEquals("Third Param wasn't the expected value" , third, obj.getPocketId());
    }

    @Test
    public void testHashCode() {
        String first = "first";
        String second = "second";
        String third = "third";

        FragmentVersionDagId id1 = new FragmentVersionDagId(first, second, third);
        FragmentVersionDagId id2 = new FragmentVersionDagId(first, second, third);
        assertEquals(id1, id2);
        assertTrue(id1.hashCode() == id2.hashCode());
    }

    @Test
    public void testEquals() {
        String first = "first";
        String second = "second";
        String third = "third";

        FragmentVersionDagId id1 = new FragmentVersionDagId(first, second, third);
        FragmentVersionDagId id2 = new FragmentVersionDagId(first, second, third);
        assertTrue(id1.equals(id2));
        EqualsChecker.assertEqualsIsProperlyImplemented(id1, id2);
        assertFalse(id1.equals(null));
    }

}
