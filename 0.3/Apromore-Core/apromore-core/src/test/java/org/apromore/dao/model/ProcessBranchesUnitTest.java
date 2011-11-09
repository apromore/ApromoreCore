package org.apromore.dao.model;

import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Test;

/**
 * Test the Process Branches POJO.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class ProcessBranchesUnitTest {

    @Test
    public void testLikeJavaBean() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(ProcessBranches.class);
    }

}
