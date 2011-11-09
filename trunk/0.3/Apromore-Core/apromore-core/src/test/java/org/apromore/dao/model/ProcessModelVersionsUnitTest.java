package org.apromore.dao.model;

import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Test;

/**
 * Test the Process Model Versions POJO.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class ProcessModelVersionsUnitTest {

    @Test
    public void testLikeJavaBean() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(ProcessModelVersions.class);
    }

}
