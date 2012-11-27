package org.apromore.dao.model;

import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Test;

/**
 * Test the Node Attribute POJO.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class NodeAttributeUnitTest {

    @Test
    public void testLikeJavaBean() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(NodeAttribute.class);
    }

}
