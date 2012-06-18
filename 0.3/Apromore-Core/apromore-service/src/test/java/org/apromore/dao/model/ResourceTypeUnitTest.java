package org.apromore.dao.model;

import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Test;

/**
 * Test the Resource Type POJO.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class ResourceTypeUnitTest {

    @Test
    public void testLikeJavaBean() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(ResourceType.class);
    }

}
