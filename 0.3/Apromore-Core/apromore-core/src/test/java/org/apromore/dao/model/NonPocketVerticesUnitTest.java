package org.apromore.dao.model;

import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Test;

/**
 * Test the Non Pocket Vertices POJO.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class NonPocketVerticesUnitTest {

    @Test
    public void testLikeJavaBean() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(NonPocketVertices.class);
    }

}
