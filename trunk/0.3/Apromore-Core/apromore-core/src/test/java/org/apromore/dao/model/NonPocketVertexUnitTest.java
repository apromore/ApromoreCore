package org.apromore.dao.model;

import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Test;

/**
 * Test the Non Pocket Vertex POJO.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class NonPocketVertexUnitTest {

    @Test
    public void testLikeJavaBean() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(NonPocketVertex.class);
    }

}
