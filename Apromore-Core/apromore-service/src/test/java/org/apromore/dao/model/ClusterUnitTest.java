package org.apromore.dao.model;

import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Test the Cluster POJO.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class ClusterUnitTest {

    @Test
    public void testLikeJavaBean() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(Cluster.class);
    }

    @Test
    public void clusterTest() {
        Cluster cluster = new Cluster();

        assertThat(cluster.toString(), equalTo("null | 0 | 0.0 | 0.0"));

        cluster.addClusterAssignment(new ClusterAssignment());
        cluster.addClusterAssignment(new ClusterAssignment());

        assertThat(cluster.getClusterAssignments().size(), equalTo(2));
    }
}
