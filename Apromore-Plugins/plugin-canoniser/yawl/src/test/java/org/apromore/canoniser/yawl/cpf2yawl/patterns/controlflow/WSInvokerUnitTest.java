package org.apromore.canoniser.yawl.cpf2yawl.patterns.controlflow;

import java.io.File;

import org.apromore.canoniser.yawl.BaseCPF2YAWLUnitTest;
import org.apromore.canoniser.yawl.utils.TestUtils;

public class WSInvokerUnitTest extends BaseCPF2YAWLUnitTest {

    @Override
    protected File getCPFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/WSInvokerTest.yawl.cpf");
    }

    @Override
    protected File getANFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/WSInvokerTest.yawl.anf");
    }

}
