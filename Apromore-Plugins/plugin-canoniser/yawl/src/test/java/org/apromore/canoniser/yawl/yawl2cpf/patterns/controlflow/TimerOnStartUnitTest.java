package org.apromore.canoniser.yawl.yawl2cpf.patterns.controlflow;

import java.io.File;

import org.apromore.canoniser.yawl.utils.TestUtils;
import org.apromore.canoniser.yawl.yawl2cpf.patterns.BasePatternUnitTest;

public class TimerOnStartUnitTest extends BasePatternUnitTest {

    /* (non-Javadoc)
     * @see org.apromore.canoniser.yawl.BaseYAWL2CPFUnitTest#getYAWLFile()
     */
    @Override
    protected File getYAWLFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/Others/TimerTaskOnStart.yawl");
    }

}
