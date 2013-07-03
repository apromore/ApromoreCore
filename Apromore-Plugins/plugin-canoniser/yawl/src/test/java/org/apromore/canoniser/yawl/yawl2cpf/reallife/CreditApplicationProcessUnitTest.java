package org.apromore.canoniser.yawl.yawl2cpf.reallife;

import java.io.File;
import java.util.List;

import org.apromore.canoniser.yawl.BaseYAWL2CPFUnitTest;
import org.apromore.canoniser.yawl.utils.TestUtils;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.XORSplitType;
import org.junit.Test;

public class CreditApplicationProcessUnitTest extends BaseYAWL2CPFUnitTest {

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.BaseYAWL2CPFUnitTest#getYAWLFile()
     */
    @Override
    protected File getYAWLFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/CreditApplicationProcess.yawl");
    }


    @Test
    public void testCreditApplication() {
        NetType net = yawl2Canonical.getCpf().getNet().get(0);
        checkNode(net, "check loan amount", TaskType.class, 1, 1);
        NodeType makeDecision = checkNode(net, "make decision", TaskType.class, 1, 1);
        NodeType makeDecisionSplit = getFirstSuccessor(net, makeDecision);
        checkNode(net, makeDecisionSplit, XORSplitType.class, 1, 2);
        List<EdgeType> edges = getOutgoingEdges(net, makeDecisionSplit.getId());
        checkOnlyOneDefaultEdge(edges);
        TaskType checkLoanAmount = checkNode(net, "check loan amount", TaskType.class, 1, 1);
        NodeType checkLoanAmountSplit = getFirstSuccessor(net, checkLoanAmount);
        checkNode(net, checkLoanAmountSplit, XORSplitType.class, 1, 2);
        checkOnlyOneDefaultEdge(getOutgoingEdges(net, checkLoanAmountSplit.getId()));
    }


}
