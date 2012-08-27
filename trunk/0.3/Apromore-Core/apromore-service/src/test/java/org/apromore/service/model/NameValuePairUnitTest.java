package org.apromore.service.model;

import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Test the Name Value Pair POJO.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class NameValuePairUnitTest {

    @Test
    public void testLikeJavaBean() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(NameValuePair.class);
    }


    @Test
    public void testConstructor() {
        String first = "1";
        String second = "2";

        NameValuePair obj = new NameValuePair(first, second);
        assertEquals("First param wasn't the expected value" , first, obj.getName());
        assertEquals("Second Param wasn't the expected value" , second, obj.getValue());
    }
}