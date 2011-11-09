package org.apromore.dao.model;

import org.apromore.test.heuristic.EqualsChecker;
import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Test the Vertices POJO.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class VerticesUnitTest {

    @Test
    public void testLikeJavaBean() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(Vertices.class);
    }

}
