package org.apromore.dao.model;

import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Test;

/**
 * Test the Object Type Attribute POJO.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class ObjectTypeAttributeUnitTest {

    @Test
    public void testLikeJavaBean() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(ObjectTypeAttribute.class);
    }

}
