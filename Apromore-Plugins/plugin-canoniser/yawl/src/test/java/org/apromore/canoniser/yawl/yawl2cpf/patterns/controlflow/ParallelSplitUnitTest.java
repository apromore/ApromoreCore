package org.apromore.canoniser.yawl.yawl2cpf.patterns.controlflow;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.apromore.canoniser.yawl.utils.TestUtils;
import org.apromore.canoniser.yawl.yawl2cpf.patterns.BasePatternUnitTest;
import org.apromore.cpf.ANDJoinType;
import org.apromore.cpf.ANDSplitType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.TaskType;
import org.junit.Test;

public class ParallelSplitUnitTest extends BasePatternUnitTest {

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.yawl.patterns.controlflow.PatternTest#getYAWLFile()
     */
    @Override
    protected File getYAWLFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/Patterns/ControlFlow/WPC2ParallelSplit.yawl");
    }

    @Test
    public void testIsParallelSplit() {
        final NetType rootNet = yawl2Canonical.getCpf().getNet().get(0);
        assertEquals(10, rootNet.getEdge().size());
        assertEquals(9, rootNet.getNode().size());

        final NodeType nodeA = checkNode(rootNet, "A", TaskType.class, 1, 1);

        final List<EdgeType> edges = getOutgoingEdges(rootNet, nodeA.getId());
        assertEquals(1, edges.size());
        final NodeType routingNode = getNodeByID(rootNet, edges.get(0).getTargetId());
        checkNode(rootNet, routingNode, ANDSplitType.class, 1, 3);

        checkNode(rootNet, "B", TaskType.class, 1, 1);
        final NodeType nodeC = checkNode(rootNet, "C", TaskType.class, 1, 1);

        final List<EdgeType> cEdges = getOutgoingEdges(rootNet, nodeC.getId());
        assertEquals(1, cEdges.size());
        final NodeType joiningNode = getNodeByID(rootNet, cEdges.get(0).getTargetId());
        checkNode(rootNet, joiningNode, ANDJoinType.class, 3, 1);
    }

}
