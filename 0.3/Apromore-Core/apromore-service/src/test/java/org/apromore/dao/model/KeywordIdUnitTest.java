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
public class KeywordIdUnitTest {

    @Test
    public void testLikeJavaBean() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(KeywordId.class);
    }

    @Test
    public void testConstructor() {
        Integer processId = new Integer(10);
        String word = "bob";

        KeywordId obj = new KeywordId(processId, word);
        assertEquals("ProcessId wasn't the expected value" , processId, obj.getProcessId());
        assertEquals("Word wasn't the expected value" , word, obj.getWord());
    }

    @Test
    public void testHashCode() {
        KeywordId id1 = new KeywordId(new Integer(20), "process");
        KeywordId id2 = new KeywordId(new Integer(20), "process");
        assertEquals(id1, id2);
        assertTrue(id1.hashCode() == id2.hashCode());
    }

    @Test
    public void testEquals() {
        KeywordId id1 = new KeywordId(new Integer(20), "process");
        KeywordId id2 = new KeywordId(new Integer(20), "process");
        assertTrue(id1.equals(id2));
        EqualsChecker.assertEqualsIsProperlyImplemented(id1, id2);

        assertFalse(id1.equals(null));
    }
}
