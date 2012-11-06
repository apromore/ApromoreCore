package org.apromore.canoniser.yawl.cpf2yawl.external;

import java.io.File;

import org.apromore.canoniser.yawl.BaseCPF2YAWLTest;
import org.apromore.canoniser.yawl.utils.TestUtils;


public class YAWL4FilmCpfFromApromoreManualTest extends BaseCPF2YAWLTest {

    @Override
    protected File getCPFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/YAWL4Film_Process.cpf");
    }

    @Override
    protected File getANFFile() {
        return null;
    }

}
