package org.apromore.service.model;

import java.util.ArrayList;
import java.util.List;

import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

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
    public void testAddingAFragment() {
        Cluster obj = new Cluster();
        obj.addFragment(new MemberFragment("1"));
        obj.addFragment(new MemberFragment("2"));

        Assert.assertThat(obj.getFragments().size(), CoreMatchers.equalTo(2));
        Assert.assertThat(obj.getFragments().get(0).getFragmentId(), CoreMatchers.equalTo("1"));
    }
}
