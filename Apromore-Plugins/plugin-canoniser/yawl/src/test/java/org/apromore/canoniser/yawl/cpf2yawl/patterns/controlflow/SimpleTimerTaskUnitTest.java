package org.apromore.canoniser.yawl.cpf2yawl.patterns.controlflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.apromore.canoniser.yawl.BaseCPF2YAWLUnitTest;
import org.apromore.canoniser.yawl.utils.TestUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.yawlfoundation.yawlschema.ExternalTaskFactsType;
import org.yawlfoundation.yawlschema.NetFactsType;
import org.yawlfoundation.yawlschema.TimerTriggerType;

@Ignore
public class SimpleTimerTaskUnitTest extends BaseCPF2YAWLUnitTest {

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.BaseCPF2YAWLUnitTest#getCPFFile()
     */
    @Override
    protected File getCPFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/SimpleTimerTask.yawl.cpf");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.BaseCPF2YAWLUnitTest#getANFFile()
     */
    @Override
    protected File getANFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/SimpleTimerTask.yawl.anf");
    }

    @Ignore
    @Test
    public void testTimerOnEnablement() {
        NetFactsType net = findRootNet();
        ExternalTaskFactsType taskB = findTaskByName("B", net);
        assertNotNull(taskB);
        assertEquals("OnEnablement Timer excpected on Task B",TimerTriggerType.ON_ENABLED, taskB.getTimer().getTrigger());
    }

    @Test
    public void testTimerOnStart() {
        NetFactsType net = findRootNet();
        ExternalTaskFactsType taskA = findTaskByName("A", net);
        assertNotNull(taskA);
        assertEquals("OnEnablement Timer excpected on Task A",TimerTriggerType.ON_EXECUTING, taskA.getTimer().getTrigger());
    }

}
