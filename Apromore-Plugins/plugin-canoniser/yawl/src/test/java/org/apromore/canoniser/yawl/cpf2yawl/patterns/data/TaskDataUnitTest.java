package org.apromore.canoniser.yawl.cpf2yawl.patterns.data;

import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.apromore.canoniser.yawl.BaseCPF2YAWLUnitTest;
import org.apromore.canoniser.yawl.utils.TestUtils;
import org.junit.Test;
import org.yawlfoundation.yawlschema.ExternalTaskFactsType;
import org.yawlfoundation.yawlschema.NetFactsType;

public class TaskDataUnitTest extends BaseCPF2YAWLUnitTest {

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.BaseCPF2YAWLUnitTest#getCPFFile()
     */
    @Override
    protected File getCPFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/WPD1TaskData.yawl.cpf");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.BaseCPF2YAWLUnitTest#getANFFile()
     */
    @Override
    protected File getANFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/WPD1TaskData.yawl.anf");
    }

    @Test
    public void testNetVariables() {
        NetFactsType net = findRootNet();
        checkInputParameter("n1", "boolean", net);
        checkOutputParameter("n2", "string", net);
        checkInputParameter("n3", "byte", net);
        checkOutputParameter("n3", "byte", net);
        checkLocalVariable("n4", "string", net);
        checkLocalVariable("n5", "YDocumentType", net);
    }

    @Test
    public void testTaskMappings() {
        ExternalTaskFactsType taskA = findTaskByName("A", findRootNet());
        assertNotNull(taskA);

        checkInputMapping("t1", "<t1>{/N-Net/n1/text()}</t1>", taskA);
        checkInputMapping("t2", "<t2>{/N-Net/n1/text()}</t2>", taskA);
        checkInputMapping("t3", "<t3>{/N-Net/n3/text()}</t3>", taskA);

        checkOutputMapping("n3", "<n3>{/C-A/t3/text()}</n3>", taskA);
        checkOutputMapping("n2", "<n2>{/C-A/t3/text()}</n2>", taskA);
        checkOutputMapping("n4", "<n4>{/C-A/t4/text()}</n4>", taskA);
    }


}
