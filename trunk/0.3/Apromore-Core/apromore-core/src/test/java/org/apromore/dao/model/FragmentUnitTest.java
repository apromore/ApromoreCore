package org.apromore.dao.model;

import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Test;

/**
 * Test the Fragment POJO.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class FragmentUnitTest {

    @Test
    public void testLikeJavaBean() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(Fragment.class);
    }

}
