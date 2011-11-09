package org.apromore.dao.model;

import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Test;

/**
 * Test the Contents POJO.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class ContentsUnitTest {

    @Test
    public void testLikeJavaBean() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(Contents.class);
    }

}
