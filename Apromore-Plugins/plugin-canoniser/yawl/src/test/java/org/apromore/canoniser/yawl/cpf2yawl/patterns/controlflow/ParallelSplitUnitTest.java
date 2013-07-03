package org.apromore.canoniser.yawl.cpf2yawl.patterns.controlflow;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apromore.canoniser.yawl.BaseCPF2YAWLUnitTest;
import org.apromore.canoniser.yawl.utils.TestUtils;
import org.junit.Test;
import org.yawlfoundation.yawlschema.ControlTypeCodeType;
import org.yawlfoundation.yawlschema.ExternalTaskFactsType;
import org.yawlfoundation.yawlschema.NetFactsType;

public class ParallelSplitUnitTest extends BaseCPF2YAWLUnitTest {

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.yawl.BaseCPF2YAWLUnitTest#getCPFFile()
     */
    @Override
    protected File getCPFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/WPC2ParallelSplit.yawl.cpf");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.yawl.BaseCPF2YAWLUnitTest#getANFFile()
     */
    @Override
    protected File getANFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/WPC2ParallelSplit.yawl.anf");
    }

    @Test
    public void testStructure() {
        final NetFactsType rootNet = findRootNet();
        final ExternalTaskFactsType a = findTaskByName("A", rootNet);
        assertNotNull("Could not find Task with Name A", a);
        assertTrue(a.getSplit().getCode().equals(ControlTypeCodeType.AND));
        final ExternalTaskFactsType b = findTaskByName("B", rootNet);
        assertNotNull("Could not find Task with Name B", b);
        final ExternalTaskFactsType c = findTaskByName("C", rootNet);
        assertNotNull("Could not find Task with Name C", c);
        final ExternalTaskFactsType d = findTaskByName("D", rootNet);
        assertNotNull("Could not find Task with Name D", d);
        final ExternalTaskFactsType e = findTaskByName("E", rootNet);
        assertNotNull("Could not find Task with Name E", e);
        assertTrue(e.getJoin().getCode().equals(ControlTypeCodeType.AND));
    }

}
