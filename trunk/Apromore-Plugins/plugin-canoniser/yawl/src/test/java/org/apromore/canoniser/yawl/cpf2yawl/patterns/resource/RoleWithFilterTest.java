package org.apromore.canoniser.yawl.cpf2yawl.patterns.resource;

import static org.junit.Assert.fail;

import java.io.File;

import org.apromore.canoniser.yawl.BaseCPF2YAWLTest;
import org.apromore.canoniser.yawl.utils.TestUtils;
import org.junit.Ignore;
import org.junit.Test;

public class RoleWithFilterTest extends BaseCPF2YAWLTest {

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.BaseCPF2YAWLTest#getCPFFile()
     */
    @Override
    protected File getCPFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/RoleWithFilter.yawl.cpf");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.BaseCPF2YAWLTest#getANFFile()
     */
    @Override
    protected File getANFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/RoleWithFilter.yawl.anf");
    }

    @Ignore
    @Test
    public void testWithFilter() {
        fail();
    }

}
