package org.apromore.dao.model;

import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Test the Clustering Summary POJO.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class ClusteringSummaryUnitTest {

    @Test
    public void testLikeJavaBean() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(ClusteringSummary.class);
    }

    @Test
    public void testConstructor() {
        ClusteringSummary summary = new ClusteringSummary(1l, 1, 2, 1.2f, 2.3f, 4.0d, 5.0d);
        assertThat(summary.getNumClusters(), equalTo(1));
        assertThat(summary.getMinClusterSize(), equalTo(1));
        assertThat(summary.getMaxClusterSize(), equalTo(2));
        assertThat(summary.getMinAvgFragmentSize(), equalTo(1.2f));
        assertThat(summary.getMaxAvgFragmentSize(), equalTo(2.3f));
        assertThat(summary.getMinBCR(), equalTo(4.0d));
        assertThat(summary.getMaxBCR(), equalTo(5.0d));

        summary = new ClusteringSummary(null, 1, 2, 1.2f, 2.3f, 4.0d, 5.0d);
        assertThat(summary.getNumClusters(), equalTo(0));
        assertThat(summary.getMinClusterSize(), equalTo(1));
        assertThat(summary.getMaxClusterSize(), equalTo(2));
        assertThat(summary.getMinAvgFragmentSize(), equalTo(1.2f));
        assertThat(summary.getMaxAvgFragmentSize(), equalTo(2.3f));
        assertThat(summary.getMinBCR(), equalTo(4.0d));
        assertThat(summary.getMaxBCR(), equalTo(5.0d));
    }

}
