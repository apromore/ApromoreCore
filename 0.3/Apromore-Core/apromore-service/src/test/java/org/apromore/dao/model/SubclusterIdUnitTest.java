package org.apromore.dao.model;

import org.apromore.test.heuristic.EqualsChecker;
import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Test the Sub Cluster POJO.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class SubclusterIdUnitTest {

    @Test
    public void testLikeJavaBean() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(SubclusterId.class);
    }

    @Test
    public void testConstructor() {
        String version = "1";
        String parent = "1.0";

        SubclusterId obj = new SubclusterId(version, parent);
        assertEquals("Version wasn't the expected value" , version, obj.getFragmentVersionId());
        assertEquals("Parent wasn't the expected value" , parent, obj.getParentClusterId());
    }

    @Test
    public void testHashCode() {
        SubclusterId id1 = new SubclusterId("1", "1.0");
        SubclusterId id2 = new SubclusterId("1", "1.0");
        assertEquals(id1, id2);
        assertTrue(id1.hashCode() == id2.hashCode());
    }

    @Test
    public void testEquals() {
        SubclusterId id1 = new SubclusterId("1", "1.0");
        SubclusterId id2 = new SubclusterId("1", "1.0");
        assertTrue(id1.equals(id2));
        EqualsChecker.assertEqualsIsProperlyImplemented(id1, id2);

        assertFalse(id1.equals(null));
    }
}
