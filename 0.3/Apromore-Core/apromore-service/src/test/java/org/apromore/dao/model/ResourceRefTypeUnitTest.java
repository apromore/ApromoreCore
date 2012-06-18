package org.apromore.dao.model;

import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Test;

/**
 * Test the Resource Ref Type POJO.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class ResourceRefTypeUnitTest {

    @Test
    public void testLikeJavaBean() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(ResourceRefType.class);
    }

}
