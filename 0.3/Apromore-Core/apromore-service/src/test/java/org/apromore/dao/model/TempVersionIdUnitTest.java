package org.apromore.dao.model;

import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Test;

/**
 * Test the Temp Version Id POJO.
 *  @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class TempVersionIdUnitTest {

    @Test
    public void testLikeJavaBean() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(TempVersionId.class);
    }

}
