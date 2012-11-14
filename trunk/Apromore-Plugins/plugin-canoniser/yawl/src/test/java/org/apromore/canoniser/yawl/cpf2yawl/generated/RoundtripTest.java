package org.apromore.canoniser.yawl.cpf2yawl.generated;

import static org.junit.Assert.*;

import java.io.File;

import org.apromore.canoniser.yawl.BaseCPF2YAWLTest;
import org.apromore.canoniser.yawl.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;

public class RoundtripTest extends BaseCPF2YAWLTest {

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.yawl.BaseCPF2YAWLTest#getCPFFile()
     */
    @Override
    protected File getCPFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/13_AndSplitJoin.cpf.yawl.cpf");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.yawl.BaseCPF2YAWLTest#getANFFile()
     */
    @Override
    protected File getANFFile() {
        return null;
    }


}
