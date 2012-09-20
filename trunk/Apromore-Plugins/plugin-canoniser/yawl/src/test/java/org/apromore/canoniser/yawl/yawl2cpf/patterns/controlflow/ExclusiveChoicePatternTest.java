package org.apromore.canoniser.yawl.yawl2cpf.patterns.controlflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.apromore.canoniser.yawl.utils.TestUtils;
import org.apromore.canoniser.yawl.yawl2cpf.patterns.BasePatternTest;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.StateType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.XORSplitType;
import org.junit.Test;

public class ExclusiveChoicePatternTest extends BasePatternTest {

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.yawl.BaseYAWL2CPFTest#getYAWLFile()
     */
    @Override
    protected File getYAWLFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/Patterns/ControlFlow/WPC4ExclusiveChoice.yawl");
    }

    @Test
    public void testIsExclusiveChoice() {
        final NetType rootNet = yawl2Canonical.getCpf().getNet().get(0);
        assertEquals(9, rootNet.getEdge().size());
        assertEquals(8, rootNet.getNode().size());

        final NodeType nodeA = checkNode(rootNet, "A", TaskType.class, 1, 1);

        final List<EdgeType> edges = getOutgoingEdges(rootNet, nodeA.getId());
        assertEquals(1, edges.size());
        final NodeType routingNode = getNodeByID(rootNet, edges.get(0).getTargetId());
        assertTrue("Routing node should be XORSplitType", routingNode instanceof XORSplitType);
        assertEquals(3, countOutgoingEdges(rootNet, routingNode.getId()));

        final List<EdgeType> routingEdges = getOutgoingEdges(rootNet, routingNode.getId());
        for (final EdgeType edge : routingEdges) {
            assertTrue("Each edge from a XOR split should have a condition or isDefault enabled", edge.isDefault()
                    || (edge.getConditionExpr() != null));
        }

        checkNode(rootNet, "B", TaskType.class, 1, 1);
        final NodeType nodeC = checkNode(rootNet, "C", TaskType.class, 1, 1);
        checkNode(rootNet, "D", TaskType.class, 1, 1);

        final List<EdgeType> cEdges = getOutgoingEdges(rootNet, nodeC.getId());
        assertEquals(1, cEdges.size());
        final NodeType joiningNode = getNodeByID(rootNet, cEdges.get(0).getTargetId());
        assertTrue("Joining Node should be StateType", joiningNode instanceof StateType);
        assertEquals(3, countIncomingEdges(rootNet, joiningNode.getId()));
        assertEquals(1, countOutgoingEdges(rootNet, joiningNode.getId()));

    }

}
