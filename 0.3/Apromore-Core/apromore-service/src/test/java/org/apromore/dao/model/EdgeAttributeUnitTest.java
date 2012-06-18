package org.apromore.dao.model;

import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Test;

/**
 * Test the Edge Attribute POJO.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class EdgeAttributeUnitTest {

    @Test
    public void testLikeJavaBean() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(EdgeAttribute.class);
    }

}
