package org.apromore.canoniser.yawl.cpf2yawl.patterns.controlflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.apromore.canoniser.yawl.BaseCPF2YAWLUnitTest;
import org.apromore.canoniser.yawl.utils.TestUtils;
import org.junit.Test;
import org.yawlfoundation.yawlschema.ExternalConditionFactsType;
import org.yawlfoundation.yawlschema.ExternalTaskFactsType;
import org.yawlfoundation.yawlschema.NetFactsType;

public class DeferredChoiceUnitTest extends BaseCPF2YAWLUnitTest {

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.BaseCPF2YAWLUnitTest#getCPFFile()
     */
    @Override
    protected File getCPFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/WPC16DeferredChoice.yawl.cpf");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.BaseCPF2YAWLUnitTest#getANFFile()
     */
    @Override
    protected File getANFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/WPC16DeferredChoice.yawl.anf");
    }

    @Test
    public void testStructure() {
        final NetFactsType rootNet = findRootNet();
        assertNotNull(rootNet.getProcessControlElements().getInputCondition());
        final ExternalTaskFactsType a = findTaskByName("A", rootNet);
        assertNotNull("Could not find Task with Name A", a);
        ExternalConditionFactsType b = findConditonByName("B", rootNet);
        assertNotNull("Could not find Condition with Name B", b);
        assertEquals(2, b.getFlowsInto().size());
        final ExternalTaskFactsType c = findTaskByName("C", rootNet);
        assertNotNull("Could not find Task with Name C", c);
        final ExternalTaskFactsType d = findTaskByName("D", rootNet);
        assertNotNull("Could not find Task with Name D", d);
    }

}
