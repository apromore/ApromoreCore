package org.apromore.service.model;

import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Test the Shared Fragment Version POJO.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class SharedFragmentVersionUnitTest {

    @Test
    public void testLikeJavaBean() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(SharedFragmentVersion.class);
    }


    @Test
    public void basicBean() {
        int fragmentId = 123;
        int numOfUses = 2;

        SharedFragmentVersion sfv = new SharedFragmentVersion(fragmentId, numOfUses);
        assertThat(sfv.getFragmentVersionid(), equalTo(fragmentId));
        assertThat(sfv.getNumberOfUses(), equalTo(numOfUses));

        sfv = new SharedFragmentVersion(String.valueOf(fragmentId), Long.valueOf(numOfUses));
        assertThat(sfv.getFragmentVersionid(), equalTo(fragmentId));
        assertThat(sfv.getNumberOfUses(), equalTo(numOfUses));
    }
}
