package org.apromore.dao.model;

import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Test;

/**
 * Test the Processes POJO.
 *  @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class ProcessesUnitTest {

    @Test
    public void testLikeJavaBean() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(Processes.class);
    }

}
