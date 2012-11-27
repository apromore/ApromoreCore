package org.apromore.dao.model;

import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Test;

/**
 * Test the Fragment Distance POJO.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class FragmentDistanceUnitTest {

    @Test
    public void testLikeJavaBean() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(FragmentDistance.class);
    }

}
