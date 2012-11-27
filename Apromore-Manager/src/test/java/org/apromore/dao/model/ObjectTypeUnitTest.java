package org.apromore.dao.model;

import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Test;

/**
 * Test the Object Type POJO.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class ObjectTypeUnitTest {

    @Test
    public void testLikeJavaBean() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(Object.class);
    }

}
