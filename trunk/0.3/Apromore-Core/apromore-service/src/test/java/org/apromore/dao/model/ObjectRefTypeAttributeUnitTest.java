package org.apromore.dao.model;

import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Test;

/**
 * Test the Object Ref Type Attribute POJO.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class ObjectRefTypeAttributeUnitTest {

    @Test
    public void testLikeJavaBean() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(ObjectRefTypeAttribute.class);
    }

}
