package org.apromore.dao.model;

import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Test;

/**
 * Test the Edges POJO.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class EdgesUnitTest {

    @Test
    public void testLikeJavaBean() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(Edges.class);
    }

}
