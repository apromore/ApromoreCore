package org.apromore.dao.model;

import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Test;

/**
 * Test the Object Ref Type POJO.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class ObjectRefTypeUnitTest {

    @Test
    public void testLikeJavaBean() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(ObjectRefType.class);
    }

}
