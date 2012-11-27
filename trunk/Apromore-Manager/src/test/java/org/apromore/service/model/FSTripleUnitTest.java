package org.apromore.service.model;

import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Test;

/**
 * Test the FSTriple POJO.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class FSTripleUnitTest {

    @Test
    public void testLikeJavaBean() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(FSTriple.class);
    }

}
