package org.apromore.dao.model;

import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Test;

/**
 * Test the Process Fragment Map POJO.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class ProcessFragmentMapUnitTest {

    @Test
    public void testLikeJavaBean() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(ProcessFragmentMap.class);
    }

}
