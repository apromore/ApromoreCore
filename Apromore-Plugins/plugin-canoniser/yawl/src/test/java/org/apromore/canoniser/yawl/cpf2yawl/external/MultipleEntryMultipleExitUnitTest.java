package org.apromore.canoniser.yawl.cpf2yawl.external;

import java.io.File;

import org.apromore.canoniser.yawl.BaseCPF2YAWLUnitTest;
import org.apromore.canoniser.yawl.utils.TestUtils;

public class MultipleEntryMultipleExitUnitTest extends BaseCPF2YAWLUnitTest {

    @Override
    protected File getCPFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/MultipleEntryMultipleExit.cpf");
    }

    @Override
    protected File getANFFile() {
        return null;
    }

}
