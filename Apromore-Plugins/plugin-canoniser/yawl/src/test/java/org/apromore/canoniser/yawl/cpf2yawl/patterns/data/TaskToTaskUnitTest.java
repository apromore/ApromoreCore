package org.apromore.canoniser.yawl.cpf2yawl.patterns.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apromore.canoniser.yawl.BaseCPF2YAWLUnitTest;
import org.apromore.canoniser.yawl.utils.TestUtils;
import org.junit.Test;
import org.yawlfoundation.yawlschema.ExternalTaskFactsType;
import org.yawlfoundation.yawlschema.NetFactsType;
import org.yawlfoundation.yawlschema.WebServiceGatewayFactsType;

public class TaskToTaskUnitTest extends BaseCPF2YAWLUnitTest {

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.BaseCPF2YAWLUnitTest#getCPFFile()
     */
    @Override
    protected File getCPFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/WPD9TaskToTask.yawl.cpf");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.BaseCPF2YAWLUnitTest#getANFFile()
     */
    @Override
    protected File getANFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/WPD9TaskToTask.yawl.anf");
    }

    @Test
    public void testNetVariables() {
        NetFactsType net = findRootNet();
        checkLocalVariable("x", "boolean", net);
        assertTrue(net.getOutputParam().isEmpty());
        assertTrue(net.getInputParam().isEmpty());
        assertEquals(1, net.getLocalVariable().size());
    }

    @Test
    public void testTaskMappings() {
        ExternalTaskFactsType taskA = findTaskByName("A", findRootNet());
        ExternalTaskFactsType taskB = findTaskByName("B", findRootNet());

        assertNotNull(taskA);
        assertNotNull(taskB);

        assertNull(taskA.getStartingMappings());
        checkOutputMapping("x", "<x>Boolean({/C-A/x/text()})</x>", taskA);

        assertNull(taskB.getCompletedMappings());
        checkInputMapping("y", "<y>{/N-Net/x}</y>", taskB);
    }

    @Test
    public void testTaskVariables() {
        WebServiceGatewayFactsType dA = findDecomposition(findTaskByName("A", findRootNet()));
        WebServiceGatewayFactsType dB = findDecomposition(findTaskByName("B", findRootNet()));

        assertNotNull(dA);
        assertNotNull(dB);

        assertEquals(1, dA.getOutputParam().size());
        assertTrue(dA.getInputParam().isEmpty());
        checkOutputParameter("x", "string", dA);

        assertEquals(1, dB.getInputParam().size());
        assertTrue(dB.getOutputParam().isEmpty());
        checkInputParameter("y", "boolean", dB);
    }


}
