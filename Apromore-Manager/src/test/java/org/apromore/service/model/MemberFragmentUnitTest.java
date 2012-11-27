package org.apromore.service.model;

import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Test the Member Fragment POJO.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class MemberFragmentUnitTest {

    @Test
    public void testLikeJavaBean() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(MemberFragment.class);
    }

    @Test
    public void testConstructor() {
        Integer first = 1;

        MemberFragment obj = new MemberFragment(first);
        assertEquals("First param wasn't the expected value" , first, obj.getFragmentId());
    }
}