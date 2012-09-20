package org.apromore.canoniser.yawl.yawl2cpf.reallife;

import java.io.File;

import org.apromore.canoniser.yawl.BaseYAWL2CPFTest;
import org.apromore.canoniser.yawl.utils.TestUtils;
import org.junit.Ignore;

@Ignore
public class CreditRatingProcessTest extends BaseYAWL2CPFTest {

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.yawl.BaseYAWL2CPFTest#getYAWLFile()
     */
    @Override
    protected File getYAWLFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/CreditRatingProcess.yawl");
    }

}
