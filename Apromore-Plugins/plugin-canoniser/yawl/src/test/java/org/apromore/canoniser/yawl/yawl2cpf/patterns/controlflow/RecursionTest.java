package org.apromore.canoniser.yawl.yawl2cpf.patterns.controlflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apromore.canoniser.yawl.utils.TestUtils;
import org.apromore.canoniser.yawl.yawl2cpf.patterns.BasePatternTest;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.TaskType;
import org.junit.Test;

public class RecursionTest extends BasePatternTest {

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.yawl.BaseYAWL2CPFTest#getYAWLFile()
     */
    @Override
    protected File getYAWLFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/Patterns/ControlFlow/WPC22Recursion.yawl");
    }

    @Test
    public void testIsRecursion() {
        final NetType rootNet = yawl2Canonical.getCpf().getNet().get(0);
        assertEquals(6, rootNet.getEdge().size());
        assertEquals(6, rootNet.getNode().size());

        final NodeType nodeNet = getNodeByName(rootNet, "Net");
        assertEquals(1, countOutgoingEdges(rootNet, nodeNet.getId()));
        assertEquals(1, countIncomingEdges(rootNet, nodeNet.getId()));
        assertTrue(nodeNet instanceof TaskType);
        final TaskType taskNet = (TaskType) nodeNet;
        assertEquals("Task linked to wrong subnet!", rootNet.getId(), taskNet.getSubnetId());
    }

}
