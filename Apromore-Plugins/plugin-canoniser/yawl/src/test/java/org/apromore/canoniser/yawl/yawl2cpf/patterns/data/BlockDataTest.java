package org.apromore.canoniser.yawl.yawl2cpf.patterns.data;

import java.io.File;

import org.apromore.canoniser.yawl.utils.TestUtils;
import org.apromore.canoniser.yawl.yawl2cpf.patterns.BasePatternTest;
import org.junit.Test;

public class BlockDataTest extends BasePatternTest {

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.BaseYAWL2CPFTest#getYAWLFile()
     */
    @Override
    protected File getYAWLFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/Patterns/Data/WPD2BlockData.yawl");
    }

    @Test
    public void testBlockData() {
        
    }

}
