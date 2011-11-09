package org.apromore.dao.model;

import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Test;

/**
 * Test the Issued Vertex Ids POJO.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class IssuedVertexIdsUnitTest {

    @Test
    public void testLikeJavaBean() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(IssuedVertexIds.class);
    }

}
