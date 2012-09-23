package org.apromore.canoniser.yawl.yawl2cpf.patterns;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apromore.canoniser.yawl.BaseYAWL2CPFTest;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.junit.Test;

/**
 * Basic class for all pattern based tests. Assumes that there is a InputCondition named "IN" and an OutputCondition named "OUT".
 * 
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 * 
 */
public abstract class BasePatternTest extends BaseYAWL2CPFTest {

    @Test
    public void testInputCondition() {
        for (final NetType net : yawl2Canonical.getCpf().getNet()) {
            final NodeType nodeIN = getNodeByName(net, "IN");
            assertNotNull("InputCondition named 'IN' is missing!", nodeIN);
            assertEquals("Input Condition missing outgoing edge", 1, countOutgoingEdges(net, nodeIN.getId()));
            assertEquals("Input Condition must not have incoming edge", 0, countIncomingEdges(net, nodeIN.getId()));
        }
    }

    @Test
    public void testOutputCondition() {
        for (final NetType net : yawl2Canonical.getCpf().getNet()) {
            final NodeType nodeOUT = getNodeByName(net, "OUT");
            assertNotNull("OutputCondition named 'OUT' is missing!", nodeOUT);
            assertEquals("Output Condition must not have outgoing edge", 0, countOutgoingEdges(net, nodeOUT.getId()));
            assertEquals("Output Condition missing incoming edge", 1, countIncomingEdges(net, nodeOUT.getId()));
        }
    }

}
