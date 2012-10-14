package org.apromore.canoniser.yawl.cpf2yawl.patterns.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.apromore.canoniser.yawl.BaseCPF2YAWLTest;
import org.apromore.canoniser.yawl.utils.TestUtils;
import org.junit.Test;
import org.yawlfoundation.yawlschema.ExternalTaskFactsType;
import org.yawlfoundation.yawlschema.NetFactsType;

public class RoundRobinAllocationTest extends BaseCPF2YAWLTest {

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.BaseCPF2YAWLTest#getCPFFile()
     */
    @Override
    protected File getCPFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/WPR16RoundRobinAllocation.yawl.cpf");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.BaseCPF2YAWLTest#getANFFile()
     */
    @Override
    protected File getANFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/WPR16RoundRobinAllocation.yawl.anf");
    }

    @Test
    public void testAllocationStrategy() {
        NetFactsType net = findRootNet();

        final ExternalTaskFactsType a = findTaskByName("A", net);
        final ExternalTaskFactsType b = findTaskByName("B", net);
        final ExternalTaskFactsType c = findTaskByName("C", net);

        assertNotNull(a.getResourcing());
        assertNotNull(b.getResourcing());
        assertNotNull(c.getResourcing());

        assertNotNull(a.getResourcing().getAllocate());
        assertNotNull(b.getResourcing().getAllocate());
        assertNotNull(c.getResourcing().getAllocate());

        assertNotNull(a.getResourcing().getAllocate().getAllocator());
        assertNotNull(b.getResourcing().getAllocate().getAllocator());
        assertNotNull(c.getResourcing().getAllocate().getAllocator());

        assertEquals("RoundRobinByTime", a.getResourcing().getAllocate().getAllocator().getName());
        assertEquals("RoundRobinByLeastFrequency", b.getResourcing().getAllocate().getAllocator().getName());
        assertEquals("RoundRobinByExperience", c.getResourcing().getAllocate().getAllocator().getName());
    }
}
