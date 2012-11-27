package org.apromore.service.model;

import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Test the FDNode POJO.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class FDNodeUnitTest {

    @Test
    public void testLikeJavaBean() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(FDNode.class);
    }

    @Test
    public void basicBean() {
        String fragmentId = "123";
        List<String> ids = new ArrayList<String>(0);

        FDNode fdn = new FDNode(fragmentId);
        fdn.setChildIds(ids);
        fdn.setParentIds(ids);

        assertThat(fdn.getFragmentId(), equalTo(fragmentId));
        assertThat(fdn.getChildIds(), equalTo(ids));
        assertThat(fdn.getParentIds(), equalTo(ids));
    }

}
