package org.apromore.canoniser.yawl.cpf2yawl.patterns.controlflow;

import java.io.File;

import org.apromore.canoniser.yawl.BaseCPF2YAWLUnitTest;
import org.apromore.canoniser.yawl.utils.TestUtils;

public class AutomatedTimerTaskUnitTest extends BaseCPF2YAWLUnitTest {

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.yawl.BaseCPF2YAWLUnitTest#getCPFFile()
     */
    @Override
    protected File getCPFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/AutomatedTimerTask.yawl.cpf");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.yawl.BaseCPF2YAWLUnitTest#getANFFile()
     */
    @Override
    protected File getANFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/AutomatedTimerTask.yawl.anf");
    }

}
