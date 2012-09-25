package org.apromore.canoniser.yawl.yawl2cpf.patterns.controlflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import org.apromore.canoniser.yawl.internal.utils.ConversionUtils;
import org.apromore.canoniser.yawl.utils.TestUtils;
import org.apromore.canoniser.yawl.yawl2cpf.patterns.BasePatternTest;
import org.apromore.cpf.ANDSplitType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.MessageType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.StateType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.TimerType;
import org.apromore.cpf.WorkType;
import org.apromore.cpf.XORJoinType;
import org.junit.Test;

public class TimerTest extends BasePatternTest {

    @Override
    protected File getYAWLFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/Others/SimpleTimerTask.yawl");
    }

    @Test
    public void testTimerOnStart() {
        final NetType rootNet = yawl2Canonical.getCpf().getNet().get(0);

        assertEquals(14, rootNet.getNode().size());

        // Timer onStart
        final TaskType taskA = (TaskType) checkNode(rootNet, "A", TaskType.class, 1, 1);
        final NodeType xorJoin = getFirstPredecessor(rootNet, taskA);
        checkNode(rootNet, xorJoin, XORJoinType.class, 2, 1);
        final List<EdgeType> incomingXorJoin = getIncomingEdges(rootNet, xorJoin.getId());
        final NodeType nodeA = getNodeById(rootNet, incomingXorJoin.get(0).getSourceId());
        final NodeType nodeB = getNodeById(rootNet, incomingXorJoin.get(1).getSourceId());
        if ((nodeA instanceof TimerType && nodeB instanceof MessageType) || (nodeB instanceof TimerType && nodeA instanceof MessageType)) {
            checkMutuallyCancelingEachOther(nodeA, nodeB);
        } else {
            fail("Timer onStart converted in a wrong way");
        }
        assertEquals(getFirstPredecessor(rootNet, nodeA), getFirstPredecessor(rootNet, nodeB));
        assertNotNull(checkNode(rootNet, getFirstPredecessor(rootNet, nodeA), ANDSplitType.class, 1, 2));
        checkNode(rootNet, getFirstSuccessor(rootNet, taskA), StateType.class, 1, 2);
        // C should be left untouched
        checkNode(rootNet, "C", TaskType.class, 1, 1);
    }

    @Test
    public void testTimerOnEnablement() {
        final NetType rootNet = yawl2Canonical.getCpf().getNet().get(0);

        assertEquals(14, rootNet.getNode().size());

        // Timer onStart
        final TaskType taskB = (TaskType) checkNode(rootNet, "B", TaskType.class, 1, 1);
        final ANDSplitType andSplit = checkNode(rootNet, getFirstPredecessor(rootNet, taskB), ANDSplitType.class, 1, 2);
        final XORJoinType xorJoin = checkNode(rootNet, getFirstSuccessor(rootNet, taskB), XORJoinType.class, 2, 1);

        final List<EdgeType> incomingXorJoin = getIncomingEdges(rootNet, xorJoin.getId());
        final NodeType nodeA = getNodeById(rootNet, incomingXorJoin.get(0).getSourceId());
        final NodeType nodeB = getNodeById(rootNet, incomingXorJoin.get(1).getSourceId());
        if ((nodeA instanceof TimerType && nodeB instanceof TaskType) || (nodeB instanceof TimerType && nodeA instanceof TaskType)) {
            checkMutuallyCancelingEachOther(nodeA, nodeB);
        } else {
            fail("Timer onEnablement converted in a wrong way");
        }

    }

    private void checkMutuallyCancelingEachOther(final NodeType nodeA, final NodeType nodeB) {
        final WorkType workA = (WorkType) nodeA;
        final boolean isACancelingB = workA.getCancelNodeId().get(0).getRefId().equals(nodeB.getId());
        assertTrue("Node " + ConversionUtils.toString(workA) + " does not cancel " + ConversionUtils.toString(nodeB), isACancelingB);
        final WorkType workB = (WorkType) nodeB;
        final boolean isBCancelingA = workB.getCancelNodeId().get(0).getRefId().equals(nodeA.getId());
        assertTrue("Node " + ConversionUtils.toString(workB) + " does not cancel " + ConversionUtils.toString(nodeB), isBCancelingA);
    }

}
