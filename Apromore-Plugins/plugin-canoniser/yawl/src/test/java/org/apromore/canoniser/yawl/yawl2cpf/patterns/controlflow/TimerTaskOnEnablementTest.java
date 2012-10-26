package org.apromore.canoniser.yawl.yawl2cpf.patterns.controlflow;

import java.io.File;

import org.apromore.canoniser.yawl.utils.TestUtils;
import org.apromore.canoniser.yawl.yawl2cpf.patterns.BasePatternTest;

public class TimerTaskOnEnablementTest extends BasePatternTest {

    @Override
    protected File getYAWLFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/Others/TimerTaskOnEnablement.yawl");
    }



}
