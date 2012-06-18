package org.apromore.service.model;

import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Test;

/**
 * Test the Similarity Search Data Object POJO.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class SimilarityDataUnitTest {

    @Test
    public void testLikeJavaBean() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(ToolboxData.class);
    }

}
