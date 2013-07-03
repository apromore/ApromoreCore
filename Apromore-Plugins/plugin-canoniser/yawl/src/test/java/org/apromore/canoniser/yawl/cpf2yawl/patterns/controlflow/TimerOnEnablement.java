package org.apromore.canoniser.yawl.cpf2yawl.patterns.controlflow;

import java.io.File;

import org.apromore.canoniser.yawl.BaseCPF2YAWLUnitTest;
import org.apromore.canoniser.yawl.utils.TestUtils;

public class TimerOnEnablement extends BaseCPF2YAWLUnitTest {
    
    public TimerOnEnablement() {
        super();
    }

    @Override
    protected File getCPFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/TimerTaskOnEnablement.yawl.cpf");
    }

    @Override
    protected File getANFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/TimerTaskOnEnablement.yawl.anf");
    }
    
}
