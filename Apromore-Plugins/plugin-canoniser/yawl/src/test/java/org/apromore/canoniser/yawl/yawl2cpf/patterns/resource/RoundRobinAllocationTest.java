package org.apromore.canoniser.yawl.yawl2cpf.patterns.resource;

import java.io.File;

import org.apromore.canoniser.yawl.utils.TestUtils;
import org.apromore.canoniser.yawl.yawl2cpf.patterns.BasePatternTest;
import org.junit.Test;

public class RoundRobinAllocationTest extends BasePatternTest {

    @Override
    protected File getYAWLFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/Patterns/Resource/WPR16RoundRobinAllocation.yawl");
    }

    @Test
    public void testRoundRobin() {

    }

}
