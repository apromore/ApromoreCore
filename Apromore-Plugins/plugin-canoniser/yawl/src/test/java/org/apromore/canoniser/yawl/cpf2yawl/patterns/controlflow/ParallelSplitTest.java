package org.apromore.canoniser.yawl.cpf2yawl.patterns.controlflow;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apromore.canoniser.yawl.BaseCPF2YAWLTest;
import org.apromore.canoniser.yawl.utils.TestUtils;
import org.junit.Test;
import org.yawlfoundation.yawlschema.ControlTypeCodeType;
import org.yawlfoundation.yawlschema.ExternalTaskFactsType;
import org.yawlfoundation.yawlschema.NetFactsType;

public class ParallelSplitTest extends BaseCPF2YAWLTest {

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.yawl.BaseCPF2YAWLTest#getCPFFile()
     */
    @Override
    protected File getCPFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/WPC2ParallelSplit.yawl.cpf");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.yawl.BaseCPF2YAWLTest#getANFFile()
     */
    @Override
    protected File getANFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/WPC2ParallelSplit.yawl.anf");
    }

    @Test
    public void testStructure() {
        final NetFactsType rootNet = findRootNet();
        final ExternalTaskFactsType a = findTaskByName("A", rootNet);
        assertNotNull(a);
        assertTrue(a.getSplit().getCode().equals(ControlTypeCodeType.AND));
        final ExternalTaskFactsType b = findTaskByName("B", rootNet);
        assertNotNull(b);
        final ExternalTaskFactsType c = findTaskByName("C", rootNet);
        assertNotNull(c);
        final ExternalTaskFactsType d = findTaskByName("D", rootNet);
        assertNotNull(d);
        final ExternalTaskFactsType e = findTaskByName("E", rootNet);
        assertNotNull(e);
        assertTrue(e.getJoin().getCode().equals(ControlTypeCodeType.AND));
    }

}
