package org.apromore.canoniser.yawl.cpf2yawl.reallife;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.apromore.canoniser.yawl.BaseCPF2YAWLTest;
import org.apromore.canoniser.yawl.utils.TestUtils;
import org.junit.Test;
import org.yawlfoundation.yawlschema.NetFactsType;

public class CreditApplicationProcessTest extends BaseCPF2YAWLTest {

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.BaseCPF2YAWLTest#getCPFFile()
     */
    @Override
    protected File getCPFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/CreditApplicationProcess.yawl.cpf");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.BaseCPF2YAWLTest#getANFFile()
     */
    @Override
    protected File getANFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/CreditApplicationProcess.yawl.anf");
    }


    @Test
    public void testLayoutLocale() {
        assertEquals("AU", canonical2Yawl.getYAWL().getLayout().getLocale().getCountry());
        assertEquals("en", canonical2Yawl.getYAWL().getLayout().getLocale().getLanguage());
    }

    @Test
    public void testStructure() {
        NetFactsType rootNet = findRootNet();
        assertNotNull(rootNet);
        assertNotNull(findTaskByName("receive application", rootNet));
        assertNotNull(findTaskByName("get more info", rootNet));
        assertNotNull(findTaskByName("check for completeness", rootNet));
    }

}
