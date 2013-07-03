package org.apromore.canoniser.yawl.yawl2cpf.patterns.controlflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.util.List;

import org.apromore.canoniser.yawl.utils.TestUtils;
import org.apromore.canoniser.yawl.yawl2cpf.patterns.BasePatternUnitTest;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.StateType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.XORJoinType;
import org.junit.Test;

public class SimpleMergeUnitTest extends BasePatternUnitTest {

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.yawl.BaseYAWL2CPFUnitTest#getYAWLFile()
     */
    @Override
    protected File getYAWLFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/Patterns/ControlFlow/WPC5SimpleMerge.yawl");
    }

    @Test
    public void testSimpleMerge() {
        final NetType rootNet = yawl2Canonical.getCpf().getNet().get(0);
        assertEquals(9, rootNet.getEdge().size());
        assertEquals(8, rootNet.getNode().size());

        final List<EdgeType> edges = getOutgoingEdges(rootNet, getNodeByName(rootNet, "IN").getId());
        assertEquals(1, edges.size());
        final NodeType routingNode = getNodeByID(rootNet, edges.get(0).getTargetId());
        checkNode(rootNet, routingNode, StateType.class, 1, 3);

        final List<EdgeType> routingEdges = getOutgoingEdges(rootNet, routingNode.getId());
        for (final EdgeType edge : routingEdges) {
            assertNull("No condition after StateNode", edge.getConditionExpr());
        }

        checkNode(rootNet, "A", TaskType.class, 1, 1);
        checkNode(rootNet, "B", TaskType.class, 1, 1);
        final NodeType nodeC = checkNode(rootNet, "C", TaskType.class, 1, 1);

        final List<EdgeType> cEdges = getOutgoingEdges(rootNet, nodeC.getId());
        assertEquals(1, cEdges.size());
        final NodeType joiningNode = getNodeByID(rootNet, cEdges.get(0).getTargetId());
        checkNode(rootNet, joiningNode, XORJoinType.class, 3, 1);

        checkNode(rootNet, "D", TaskType.class, 1, 1);

    }
}
