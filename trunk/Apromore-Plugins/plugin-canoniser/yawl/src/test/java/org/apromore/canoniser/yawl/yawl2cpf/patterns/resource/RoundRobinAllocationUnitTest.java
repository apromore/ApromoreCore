package org.apromore.canoniser.yawl.yawl2cpf.patterns.resource;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.apromore.canoniser.yawl.utils.TestUtils;
import org.apromore.canoniser.yawl.yawl2cpf.patterns.BasePatternUnitTest;
import org.apromore.cpf.AllocationStrategyEnum;
import org.apromore.cpf.NetType;
import org.apromore.cpf.TaskType;
import org.junit.Test;

public class RoundRobinAllocationUnitTest extends BasePatternUnitTest {

    @Override
    protected File getYAWLFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/Patterns/Resource/WPR16RoundRobinAllocation.yawl");
    }

    @Test
    public void testRoundRobin() {
        final NetType rootNet = yawl2Canonical.getCpf().getNet().get(0);
        TaskType taskA = checkNode(rootNet, "A", TaskType.class, 1, 1);
        TaskType taskB = checkNode(rootNet, "B", TaskType.class, 1, 1);
        TaskType taskC = checkNode(rootNet, "C", TaskType.class, 1, 1);
        assertEquals(AllocationStrategyEnum.ROUND_ROBIN_BY_TIME, taskA.getAllocationStrategy());
        assertEquals(AllocationStrategyEnum.ROUND_ROBIN_BY_FREQUENCY, taskB.getAllocationStrategy());
        assertEquals(AllocationStrategyEnum.ROUND_ROBIN_BY_EXPERIENCE, taskC.getAllocationStrategy());
    }

}
