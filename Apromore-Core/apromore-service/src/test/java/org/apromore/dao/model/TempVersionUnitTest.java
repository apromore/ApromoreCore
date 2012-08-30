package org.apromore.dao.model;

import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Test;

/**
 * Test the Temp Version POJO.
 *  @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class TempVersionUnitTest {

    @Test
    public void testLikeJavaBean() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(TempVersion.class);
    }

}
