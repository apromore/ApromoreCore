package org.apromore.canoniser.yawl.yawl2cpf.patterns.controlflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.apromore.canoniser.yawl.utils.TestUtils;
import org.apromore.canoniser.yawl.yawl2cpf.patterns.BasePatternUnitTest;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.XORJoinType;
import org.apromore.cpf.XORSplitType;
import org.junit.Test;

public class AbitraryCyclesUnitTest extends BasePatternUnitTest {

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.yawl.BaseYAWL2CPFUnitTest#getYAWLFile()
     */
    @Override
    protected File getYAWLFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/Patterns/ControlFlow/WPC10AbitraryCycles.yawl");
    }

    @Test
    public void testAbitraryCycles() {
        final NetType rootNet = yawl2Canonical.getCpf().getNet().get(0);

        final NodeType a = checkNode(rootNet, "A", TaskType.class, 1, 1);
        assertNotNull(a);
        final NodeType joinA = getFirstPredecessor(rootNet, a);
        checkNode(rootNet, joinA, XORJoinType.class, 2, 1);

        final NodeType b = checkNode(rootNet, "B", TaskType.class, 1, 1);
        final NodeType joinB = getFirstPredecessor(rootNet, b);
        checkNode(rootNet, joinB, XORJoinType.class, 2, 1);

        final NodeType c = checkNode(rootNet, "C", TaskType.class, 1, 1);
        final NodeType splitC = getFirstSuccessor(rootNet, c);
        checkNode(rootNet, splitC, XORSplitType.class, 1, 2);

        final NodeType d = checkNode(rootNet, "D", TaskType.class, 1, 1);
        final NodeType splitD = getFirstSuccessor(rootNet, d);
        checkNode(rootNet, splitD, XORSplitType.class, 1, 2);

        final NodeType e = checkNode(rootNet, "E", TaskType.class, 1, 1);
        assertEquals(joinB, getFirstSuccessor(rootNet, e));
        assertEquals(splitD, getFirstPredecessor(rootNet, e));
    }

}
