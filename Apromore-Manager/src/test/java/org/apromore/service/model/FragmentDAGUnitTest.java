package org.apromore.service.model;

import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Test the Fragment DAG POJO.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class FragmentDAGUnitTest {

    private static final String f1 = "1234";
    private static final String f2 = "4321";
    private static final String f3 = "1111";
    private static final String f4 = "2222";

    @Test
    public void testLikeJavaBean() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(FragmentDAG.class);
    }

    @Test
    public void basicBean() {
        FragmentDAG fdag = new FragmentDAG();
        FDNode fdNode1 = new FDNode(FragmentDAGUnitTest.f1);
        FDNode fdNode2 = new FDNode(FragmentDAGUnitTest.f2);
        fdag.addFragment(fdNode1);
        fdag.addFragment(fdNode2);

        assertThat(fdag.contains(FragmentDAGUnitTest.f1), equalTo(true));
        assertThat(fdag.contains("5555"), equalTo(false));

        assertThat(fdag.getFragment(FragmentDAGUnitTest.f1), equalTo(fdNode1));
        assertThat(fdag.getFragmentIds().contains(FragmentDAGUnitTest.f1), equalTo(true));
        assertThat(fdag.getFragmentIds().contains(FragmentDAGUnitTest.f2), equalTo(true));
        assertThat(fdag.getFragmentIds().contains(5555), equalTo(false));
    }

    @Test
    public void testIsItIncludedParent() {
        List<String> childId1 = new ArrayList<String>();
        childId1.add(FragmentDAGUnitTest.f2);
        List<String> childId2 = new ArrayList<String>();
        childId2.add(FragmentDAGUnitTest.f3);
        List<String> childId3 = new ArrayList<String>();
        childId3.add(FragmentDAGUnitTest.f4);

        FragmentDAG fdag = new FragmentDAG();
        FDNode fdNode1 = new FDNode(FragmentDAGUnitTest.f1);
        FDNode fdNode2 = new FDNode(FragmentDAGUnitTest.f2);
        FDNode fdNode3 = new FDNode(FragmentDAGUnitTest.f3);
        FDNode fdNode4 = new FDNode(FragmentDAGUnitTest.f4);

        fdNode1.setChildIds(childId1);
        fdNode2.setChildIds(childId2);
        fdNode3.setChildIds(childId3);

        fdag.addFragment(fdNode1);
        fdag.addFragment(fdNode2);
        fdag.addFragment(fdNode3);
        fdag.addFragment(fdNode4);

        assertThat(fdag.contains(FragmentDAGUnitTest.f1), equalTo(true));

        assertThat(fdag.isIncluded(FragmentDAGUnitTest.f1, FragmentDAGUnitTest.f1), equalTo(true));
        assertThat(fdag.isIncluded(FragmentDAGUnitTest.f1, FragmentDAGUnitTest.f2), equalTo(true));
        assertThat(fdag.isIncluded(FragmentDAGUnitTest.f2, FragmentDAGUnitTest.f4), equalTo(true));
        assertThat(fdag.isIncluded(FragmentDAGUnitTest.f1, "5555"), equalTo(false));
    }


    @Test
    public void testIsItIncludedForListIds() {
        List<String> childId1 = new ArrayList<String>();
        childId1.add(FragmentDAGUnitTest.f2);
        List<String> childId2 = new ArrayList<String>();
        childId2.add(FragmentDAGUnitTest.f3);
        childId2.add(FragmentDAGUnitTest.f4);

        FragmentDAG fdag = new FragmentDAG();
        FDNode fdNode1 = new FDNode(FragmentDAGUnitTest.f1);
        fdNode1.setChildIds(childId1);
        FDNode fdNode2 = new FDNode(FragmentDAGUnitTest.f2);
        fdag.addFragment(fdNode1);
        fdag.addFragment(fdNode2);

        assertThat(fdag.contains(FragmentDAGUnitTest.f1), equalTo(true));
        assertThat(fdag.isIncluded(FragmentDAGUnitTest.f1, childId1), equalTo(true));
        assertThat(fdag.isIncluded(FragmentDAGUnitTest.f1, childId2), equalTo(false));
    }
}