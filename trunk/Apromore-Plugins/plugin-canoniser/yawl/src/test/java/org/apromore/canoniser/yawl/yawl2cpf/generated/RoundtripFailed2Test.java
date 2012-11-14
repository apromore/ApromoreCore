package org.apromore.canoniser.yawl.yawl2cpf.generated;

import static org.junit.Assert.*;

import java.io.File;

import org.apromore.canoniser.yawl.BaseYAWL2CPFTest;
import org.apromore.canoniser.yawl.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;

public class RoundtripFailed2Test extends BaseYAWL2CPFTest {

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.yawl.BaseYAWL2CPFTest#getYAWLFile()
     */
    @Override
    protected File getYAWLFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/Roundtrip/and.cpf.yawl");
    }
}
